package dk.mosberg.entomology.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.entity.EntityType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import dk.mosberg.entomology.EntomologyMod;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Central registry for all JSON-driven definitions.
 * Loads items, blocks, specimens, and research entries from data packs.
 */
public final class DataDrivenRegistry {
  @SuppressWarnings({ "deprecation", "unused" }) // setLenient required for compatibility
  private static final Gson GSON = new GsonBuilder().setLenient().create();

  public static final Identifier SPECIMEN_RELOADER_ID = EntomologyMod.id("specimen_loader");
  public static final Identifier DEFINITION_RELOADER_ID = EntomologyMod.id("definition_loader");
  public static final Identifier MECHANICS_RELOADER_ID = EntomologyMod.id("mechanics_loader");

  private static final Map<String, ItemDefinition> ITEMS = new HashMap<>();
  private static final Map<String, BlockDefinition> BLOCKS = new HashMap<>();
  private static final Map<String, SpecimenDefinition> SPECIMENS = new HashMap<>();
  private static final Map<String, ResearchEntryDefinition> RESEARCH = new HashMap<>();
  private static BreedingConfigDefinition breedingConfig;
  private static MechanicsConfigDefinition mechanicsConfig;

  private DataDrivenRegistry() {
  }

  public static void bootstrap() {
    // no-op, just triggers class load
  }

  public static ItemDefinition getItem(String id) {
    return ITEMS.get(id);
  }

  public static BlockDefinition getBlock(String id) {
    return BLOCKS.get(id);
  }

  public static SpecimenDefinition getSpecimen(String id) {
    return SPECIMENS.get(id);
  }

  public static Collection<ResearchEntryDefinition> getResearchEntries() {
    return RESEARCH.values();
  }

  public static BreedingConfigDefinition getBreedingConfig() {
    return breedingConfig;
  }

  public static MechanicsConfigDefinition getMechanicsConfig() {
    return mechanicsConfig;
  }

  public static SpecimenDefinition getSpecimenByEntityType(EntityType<?> type) {
    @SuppressWarnings("deprecation") // getRegistryEntry required for dynamic entity type lookup
    var key = type.getRegistryEntry().registryKey().getValue();
    for (SpecimenDefinition def : SPECIMENS.values()) {
      if (def.entityType().equals(key)) {
        return def;
      }
    }
    return null;
  }

  /**
   * Reloader for specimens and research entries.
   */
  @SuppressWarnings("deprecation") // SimpleSynchronousResourceReloadListener still in use
  public static final class SpecimenReloader implements SimpleSynchronousResourceReloadListener {
    @Override
    public Identifier getFabricId() {
      return SPECIMEN_RELOADER_ID;
    }

    @Override
    public void reload(ResourceManager manager) {
      SPECIMENS.clear();
      RESEARCH.clear();

      loadJsonDirectory(manager, "specimens", obj -> {
        String id = requireString(obj, "id");
        String entity = requireString(obj, "entity_type");
        String nameKey = requireString(obj, "display_name_key");
        String descKey = requireString(obj, "description_key");

        // Optional fields with defaults
        String rarity = obj.has("rarity") ? obj.get("rarity").getAsString() : "common";
        double size = obj.has("size") ? obj.get("size").getAsDouble() : 1.0;
        int expValue = obj.has("experience_value") ? obj.get("experience_value").getAsInt() : 5;
        boolean canBreed = !obj.has("can_breed") || obj.get("can_breed").getAsBoolean();

        SpecimenDefinition def = new SpecimenDefinition(
            id,
            Identifier.of(entity),
            nameKey,
            descKey,
            rarity,
            size,
            expValue,
            canBreed);
        SPECIMENS.put(id, def);
      });

      loadJsonDirectory(manager, "research", obj -> {
        String id = requireString(obj, "id");
        String specimenId = requireString(obj, "specimen_id");
        String pageKey = requireString(obj, "page_key");
        if (!SPECIMENS.containsKey(specimenId)) {
          EntomologyMod.LOGGER.warn("Research '{}' refers to unknown specimen '{}'", id, specimenId);
          return;
        }
        RESEARCH.put(id, new ResearchEntryDefinition(id, specimenId, pageKey));
      });

      EntomologyMod.LOGGER.info("Loaded {} specimens and {} research entries",
          SPECIMENS.size(), RESEARCH.size());
    }
  }

  /**
   * Reloader for item/block definitions.
   */
  @SuppressWarnings("deprecation") // SimpleSynchronousResourceReloadListener still in use
  public static final class DefinitionReloader implements SimpleSynchronousResourceReloadListener {
    @Override
    public Identifier getFabricId() {
      return DEFINITION_RELOADER_ID;
    }

    @Override
    public void reload(ResourceManager manager) {
      ITEMS.clear();
      BLOCKS.clear();

      // Items
      loadJsonDirectory(manager, "items", obj -> {
        String id = requireString(obj, "id");
        String type = requireString(obj, "type");
        int durability = obj.has("durability") ? obj.get("durability").getAsInt() : 0;
        List<Identifier> targets = new ArrayList<>();
        if (obj.has("capture_targets")) {
          JsonArray arr = obj.getAsJsonArray("capture_targets");
          for (JsonElement el : arr) {
            targets.add(Identifier.of(el.getAsString()));
          }
        }

        // schema validation: ensure specific items have expected fields
        if ("bug_net".equals(id)) {
          if (!obj.has("capture_targets")) {
            throw new JsonParseException("bug_net requires capture_targets");
          }
          if (durability <= 0) {
            throw new JsonParseException("bug_net must have positive durability");
          }
        }

        ITEMS.put(id, new ItemDefinition(id, type, targets, durability));
      });

      // Blocks
      loadJsonDirectory(manager, "blocks", obj -> {
        String id = requireString(obj, "id");
        int capacity = obj.has("capacity") ? obj.get("capacity").getAsInt() : 0;
        boolean displayEntity = obj.has("display_entity") && obj.get("display_entity").getAsBoolean();
        List<String> inputs = readStringList(obj, "inputs");
        List<String> outputs = readStringList(obj, "outputs");

        // schema validation for known ids
        if ("specimen_jar".equals(id)) {
          if (capacity <= 0) {
            throw new JsonParseException("specimen_jar capacity must be > 0");
          }
        }
        if ("research_station".equals(id)) {
          if (!inputs.contains("specimen_jar") || !inputs.contains("field_guide")) {
            throw new JsonParseException("research_station inputs must contain specimen_jar and field_guide");
          }
          if (!outputs.contains("research_entry")) {
            throw new JsonParseException("research_station outputs must contain research_entry");
          }
        }

        BLOCKS.put(id, new BlockDefinition(id, capacity, displayEntity, inputs, outputs));
      });

      EntomologyMod.LOGGER.info("Loaded {} items and {} blocks", ITEMS.size(), BLOCKS.size());
    }
  }

  /**
   * Reloader for mechanics configuration files.
   */
  @SuppressWarnings("deprecation") // SimpleSynchronousResourceReloadListener still in use
  public static final class MechanicsReloader implements SimpleSynchronousResourceReloadListener {
    @Override
    public Identifier getFabricId() {
      return MECHANICS_RELOADER_ID;
    }

    @Override
    public void reload(ResourceManager manager) {
      // Load breeding configuration
      loadJsonDirectory(manager, "mechanics", obj -> {
        if (obj.has("breeding_pairs")) {
          breedingConfig = parseBreedingConfig(obj);
          EntomologyMod.LOGGER.info("Loaded breeding config with {} pairs",
              breedingConfig.breedingPairs().size());
        }

        // Load general mechanics config
        if (obj.has("breeding") || obj.has("environment") || obj.has("research")) {
          mechanicsConfig = parseMechanicsConfig(obj);
          EntomologyMod.LOGGER.info("Loaded mechanics config");
        }
      });
    }

    private BreedingConfigDefinition parseBreedingConfig(JsonObject obj) {
      List<BreedingConfigDefinition.BreedingPair> pairs = new ArrayList<>();

      if (obj.has("breeding_pairs")) {
        JsonArray pairsArray = obj.getAsJsonArray("breeding_pairs");
        for (JsonElement element : pairsArray) {
          JsonObject pairObj = element.getAsJsonObject();

          String parent1 = requireString(pairObj, "parent1");
          String parent2 = requireString(pairObj, "parent2");
          double chance = pairObj.has("chance") ? pairObj.get("chance").getAsDouble() : 0.25;

          List<String> offspring = new ArrayList<>();
          if (pairObj.has("offspring")) {
            JsonArray offspringArray = pairObj.getAsJsonArray("offspring");
            for (JsonElement o : offspringArray) {
              offspring.add(o.getAsString());
            }
          }

          List<BreedingConfigDefinition.Mutation> mutations = new ArrayList<>();
          if (pairObj.has("mutations")) {
            JsonArray mutArray = pairObj.getAsJsonArray("mutations");
            for (JsonElement m : mutArray) {
              JsonObject mutObj = m.getAsJsonObject();
              String result = requireString(mutObj, "result");
              double mutChance = mutObj.has("chance") ? mutObj.get("chance").getAsDouble() : 0.05;

              BreedingConfigDefinition.MutationConditions conditions = null;
              if (mutObj.has("conditions")) {
                JsonObject condObj = mutObj.getAsJsonObject("conditions");
                String biome = condObj.has("biome") ? condObj.get("biome").getAsString() : null;
                String moonPhase = condObj.has("moon_phase") ? condObj.get("moon_phase").getAsString() : null;
                String weather = condObj.has("weather") ? condObj.get("weather").getAsString() : null;
                List<String> blocks = readStringList(condObj, "required_blocks_nearby");
                conditions = new BreedingConfigDefinition.MutationConditions(biome, moonPhase, weather, blocks);
              }

              mutations.add(new BreedingConfigDefinition.Mutation(result, mutChance, conditions));
            }
          }

          BreedingConfigDefinition.Requirements requirements = null;
          if (pairObj.has("requirements")) {
            JsonObject reqObj = pairObj.getAsJsonObject("requirements");
            requirements = new BreedingConfigDefinition.Requirements(
                reqObj.has("min_light_level") ? reqObj.get("min_light_level").getAsInt() : null,
                reqObj.has("max_light_level") ? reqObj.get("max_light_level").getAsInt() : null,
                reqObj.has("min_temperature") ? reqObj.get("min_temperature").getAsDouble() : null,
                reqObj.has("max_temperature") ? reqObj.get("max_temperature").getAsDouble() : null,
                reqObj.has("underground") ? reqObj.get("underground").getAsBoolean() : null,
                reqObj.has("requires_water_nearby") ? reqObj.get("requires_water_nearby").getAsBoolean() : null,
                reqObj.has("requires_flowers_nearby") ? reqObj.get("requires_flowers_nearby").getAsBoolean() : null,
                reqObj.has("requires_logs_nearby") ? reqObj.get("requires_logs_nearby").getAsBoolean() : null);
          }

          pairs.add(
              new BreedingConfigDefinition.BreedingPair(parent1, parent2, offspring, chance, mutations, requirements));
        }
      }

      return new BreedingConfigDefinition(pairs);
    }

    private MechanicsConfigDefinition parseMechanicsConfig(JsonObject obj) {
      // Parse each section with defaults
      MechanicsConfigDefinition.BreedingConfig breeding = new MechanicsConfigDefinition.BreedingConfig();
      if (obj.has("breeding")) {
        JsonObject breedingObj = obj.getAsJsonObject("breeding");
        breeding = new MechanicsConfigDefinition.BreedingConfig(
            breedingObj.has("enabled") ? breedingObj.get("enabled").getAsBoolean() : true,
            breedingObj.has("base_chance") ? breedingObj.get("base_chance").getAsDouble() : 0.25,
            breedingObj.has("mutation_chance") ? breedingObj.get("mutation_chance").getAsDouble() : 0.05,
            breedingObj.has("cooldown_ticks") ? breedingObj.get("cooldown_ticks").getAsInt() : 6000);
      }

      MechanicsConfigDefinition.EnvironmentConfig environment = new MechanicsConfigDefinition.EnvironmentConfig();
      if (obj.has("environment")) {
        JsonObject envObj = obj.getAsJsonObject("environment");
        environment = new MechanicsConfigDefinition.EnvironmentConfig(
            envObj.has("enabled") ? envObj.get("enabled").getAsBoolean() : true,
            envObj.has("affect_health") ? envObj.get("affect_health").getAsBoolean() : true,
            envObj.has("affect_behavior") ? envObj.get("affect_behavior").getAsBoolean() : true,
            envObj.has("update_interval") ? envObj.get("update_interval").getAsInt() : 100);
      }

      MechanicsConfigDefinition.ResearchConfig research = new MechanicsConfigDefinition.ResearchConfig();
      if (obj.has("research")) {
        JsonObject resObj = obj.getAsJsonObject("research");
        research = new MechanicsConfigDefinition.ResearchConfig(
            resObj.has("enabled") ? resObj.get("enabled").getAsBoolean() : true,
            resObj.has("base_research_time") ? resObj.get("base_research_time").getAsInt() : 200,
            resObj.has("experience_multiplier") ? resObj.get("experience_multiplier").getAsDouble() : 1.0,
            resObj.has("require_field_guide") ? resObj.get("require_field_guide").getAsBoolean() : true);
      }

      MechanicsConfigDefinition.CaptureConfig capture = new MechanicsConfigDefinition.CaptureConfig();
      if (obj.has("capture")) {
        JsonObject capObj = obj.getAsJsonObject("capture");
        capture = new MechanicsConfigDefinition.CaptureConfig(
            capObj.has("enabled") ? capObj.get("enabled").getAsBoolean() : true,
            capObj.has("base_chance") ? capObj.get("base_chance").getAsDouble() : 0.5,
            capObj.has("consume_durability") ? capObj.get("consume_durability").getAsBoolean() : true,
            capObj.has("allow_recapture") ? capObj.get("allow_recapture").getAsBoolean() : false);
      }

      MechanicsConfigDefinition.SpawningConfig spawning = new MechanicsConfigDefinition.SpawningConfig();
      if (obj.has("spawning")) {
        JsonObject spawnObj = obj.getAsJsonObject("spawning");
        spawning = new MechanicsConfigDefinition.SpawningConfig(
            spawnObj.has("enabled") ? spawnObj.get("enabled").getAsBoolean() : true,
            spawnObj.has("spawn_rate_multiplier") ? spawnObj.get("spawn_rate_multiplier").getAsDouble() : 1.0,
            spawnObj.has("respect_biome_requirements") ? spawnObj.get("respect_biome_requirements").getAsBoolean()
                : true,
            spawnObj.has("max_entities_per_chunk") ? spawnObj.get("max_entities_per_chunk").getAsInt() : 10);
      }

      MechanicsConfigDefinition.DisplayConfig display = new MechanicsConfigDefinition.DisplayConfig();
      if (obj.has("display")) {
        JsonObject dispObj = obj.getAsJsonObject("display");
        display = new MechanicsConfigDefinition.DisplayConfig(
            dispObj.has("enabled") ? dispObj.get("enabled").getAsBoolean() : true,
            dispObj.has("show_particles") ? dispObj.get("show_particles").getAsBoolean() : true,
            dispObj.has("show_labels") ? dispObj.get("show_labels").getAsBoolean() : true,
            dispObj.has("entity_scale") ? dispObj.get("entity_scale").getAsDouble() : 1.0);
      }

      MechanicsConfigDefinition.DifficultyConfig difficulty = new MechanicsConfigDefinition.DifficultyConfig();
      if (obj.has("difficulty")) {
        JsonObject diffObj = obj.getAsJsonObject("difficulty");
        String mode = diffObj.has("mode") ? diffObj.get("mode").getAsString() : "normal";

        MechanicsConfigDefinition.DifficultyScaling scaling = new MechanicsConfigDefinition.DifficultyScaling();
        if (diffObj.has("scaling")) {
          JsonObject scaleObj = diffObj.getAsJsonObject("scaling");
          scaling = new MechanicsConfigDefinition.DifficultyScaling(
              scaleObj.has("capture_chance_multiplier") ? scaleObj.get("capture_chance_multiplier").getAsDouble() : 1.0,
              scaleObj.has("breeding_chance_multiplier") ? scaleObj.get("breeding_chance_multiplier").getAsDouble()
                  : 1.0,
              scaleObj.has("mutation_chance_multiplier") ? scaleObj.get("mutation_chance_multiplier").getAsDouble()
                  : 1.0,
              scaleObj.has("research_time_multiplier") ? scaleObj.get("research_time_multiplier").getAsInt() : 1);
        }

        difficulty = new MechanicsConfigDefinition.DifficultyConfig(mode, scaling);
      }

      return new MechanicsConfigDefinition(breeding, environment, research, capture, spawning, display, difficulty);
    }
  }

  private interface JsonConsumer {
    void accept(JsonObject obj);
  }

  private static void loadJsonDirectory(ResourceManager manager, String dir, JsonConsumer consumer) {
    String base = EntomologyMod.MODID + "/" + dir;
    manager.findResources(base, path -> path.getPath().endsWith(".json")).forEach((identifier, resource) -> {
      try (var stream = resource.getInputStream();
          var reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
        JsonElement root = JsonParser.parseReader(reader);
        if (!root.isJsonObject()) {
          throw new JsonParseException("Root is not an object for " + identifier);
        }
        consumer.accept(root.getAsJsonObject());
      } catch (Exception e) {
        EntomologyMod.LOGGER.error("Failed to load JSON {}", identifier, e);
      }
    });
  }

  private static String requireString(JsonObject obj, String key) {
    if (!obj.has(key)) {
      throw new JsonParseException("Missing required field: " + key);
    }
    return obj.get(key).getAsString();
  }

  private static List<String> readStringList(JsonObject obj, String key) {
    List<String> list = new ArrayList<>();
    if (obj.has(key)) {
      JsonArray arr = obj.getAsJsonArray(key);
      for (JsonElement el : arr) {
        list.add(el.getAsString());
      }
    }
    return list;
  }
}
