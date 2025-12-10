package dk.mosberg.entomology.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dk.mosberg.entomology.EntomologyMod;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages mod configuration with hot-reload support.
 * Configuration is hierarchical: default -> file -> runtime overrides
 *
 * Thread Safety: All methods are synchronized for thread-safe access
 */
public class ConfigManager {
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private static ConfigManager instance;

  private final Path configDir;
  private final Map<String, JsonObject> configs = new HashMap<>();
  private final Map<String, List<ConfigListener>> listeners = new HashMap<>();

  private ConfigManager(Path configDir) {
    this.configDir = configDir;
  }

  public static synchronized ConfigManager getInstance() {
    if (instance == null) {
      Path configPath = Paths.get("config", EntomologyMod.MODID);
      instance = new ConfigManager(configPath);
      instance.initialize();
    }
    return instance;
  }

  private void initialize() {
    try {
      Files.createDirectories(configDir);
      loadAllConfigs();
    } catch (IOException e) {
      EntomologyMod.LOGGER.error("Failed to initialize config directory", e);
    }
  }

  /**
   * Loads all configuration files from disk.
   */
  public synchronized void loadAllConfigs() {
    configs.clear();

    try (DirectoryStream<Path> stream = Files.newDirectoryStream(configDir, "*.json")) {
      for (Path file : stream) {
        String name = file.getFileName().toString().replace(".json", "");
        loadConfig(name);
      }
    } catch (IOException e) {
      EntomologyMod.LOGGER.error("Failed to load configs", e);
    }
  }

  /**
   * Loads a specific configuration file.
   * Creates default if it doesn't exist.
   *
   * @param name config name without .json extension
   * @return the loaded configuration
   */
  public synchronized JsonObject loadConfig(String name) {
    Path file = configDir.resolve(name + ".json");

    try {
      if (!Files.exists(file)) {
        JsonObject defaults = getDefaultConfig(name);
        saveConfig(name, defaults);
        return defaults;
      }

      try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
        JsonObject config = GSON.fromJson(reader, JsonObject.class);
        configs.put(name, config);
        notifyListeners(name, config);
        return config;
      }
    } catch (IOException | JsonParseException e) {
      EntomologyMod.LOGGER.error("Failed to load config: " + name, e);
      return new JsonObject();
    }
  }

  /**
   * Saves a configuration to disk.
   *
   * @param name   config name
   * @param config config data
   */
  public synchronized void saveConfig(String name, JsonObject config) {
    Path file = configDir.resolve(name + ".json");

    try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
      GSON.toJson(config, writer);
      configs.put(name, config);
    } catch (IOException e) {
      EntomologyMod.LOGGER.error("Failed to save config: " + name, e);
    }
  }

  /**
   * Gets a configuration value.
   *
   * @param configName   config file name
   * @param path         dot-separated path (e.g., "mechanics.breeding.enabled")
   * @param defaultValue default if not found
   * @return the value or default
   */
  public synchronized <T> T get(String configName, String path, T defaultValue) {
    JsonObject config = configs.get(configName);
    if (config == null) {
      config = loadConfig(configName);
    }

    String[] parts = path.split("\\.");
    JsonElement current = config;

    for (String part : parts) {
      if (current == null || !current.isJsonObject()) {
        return defaultValue;
      }
      current = current.getAsJsonObject().get(part);
    }

    if (current == null) {
      return defaultValue;
    }

    try {
      // Type-safe casting based on runtime type checking
      @SuppressWarnings("unchecked")
      T result = (T) switch (defaultValue) {
        case Boolean b -> Boolean.valueOf(current.getAsBoolean());
        case Integer i -> Integer.valueOf(current.getAsInt());
        case Double d -> Double.valueOf(current.getAsDouble());
        case String s -> current.getAsString();
        default -> defaultValue;
      };
      return result;
    } catch (Exception e) {
      EntomologyMod.LOGGER.warn("Failed to parse config value at " + path, e);
    }

    return defaultValue;
  }

  /**
   * Sets a configuration value and saves immediately.
   *
   * @param configName config file name
   * @param path       dot-separated path
   * @param value      the value to set
   */
  public synchronized void set(String configName, String path, Object value) {
    JsonObject config = configs.computeIfAbsent(configName, k -> new JsonObject());

    String[] parts = path.split("\\.");
    JsonObject current = config;

    for (int i = 0; i < parts.length - 1; i++) {
      String part = parts[i];
      if (!current.has(part) || !current.get(part).isJsonObject()) {
        current.add(part, new JsonObject());
      }
      current = current.getAsJsonObject(part);
    }

    String lastKey = parts[parts.length - 1];
    if (value instanceof Boolean) {
      current.addProperty(lastKey, (Boolean) value);
    } else if (value instanceof Number) {
      current.addProperty(lastKey, (Number) value);
    } else if (value instanceof String) {
      current.addProperty(lastKey, (String) value);
    } else {
      current.add(lastKey, GSON.toJsonTree(value));
    }

    saveConfig(configName, config);
    notifyListeners(configName, config);
  }

  /**
   * Registers a listener for config changes.
   *
   * @param configName config file to watch
   * @param listener   callback invoked on changes
   */
  public synchronized void addListener(String configName, ConfigListener listener) {
    listeners.computeIfAbsent(configName, k -> new ArrayList<>()).add(listener);
  }

  private void notifyListeners(String configName, JsonObject config) {
    List<ConfigListener> configListeners = listeners.get(configName);
    if (configListeners != null) {
      for (ConfigListener listener : configListeners) {
        try {
          listener.onConfigChanged(config);
        } catch (Exception e) {
          EntomologyMod.LOGGER.error("Config listener error", e);
        }
      }
    }
  }

  private JsonObject getDefaultConfig(String name) {
    JsonObject config = new JsonObject();

    switch (name) {
      case "core":
        JsonObject logging = new JsonObject();
        logging.addProperty("level", "INFO");
        logging.addProperty("logMechanics", true);
        config.add("logging", logging);

        JsonObject performance = new JsonObject();
        performance.addProperty("cacheSize", 1000);
        performance.addProperty("asyncLoading", true);
        config.add("performance", performance);
        break;

      case "mechanics":
        JsonObject breeding = new JsonObject();
        breeding.addProperty("enabled", true);
        breeding.addProperty("baseChance", 0.25);
        breeding.addProperty("mutationChance", 0.05);
        config.add("breeding", breeding);

        JsonObject environment = new JsonObject();
        environment.addProperty("enabled", true);
        environment.addProperty("updateInterval", 100);
        config.add("environment", environment);
        break;

      case "balance":
        config.addProperty("captureSuccessBase", 0.7);
        config.addProperty("captureSuccessPerDamage", -0.1);
        config.addProperty("researchPointsMultiplier", 1.0);
        config.addProperty("bugNetDurability", 128);
        break;

      default:
        // Return empty config for unknown names
        break;
    }
    return config;
  }

  @FunctionalInterface
  public interface ConfigListener {
    void onConfigChanged(JsonObject config);
  }
}
