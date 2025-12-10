package dk.mosberg.entomology.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration values for Entomology mod.
 * Automatically loads from and saves to config/entomology.json
 */
public class EntomologyConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger("entomology-config");
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private static final Path CONFIG_PATH = FabricLoader.getInstance()
      .getConfigDir().resolve("entomology.json");

  public static boolean enableParticles = true;
  public static boolean enableSounds = true;
  public static boolean enableHud = true;
  public static boolean enableBreedingMechanics = true;
  public static boolean enableResearchProgression = true;

  public static double netCaptureChance = 0.8;
  public static int specimenJarMaxAge = 72000; // 1 hour in ticks

  public static boolean debugMode = false;

  /**
   * Load configuration from file or create default if not exists.
   */
  public static void load() {
    try {
      if (Files.exists(CONFIG_PATH)) {
        String json = Files.readString(CONFIG_PATH);
        ConfigData data = GSON.fromJson(json, ConfigData.class);
        if (data != null) {
          applyConfig(data);
        }
        LOGGER.info("Loaded configuration from {}", CONFIG_PATH);
      } else {
        save(); // Create default config file
        LOGGER.info("Created default configuration at {}", CONFIG_PATH);
      }
    } catch (IOException e) {
      LOGGER.error("Failed to load configuration, using defaults", e);
    }
  }

  /**
   * Save current configuration to file.
   */
  public static void save() {
    try {
      ConfigData data = new ConfigData();
      data.enableParticles = enableParticles;
      data.enableSounds = enableSounds;
      data.enableHud = enableHud;
      data.enableBreedingMechanics = enableBreedingMechanics;
      data.enableResearchProgression = enableResearchProgression;
      data.netCaptureChance = netCaptureChance;
      data.specimenJarMaxAge = specimenJarMaxAge;
      data.debugMode = debugMode;

      String json = GSON.toJson(data);
      Files.createDirectories(CONFIG_PATH.getParent());
      Files.writeString(CONFIG_PATH, json);
      LOGGER.debug("Saved configuration to {}", CONFIG_PATH);
    } catch (IOException e) {
      LOGGER.error("Failed to save configuration", e);
    }
  }

  private static void applyConfig(ConfigData data) {
    enableParticles = data.enableParticles;
    enableSounds = data.enableSounds;
    enableHud = data.enableHud;
    enableBreedingMechanics = data.enableBreedingMechanics;
    enableResearchProgression = data.enableResearchProgression;
    netCaptureChance = data.netCaptureChance;
    specimenJarMaxAge = data.specimenJarMaxAge;
    debugMode = data.debugMode;
  }

  /**
   * Internal data class for JSON serialization.
   */
  private static class ConfigData {
    boolean enableParticles = true;
    boolean enableSounds = true;
    boolean enableHud = true;
    boolean enableBreedingMechanics = true;
    boolean enableResearchProgression = true;
    double netCaptureChance = 0.8;
    int specimenJarMaxAge = 72000;
    boolean debugMode = false;
  }
}
