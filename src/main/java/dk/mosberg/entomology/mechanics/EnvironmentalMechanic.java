package dk.mosberg.entomology.mechanics;

import com.google.gson.JsonObject;
import dk.mosberg.entomology.EntomologyMod;
import dk.mosberg.entomology.config.ConfigManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mechanic for environmental effects on specimens.
 * Handles habitat preferences, temperature, humidity, and time-of-day effects.
 *
 * Configuration Schema:
 * {
 * "specimens": {
 * "specimen_id": {
 * "preferred_biomes": ["minecraft:plains", "minecraft:forest"],
 * "temperature_range": {"min": 0.5, "max": 1.0},
 * "humidity_range": {"min": 0.3, "max": 0.8},
 * "time_preference": "day" | "night" | "any",
 * "spawn_weight_multiplier": 2.0
 * }
 * }
 * }
 */
public class EnvironmentalMechanic implements IMechanic {
  private static final Identifier ID = EntomologyMod.id("environmental");

  private final Map<String, EnvironmentalPreferences> preferences = new HashMap<>();
  private boolean enabled = true;
  private int updateInterval = 100; // ticks

  public EnvironmentalMechanic() {
    ConfigManager config = ConfigManager.getInstance();
    enabled = config.get("mechanics", "environment.enabled", true);
    updateInterval = config.get("mechanics", "environment.updateInterval", 100);
  }

  /**
   * Gets the update interval in ticks for environmental calculations.
   *
   * @return ticks between environmental updates
   */
  public int getUpdateInterval() {
    return updateInterval;
  }

  @Override
  public Identifier getId() {
    return ID;
  }

  @Override
  public int getPriority() {
    return 400;
  }

  @Override
  public boolean appliesTo(String specimenId, MechanicContext context) {
    return enabled && preferences.containsKey(specimenId);
  }

  @Override
  public MechanicResult execute(String specimenId, MechanicContext context) {
    EnvironmentalPreferences prefs = preferences.get(specimenId);
    if (prefs == null || context.getWorld() == null) {
      return MechanicResult.failure("No preferences or world");
    }

    // Environmental logic would check biome, temperature, time, etc.
    // For now, return success with preference data
    return MechanicResult.success(prefs);
  }

  @Override
  public void configure(JsonObject config) {
    preferences.clear();

    if (config.has("specimens")) {
      JsonObject specimens = config.getAsJsonObject("specimens");

      for (String specimenId : specimens.keySet()) {
        JsonObject specConfig = specimens.getAsJsonObject(specimenId);

        List<String> preferredBiomes = new ArrayList<>();
        if (specConfig.has("preferred_biomes")) {
          var biomes = specConfig.getAsJsonArray("preferred_biomes");
          biomes.forEach(b -> preferredBiomes.add(b.getAsString()));
        }

        double tempMin = 0.0, tempMax = 1.0;
        if (specConfig.has("temperature_range")) {
          JsonObject tempRange = specConfig.getAsJsonObject("temperature_range");
          tempMin = tempRange.get("min").getAsDouble();
          tempMax = tempRange.get("max").getAsDouble();
        }

        double humidMin = 0.0, humidMax = 1.0;
        if (specConfig.has("humidity_range")) {
          JsonObject humidRange = specConfig.getAsJsonObject("humidity_range");
          humidMin = humidRange.get("min").getAsDouble();
          humidMax = humidRange.get("max").getAsDouble();
        }

        TimePreference timePreference = TimePreference.ANY;
        if (specConfig.has("time_preference")) {
          String timePref = specConfig.get("time_preference").getAsString();
          timePreference = TimePreference.valueOf(timePref.toUpperCase());
        }

        double spawnMultiplier = 1.0;
        if (specConfig.has("spawn_weight_multiplier")) {
          spawnMultiplier = specConfig.get("spawn_weight_multiplier").getAsDouble();
        }

        preferences.put(specimenId, new EnvironmentalPreferences(
            preferredBiomes, tempMin, tempMax, humidMin, humidMax,
            timePreference, spawnMultiplier));
      }
    }

    EntomologyMod.LOGGER.info("Configured environmental preferences for {} specimens",
        preferences.size());
  }

  /**
   * Calculates spawn weight multiplier for a specimen in given conditions.
   *
   * @param specimenId  the specimen ID
   * @param biome       the biome
   * @param temperature biome temperature (0.0-2.0)
   * @param humidity    downfall value (0.0-1.0)
   * @param isDaytime   true if daytime
   * @return spawn weight multiplier (1.0 = normal, higher = more common)
   */
  public double getSpawnWeightMultiplier(String specimenId, RegistryKey<Biome> biome,
      double temperature, double humidity, boolean isDaytime) {
    EnvironmentalPreferences prefs = preferences.get(specimenId);
    if (prefs == null) {
      return 1.0;
    }

    double multiplier = 1.0;

    // Check biome preference
    if (!prefs.preferredBiomes.isEmpty()) {
      boolean biomeMatch = prefs.preferredBiomes.contains(biome.getValue().toString());
      multiplier *= biomeMatch ? prefs.spawnMultiplier : 0.5;
    }

    // Check temperature range
    if (temperature < prefs.tempMin || temperature > prefs.tempMax) {
      multiplier *= 0.3;
    }

    // Check humidity range
    if (humidity < prefs.humidMin || humidity > prefs.humidMax) {
      multiplier *= 0.3;
    }

    // Check time preference
    if (prefs.timePreference != TimePreference.ANY) {
      boolean timeMatch = (prefs.timePreference == TimePreference.DAY && isDaytime)
          || (prefs.timePreference == TimePreference.NIGHT && !isDaytime);
      multiplier *= timeMatch ? 1.5 : 0.2;
    }
    return multiplier;
  }

  public EnvironmentalPreferences getPreferences(String specimenId) {
    return preferences.get(specimenId);
  }

  public static class EnvironmentalPreferences {
    public final List<String> preferredBiomes;
    public final double tempMin, tempMax;
    public final double humidMin, humidMax;
    public final TimePreference timePreference;
    public final double spawnMultiplier;

    public EnvironmentalPreferences(List<String> preferredBiomes,
        double tempMin, double tempMax,
        double humidMin, double humidMax,
        TimePreference timePreference,
        double spawnMultiplier) {
      this.preferredBiomes = preferredBiomes;
      this.tempMin = tempMin;
      this.tempMax = tempMax;
      this.humidMin = humidMin;
      this.humidMax = humidMax;
      this.timePreference = timePreference;
      this.spawnMultiplier = spawnMultiplier;
    }
  }

  public enum TimePreference {
    DAY, NIGHT, ANY
  }
}
