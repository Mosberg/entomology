package dk.mosberg.entomology.mechanics.impl;

import com.google.gson.JsonObject;
import dk.mosberg.entomology.EntomologyMod;
import dk.mosberg.entomology.api.mechanics.IMechanicContext;
import dk.mosberg.entomology.api.mechanics.IMechanicResult;
import dk.mosberg.entomology.mechanics.base.AbstractMechanic;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.*;

/**
 * Advanced environmental mechanic with dynamic environmental factors.
 * Handles temperature, humidity, light, time preferences, and biome
 * compatibility.
 */
public class AdvancedEnvironmentalMechanic extends AbstractMechanic {
  private static final Identifier ID = EntomologyMod.id("advanced_environmental");
  private static final String VERSION = "2.0.0";

  private final Map<String, EnvironmentalRequirements> requirements = new HashMap<>();
  private boolean enabled = true;
  @SuppressWarnings("unused") // Reserved for future tick-based checking
  private int checkInterval = 100;
  private boolean strictMode = false;

  public AdvancedEnvironmentalMechanic() {
    super(ID, VERSION, MechanicCategory.ENVIRONMENTAL, 400);
  }

  @Override
  protected void registerParameters() {
    registerParameter("enabled", "Enable environmental checks", Boolean.class, true, false);
    registerParameter("checkInterval", "Ticks between checks", Integer.class, 100, false);
    registerParameter("strictMode", "Strict requirement enforcement", Boolean.class, false, false);
    registerParameter("specimens", "Specimen environmental requirements", JsonObject.class,
        new JsonObject(), false);
  }

  @Override
  protected void applyConfiguration(JsonObject config) {
    enabled = config.has("enabled") && config.get("enabled").getAsBoolean();
    checkInterval = config.has("checkInterval") ? config.get("checkInterval").getAsInt() : 100;
    strictMode = config.has("strictMode") && config.get("strictMode").getAsBoolean();

    requirements.clear();
    if (config.has("specimens")) {
      JsonObject specimens = config.getAsJsonObject("specimens");
      for (String key : specimens.keySet()) {
        JsonObject specConfig = specimens.getAsJsonObject(key);
        requirements.put(key, parseRequirements(specConfig));
      }
    }

    EntomologyMod.LOGGER.info("Configured environmental requirements for {} specimens",
        requirements.size());
  }

  @Override
  protected IMechanicResult executeInternal(IMechanicContext context) {
    if (!enabled) {
      return IMechanicResult.skip();
    }

    Optional<Identifier> specimenIdOpt = context.getSpecimenId();
    if (specimenIdOpt.isEmpty()) {
      return IMechanicResult.failure("No specimen ID in context");
    }

    String specimenId = specimenIdOpt.get().toString();
    EnvironmentalRequirements reqs = requirements.get(specimenId);
    if (reqs == null) {
      return strictMode
          ? IMechanicResult.failure("No requirements defined")
          : IMechanicResult.skip();
    }

    // Check environmental conditions
    World world = context.getWorld();
    Optional<BlockPos> posOpt = context.getPosition();

    if (posOpt.isEmpty()) {
      return IMechanicResult.skip();
    }

    BlockPos pos = posOpt.get();

    // Check biome
    Biome biome = world.getBiome(pos).value();
    boolean biomeMatch = checkBiomeCompatibility(biome, reqs);

    // Check temperature (from biome)
    float temperature = biome.getTemperature();
    boolean tempMatch = checkTemperature(temperature, reqs);

    // Check light level
    int lightLevel = world.getLightLevel(pos);
    boolean lightMatch = checkLightLevel(lightLevel, reqs);

    // Check time of day
    long time = world.getTimeOfDay() % 24000;
    boolean timeMatch = checkTimeOfDay(time, reqs);

    // Calculate suitability score
    int matches = 0;
    if (biomeMatch) {
      matches++;
    }
    if (tempMatch) {
      matches++;
    }
    if (lightMatch) {
      matches++;
    }
    if (timeMatch) {
      matches++;
    }

    double suitability = matches / 4.0;

    if (strictMode && suitability < 0.75) {
      return IMechanicResult.failure("Environment not suitable (score: " + suitability + ")");
    }

    return IMechanicResult.builder()
        .success()
        .data("suitability", suitability)
        .data("biomeMatch", biomeMatch)
        .data("temperatureMatch", tempMatch)
        .data("lightMatch", lightMatch)
        .data("timeMatch", timeMatch)
        .build();
  }

  @Override
  public boolean appliesTo(IMechanicContext context) {
    return enabled && context.getType() == IMechanicContext.ContextType.ENVIRONMENTAL;
  }

  private EnvironmentalRequirements parseRequirements(JsonObject config) {
    List<String> biomes = new ArrayList<>();
    if (config.has("preferred_biomes")) {
      config.getAsJsonArray("preferred_biomes")
          .forEach(e -> biomes.add(e.getAsString()));
    }

    double tempMin = 0.0, tempMax = 1.0;
    if (config.has("temperature_range")) {
      JsonObject range = config.getAsJsonObject("temperature_range");
      tempMin = range.get("min").getAsDouble();
      tempMax = range.get("max").getAsDouble();
    }

    int lightMin = 0, lightMax = 15;
    if (config.has("light_range")) {
      JsonObject range = config.getAsJsonObject("light_range");
      lightMin = range.get("min").getAsInt();
      lightMax = range.get("max").getAsInt();
    }

    String timePreference = config.has("time_preference")
        ? config.get("time_preference").getAsString()
        : "any";

    return new EnvironmentalRequirements(biomes, tempMin, tempMax,
        lightMin, lightMax, timePreference);
  }

  private boolean checkBiomeCompatibility(Biome biome, EnvironmentalRequirements reqs) {
    if (reqs.preferredBiomes.isEmpty()) {
      return true; // No preference
    }

    // Would need biome registry lookup here
    // For now, accept all
    return true;
  }

  private boolean checkTemperature(float temperature, EnvironmentalRequirements reqs) {
    return temperature >= reqs.tempMin && temperature <= reqs.tempMax;
  }

  private boolean checkLightLevel(int lightLevel, EnvironmentalRequirements reqs) {
    return lightLevel >= reqs.lightMin && lightLevel <= reqs.lightMax;
  }

  private boolean checkTimeOfDay(long time, EnvironmentalRequirements reqs) {
    return switch (reqs.timePreference.toLowerCase()) {
      case "day" -> time >= 0 && time < 12000;
      case "night" -> time >= 12000 && time < 24000;
      case "dusk" -> time >= 11000 && time < 13000;
      case "dawn" -> time >= 23000 || time < 1000;
      default -> true; // "any"
    };
  }

  private static class EnvironmentalRequirements {
    final List<String> preferredBiomes;
    final double tempMin, tempMax;
    final int lightMin, lightMax;
    final String timePreference;

    EnvironmentalRequirements(List<String> biomes, double tempMin, double tempMax,
        int lightMin, int lightMax, String timePreference) {
      this.preferredBiomes = biomes;
      this.tempMin = tempMin;
      this.tempMax = tempMax;
      this.lightMin = lightMin;
      this.lightMax = lightMax;
      this.timePreference = timePreference;
    }
  }
}
