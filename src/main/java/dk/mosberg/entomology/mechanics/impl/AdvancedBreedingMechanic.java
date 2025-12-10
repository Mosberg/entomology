package dk.mosberg.entomology.mechanics.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.mosberg.entomology.EntomologyMod;
import dk.mosberg.entomology.api.mechanics.IMechanicContext;
import dk.mosberg.entomology.api.mechanics.IMechanicResult;
import dk.mosberg.entomology.mechanics.base.AbstractMechanic;
import net.minecraft.util.Identifier;

import java.util.*;

/**
 * Advanced breeding mechanic with genetics, mutations, and trait inheritance.
 * Fully data-driven and configurable.
 */
public class AdvancedBreedingMechanic extends AbstractMechanic {
  private static final Identifier ID = EntomologyMod.id("advanced_breeding");
  private static final String VERSION = "2.0.0";

  private final Map<String, BreedingPair> breedingPairs = new HashMap<>();
  private final Map<String, TraitDefinition> traits = new HashMap<>();
  private boolean enabled = true;
  private double globalMutationRate = 0.05;
  private int breedingCooldown = 6000; // ticks

  public AdvancedBreedingMechanic() {
    super(ID, VERSION, MechanicCategory.BREEDING, 600);
  }

  @Override
  protected void registerParameters() {
    registerParameter("enabled", "Enable/disable breeding mechanic", Boolean.class, true, false);
    registerParameter("globalMutationRate", "Global mutation probability", Double.class, 0.05, false);
    registerParameter("breedingCooldown", "Cooldown between breeding attempts (ticks)", Integer.class, 6000, false);
    registerParameter("breedingPairs", "List of valid breeding combinations", JsonArray.class, new JsonArray(), true);
    registerParameter("traits", "Trait definitions", JsonArray.class, new JsonArray(), false);
  }

  @Override
  protected void applyConfiguration(JsonObject config) {
    enabled = config.has("enabled") && config.get("enabled").getAsBoolean();
    globalMutationRate = config.has("globalMutationRate")
        ? config.get("globalMutationRate").getAsDouble()
        : 0.05;
    breedingCooldown = config.has("breedingCooldown")
        ? config.get("breedingCooldown").getAsInt()
        : 6000;

    // Load breeding pairs
    breedingPairs.clear();
    if (config.has("breedingPairs")) {
      JsonArray pairs = config.getAsJsonArray("breedingPairs");
      for (JsonElement element : pairs) {
        JsonObject pairObj = element.getAsJsonObject();
        BreedingPair pair = parseBreedingPair(pairObj);
        String key = generatePairKey(pair.parent1, pair.parent2);
        breedingPairs.put(key, pair);
      }
    }

    // Load traits
    traits.clear();
    if (config.has("traits")) {
      JsonArray traitsArray = config.getAsJsonArray("traits");
      for (JsonElement element : traitsArray) {
        JsonObject traitObj = element.getAsJsonObject();
        TraitDefinition trait = parseTrait(traitObj);
        traits.put(trait.id, trait);
      }
    }

    EntomologyMod.LOGGER.info("Configured advanced breeding: {} pairs, {} traits",
        breedingPairs.size(), traits.size());
  }

  @Override
  protected IMechanicResult executeInternal(IMechanicContext context) {
    if (!enabled) {
      return IMechanicResult.skip();
    }

    // Extract parent specimens from context
    Optional<String> parent1Opt = context.getData("parent1").map(Object::toString);
    Optional<String> parent2Opt = context.getData("parent2").map(Object::toString);

    if (parent1Opt.isEmpty() || parent2Opt.isEmpty()) {
      return IMechanicResult.failure("Missing parent specimens");
    }

    String parent1 = parent1Opt.get();
    String parent2 = parent2Opt.get();

    // Find breeding pair
    String key = generatePairKey(parent1, parent2);
    BreedingPair pair = breedingPairs.get(key);

    if (pair == null) {
      return IMechanicResult.failure("Incompatible breeding pair");
    }

    // Check cooldown
    long lastBreeding = context.getData("lastBreedingTime")
        .map(o -> ((Number) o).longValue())
        .orElse(0L);

    if (context.getGameTime() - lastBreeding < breedingCooldown) {
      return IMechanicResult.failure("Breeding on cooldown");
    }

    // Attempt breeding
    Random random = new Random();
    if (random.nextDouble() >= pair.successChance) {
      return IMechanicResult.failure("Breeding attempt failed");
    }

    // Check for mutation
    boolean mutated = random.nextDouble() < (pair.mutationChance * globalMutationRate);
    String offspring;

    if (mutated && !pair.mutations.isEmpty()) {
      offspring = pair.mutations.get(random.nextInt(pair.mutations.size()));
    } else if (!pair.offspring.isEmpty()) {
      offspring = pair.offspring.get(random.nextInt(pair.offspring.size()));
    } else {
      return IMechanicResult.failure("No valid offspring defined");
    }

    // Inherit traits
    @SuppressWarnings("unchecked")
    Map<String, Object> parent1Traits = context.getData("parent1Traits")
        .map(o -> (Map<String, Object>) o).orElse(Collections.emptyMap());
    @SuppressWarnings("unchecked")
    Map<String, Object> parent2Traits = context.getData("parent2Traits")
        .map(o -> (Map<String, Object>) o).orElse(Collections.emptyMap());

    Map<String, Object> inheritedTraits = calculateTraitInheritance(
        parent1Traits,
        parent2Traits,
        random);

    return IMechanicResult.builder()
        .success()
        .data("offspring", offspring)
        .data("mutated", mutated)
        .data("traits", inheritedTraits)
        .data("breedingTime", context.getGameTime())
        .build();
  }

  @Override
  public boolean appliesTo(IMechanicContext context) {
    return enabled && context.getType() == IMechanicContext.ContextType.BREEDING;
  }

  /**
   * Calculates trait inheritance from parents.
   */
  private Map<String, Object> calculateTraitInheritance(Map<String, Object> parent1Traits,
      Map<String, Object> parent2Traits,
      Random random) {
    Map<String, Object> result = new HashMap<>();

    // Combine parent traits
    Set<String> allTraits = new HashSet<>();
    allTraits.addAll(parent1Traits.keySet());
    allTraits.addAll(parent2Traits.keySet());

    for (String traitId : allTraits) {
      TraitDefinition trait = traits.get(traitId);
      if (trait == null || !trait.inheritable) {
        continue;
      }

      // Random inheritance from parents
      if (random.nextDouble() < trait.inheritanceChance) {
        // Choose parent randomly or based on dominance
        boolean useParent1 = random.nextBoolean();
        Object value = useParent1
            ? parent1Traits.get(traitId)
            : parent2Traits.get(traitId);

        if (value != null) {
          result.put(traitId, value);
        }
      }
    }

    return result;
  }

  /**
   * Parses breeding pair from JSON.
   */
  private BreedingPair parseBreedingPair(JsonObject obj) {
    String parent1 = obj.get("parent1").getAsString();
    String parent2 = obj.get("parent2").getAsString();
    double successChance = obj.has("successChance")
        ? obj.get("successChance").getAsDouble()
        : 0.5;
    double mutationChance = obj.has("mutationChance")
        ? obj.get("mutationChance").getAsDouble()
        : globalMutationRate;

    List<String> offspring = new ArrayList<>();
    if (obj.has("offspring")) {
      JsonArray offspringArray = obj.getAsJsonArray("offspring");
      offspringArray.forEach(e -> offspring.add(e.getAsString()));
    }

    List<String> mutations = new ArrayList<>();
    if (obj.has("mutations")) {
      JsonArray mutationsArray = obj.getAsJsonArray("mutations");
      mutationsArray.forEach(e -> mutations.add(e.getAsString()));
    }

    return new BreedingPair(parent1, parent2, offspring, mutations, successChance, mutationChance);
  }

  /**
   * Parses trait definition from JSON.
   */
  private TraitDefinition parseTrait(JsonObject obj) {
    String id = obj.get("id").getAsString();
    boolean inheritable = obj.has("inheritable") && obj.get("inheritable").getAsBoolean();
    double inheritanceChance = obj.has("inheritanceChance")
        ? obj.get("inheritanceChance").getAsDouble()
        : 0.5;

    return new TraitDefinition(id, inheritable, inheritanceChance);
  }

  /**
   * Generates consistent key for breeding pair.
   */
  private String generatePairKey(String parent1, String parent2) {
    // Sort to ensure consistent ordering
    if (parent1.compareTo(parent2) <= 0) {
      return parent1 + ":" + parent2;
    } else {
      return parent2 + ":" + parent1;
    }
  }

  /**
   * Breeding pair data.
   */
  private static class BreedingPair {
    final String parent1;
    final String parent2;
    final List<String> offspring;
    final List<String> mutations;
    final double successChance;
    final double mutationChance;

    BreedingPair(String parent1, String parent2, List<String> offspring,
        List<String> mutations, double successChance, double mutationChance) {
      this.parent1 = parent1;
      this.parent2 = parent2;
      this.offspring = offspring;
      this.mutations = mutations;
      this.successChance = successChance;
      this.mutationChance = mutationChance;
    }
  }

  /**
   * Trait definition data.
   */
  private static class TraitDefinition {
    final String id;
    final boolean inheritable;
    final double inheritanceChance;

    TraitDefinition(String id, boolean inheritable, double inheritanceChance) {
      this.id = id;
      this.inheritable = inheritable;
      this.inheritanceChance = inheritanceChance;
    }
  }
}
