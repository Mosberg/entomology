package dk.mosberg.entomology.config.advanced;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.mosberg.entomology.EntomologyMod;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Advanced configuration system with JSON schema validation.
 * Supports versioning, migration, and hot-reload.
 */
public class SchemaConfigManager {
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  private final Map<String, ConfigEntry> configs = new ConcurrentHashMap<>();
  private final Map<String, Schema> schemas = new ConcurrentHashMap<>();
  private final Map<String, List<ConfigListener>> listeners = new ConcurrentHashMap<>();
  private final Path configRoot;
  private final Path schemaRoot;

  public SchemaConfigManager(Path configRoot, Path schemaRoot) {
    this.configRoot = configRoot;
    this.schemaRoot = schemaRoot;
  }

  /**
   * Loads a configuration with schema validation.
   */
  public JsonObject loadConfig(String name, String schemaPath) {
    try {
      Path configFile = configRoot.resolve(name + ".json");
      Path schemaFile = schemaRoot.resolve(schemaPath);

      // Load schema
      Schema schema = loadSchema(schemaFile);
      schemas.put(name, schema);

      // Load config
      JsonObject config;
      if (Files.exists(configFile)) {
        try (Reader reader = Files.newBufferedReader(configFile, StandardCharsets.UTF_8)) {
          config = GSON.fromJson(reader, JsonObject.class);
        }
      } else {
        config = new JsonObject();
        config.addProperty("version", "1.0.0");
      }

      // Validate
      ValidationResult result = validateConfig(config, schema);
      if (!result.isValid()) {
        EntomologyMod.LOGGER.error("Configuration validation failed for {}: {}",
            name, result.getErrors());
        // Use defaults or throw
        return getDefaultConfig(name);
      }

      // Check version and migrate if needed
      config = migrateIfNeeded(name, config);

      // Store
      configs.put(name, new ConfigEntry(config, schema));

      // Save if new or migrated
      saveConfig(name, config);

      return config;
    } catch (Exception e) {
      EntomologyMod.LOGGER.error("Failed to load config: " + name, e);
      return new JsonObject();
    }
  }

  /**
   * Validates a configuration against its schema.
   */
  public ValidationResult validateConfig(JsonObject config, Schema schema) {
    try {
      // Convert Gson JsonObject to org.json.JSONObject
      JSONObject jsonObject = new JSONObject(config.toString());
      schema.validate(jsonObject);
      return ValidationResult.success();
    } catch (ValidationException e) {
      List<String> errors = new ArrayList<>();
      errors.add(e.getMessage());
      e.getCausingExceptions().forEach(ex -> errors.add(ex.getMessage()));
      return ValidationResult.error(errors);
    } catch (Exception e) {
      return ValidationResult.error(Collections.singletonList(e.getMessage()));
    }
  }

  /**
   * Saves a configuration to disk.
   */
  public void saveConfig(String name, JsonObject config) {
    try {
      Path configFile = configRoot.resolve(name + ".json");
      Files.createDirectories(configFile.getParent());

      String json = GSON.toJson(config);
      Files.writeString(configFile, json, StandardCharsets.UTF_8);

      configs.put(name, new ConfigEntry(config, schemas.get(name)));
      notifyListeners(name, config);
    } catch (IOException e) {
      EntomologyMod.LOGGER.error("Failed to save config: " + name, e);
    }
  }

  /**
   * Reloads a configuration and notifies listeners.
   */
  public void reloadConfig(String name) {
    try {
      Path configFile = configRoot.resolve(name + ".json");
      if (!Files.exists(configFile)) {
        return;
      }

      try (Reader reader = Files.newBufferedReader(configFile, StandardCharsets.UTF_8)) {
        JsonObject config = GSON.fromJson(reader, JsonObject.class);

        Schema schema = schemas.get(name);
        if (schema != null) {
          ValidationResult result = validateConfig(config, schema);
          if (!result.isValid()) {
            EntomologyMod.LOGGER.error("Reload validation failed for {}: {}",
                name, result.getErrors());
            return;
          }
        }

        configs.put(name, new ConfigEntry(config, schema));
        notifyListeners(name, config);
        EntomologyMod.LOGGER.info("Reloaded configuration: {}", name);
      }
    } catch (Exception e) {
      EntomologyMod.LOGGER.error("Failed to reload config: " + name, e);
    }
  }

  /**
   * Reloads all configurations.
   */
  public void reloadAll() {
    EntomologyMod.LOGGER.info("Reloading all configurations...");
    for (String name : configs.keySet()) {
      reloadConfig(name);
    }
  }

  /**
   * Gets a configuration value with type safety.
   */
  public <T> T get(String configName, String path, Class<T> type, T defaultValue) {
    ConfigEntry entry = configs.get(configName);
    if (entry == null) {
      return defaultValue;
    }

    JsonElement element = getElement(entry.config, path);
    if (element == null || element.isJsonNull()) {
      return defaultValue;
    }

    return deserialize(element, type, defaultValue);
  }

  /**
   * Sets a configuration value.
   */
  public void set(String configName, String path, Object value) {
    ConfigEntry entry = configs.get(configName);
    if (entry == null) {
      EntomologyMod.LOGGER.warn("Config not loaded: {}", configName);
      return;
    }

    setElement(entry.config, path, value);

    // Validate after modification
    if (entry.schema != null) {
      ValidationResult result = validateConfig(entry.config, entry.schema);
      if (!result.isValid()) {
        EntomologyMod.LOGGER.error("Modified config is invalid: {}", result.getErrors());
        return;
      }
    }

    saveConfig(configName, entry.config);
  }

  /**
   * Registers a configuration listener.
   */
  public void addListener(String configName, ConfigListener listener) {
    listeners.computeIfAbsent(configName, k -> new ArrayList<>()).add(listener);
  }

  /**
   * Notifies listeners of configuration changes.
   */
  private void notifyListeners(String configName, JsonObject config) {
    List<ConfigListener> configListeners = listeners.get(configName);
    if (configListeners != null) {
      for (ConfigListener listener : configListeners) {
        try {
          listener.onConfigChanged(config);
        } catch (Exception e) {
          EntomologyMod.LOGGER.error("Listener error for {}", configName, e);
        }
      }
    }
  }

  /**
   * Loads a JSON schema from file.
   */
  private Schema loadSchema(Path schemaFile) throws IOException {
    try (InputStream input = Files.newInputStream(schemaFile)) {
      JSONObject rawSchema = new JSONObject(new JSONTokener(input));
      return SchemaLoader.load(rawSchema);
    }
  }

  /**
   * Gets default configuration.
   */
  private JsonObject getDefaultConfig(String name) {
    JsonObject config = new JsonObject();
    config.addProperty("version", "1.0.0");
    config.addProperty("enabled", true);
    return config;
  }

  /**
   * Migrates configuration if version is outdated.
   */
  private JsonObject migrateIfNeeded(String name, JsonObject config) {
    String currentVersion = config.has("version")
        ? config.get("version").getAsString()
        : "1.0.0";

    // Migration logic would go here
    // Log current version for debugging
    EntomologyMod.LOGGER.debug("Config {} version: {}", name, currentVersion);

    // For now, just ensure version field exists
    if (!config.has("version")) {
      config.addProperty("version", "1.0.0");
    }

    return config;
  }

  /**
   * Gets a nested JSON element by dot-separated path.
   */
  private JsonElement getElement(JsonObject root, String path) {
    String[] parts = path.split("\\.");
    JsonElement current = root;

    for (String part : parts) {
      if (current == null || !current.isJsonObject()) {
        return null;
      }
      current = current.getAsJsonObject().get(part);
    }

    return current;
  }

  /**
   * Sets a nested value by dot-separated path.
   */
  private void setElement(JsonObject root, String path, Object value) {
    String[] parts = path.split("\\.");
    JsonObject current = root;

    for (int i = 0; i < parts.length - 1; i++) {
      String part = parts[i];
      if (!current.has(part) || !current.get(part).isJsonObject()) {
        current.add(part, new JsonObject());
      }
      current = current.getAsJsonObject(part);
    }

    String lastKey = parts[parts.length - 1];
    current.add(lastKey, GSON.toJsonTree(value));
  }

  /**
   * Deserializes a JSON element to a type.
   */
  @SuppressWarnings("unchecked")
  private <T> T deserialize(JsonElement element, Class<T> type, T defaultValue) {
    try {
      if (type == Boolean.class || type == boolean.class) {
        return (T) Boolean.valueOf(element.getAsBoolean());
      } else if (type == Integer.class || type == int.class) {
        return (T) Integer.valueOf(element.getAsInt());
      } else if (type == Double.class || type == double.class) {
        return (T) Double.valueOf(element.getAsDouble());
      } else if (type == String.class) {
        return (T) element.getAsString();
      } else {
        return GSON.fromJson(element, type);
      }
    } catch (Exception e) {
      EntomologyMod.LOGGER.warn("Failed to deserialize config value", e);
      return defaultValue;
    }
  }

  /**
   * Configuration entry with schema.
   */
  private static class ConfigEntry {
    final JsonObject config;
    final Schema schema;

    ConfigEntry(JsonObject config, Schema schema) {
      this.config = config;
      this.schema = schema;
    }
  }

  /**
   * Configuration listener interface.
   */
  @FunctionalInterface
  public interface ConfigListener {
    void onConfigChanged(JsonObject config);
  }

  /**
   * Validation result.
   */
  public static class ValidationResult {
    private final boolean valid;
    private final List<String> errors;
    private final List<String> warnings;

    private ValidationResult(boolean valid, List<String> errors, List<String> warnings) {
      this.valid = valid;
      this.errors = Collections.unmodifiableList(errors);
      this.warnings = Collections.unmodifiableList(warnings);
    }

    public boolean isValid() {
      return valid;
    }

    public List<String> getErrors() {
      return errors;
    }

    public List<String> getWarnings() {
      return warnings;
    }

    public static ValidationResult success() {
      return new ValidationResult(true, Collections.emptyList(), Collections.emptyList());
    }

    public static ValidationResult error(List<String> errors) {
      return new ValidationResult(false, errors, Collections.emptyList());
    }
  }
}
