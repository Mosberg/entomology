package dk.mosberg.entomology.mechanics;

import com.google.gson.JsonObject;
import dk.mosberg.entomology.EntomologyMod;
import dk.mosberg.entomology.config.ConfigManager;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Mechanic for specimen breeding and mutations.
 * Supports configurable breeding pairs, offspring, and mutation rates.
 *
 * Configuration Schema:
 * {
 * "breeding_pairs": [
 * {
 * "parent1": "specimen_id",
 * "parent2": "specimen_id",
 * "offspring": ["offspring_id"],
 * "chance": 0.25,
 * "mutations": [
 * {"result": "mutation_id", "chance": 0.05}
 * ]
 * }
 * ]
 * }
 */
public class BreedingMechanic implements IMechanic {
  private static final Identifier ID = EntomologyMod.id("breeding");

  private final List<BreedingPair> breedingPairs = new ArrayList<>();
  private boolean enabled = true;
  private double baseChance = 0.25;
  private double mutationChance = 0.05;

  public BreedingMechanic() {
    // Load config values
    ConfigManager config = ConfigManager.getInstance();
    enabled = config.get("mechanics", "breeding.enabled", true);
    baseChance = config.get("mechanics", "breeding.baseChance", 0.25);
    mutationChance = config.get("mechanics", "breeding.mutationChance", 0.05);
  }

  @Override
  public Identifier getId() {
    return ID;
  }

  @Override
  public int getPriority() {
    return 600; // Higher priority for breeding
  }

  @Override
  public boolean appliesTo(String specimenId, MechanicContext context) {
    if (!enabled) {
      return false;
    }

    // Check if this specimen can be a parent
    for (BreedingPair pair : breedingPairs) {
      if (pair.parent1.equals(specimenId) || pair.parent2.equals(specimenId)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public MechanicResult execute(String specimenId, MechanicContext context) {
    // Breeding logic would be implemented here
    // For now, return success
    return MechanicResult.success();
  }

  @Override
  public void configure(JsonObject config) {
    breedingPairs.clear();

    if (config.has("breeding_pairs")) {
      var pairs = config.getAsJsonArray("breeding_pairs");
      for (var element : pairs) {
        JsonObject pairObj = element.getAsJsonObject();

        String parent1 = pairObj.get("parent1").getAsString();
        String parent2 = pairObj.get("parent2").getAsString();
        double chance = pairObj.has("chance") ? pairObj.get("chance").getAsDouble() : baseChance;

        List<String> offspring = new ArrayList<>();
        if (pairObj.has("offspring")) {
          var offspringArray = pairObj.getAsJsonArray("offspring");
          offspringArray.forEach(o -> offspring.add(o.getAsString()));
        }

        List<Mutation> mutations = new ArrayList<>();
        if (pairObj.has("mutations")) {
          var mutationsArray = pairObj.getAsJsonArray("mutations");
          for (var mutElement : mutationsArray) {
            JsonObject mutObj = mutElement.getAsJsonObject();
            String result = mutObj.get("result").getAsString();
            double mutChance = mutObj.has("chance") ? mutObj.get("chance").getAsDouble() : mutationChance;
            mutations.add(new Mutation(result, mutChance));
          }
        }

        breedingPairs.add(new BreedingPair(parent1, parent2, offspring, chance, mutations));
      }
    }

    EntomologyMod.LOGGER.info("Configured {} breeding pairs", breedingPairs.size());
  }

  /**
   * Attempts to breed two specimens.
   *
   * @param specimen1 first parent ID
   * @param specimen2 second parent ID
   * @return optional containing offspring ID if successful
   */
  public Optional<String> attemptBreeding(String specimen1, String specimen2, Random random) {
    for (BreedingPair pair : breedingPairs) {
      if ((pair.parent1.equals(specimen1) && pair.parent2.equals(specimen2))
          || (pair.parent1.equals(specimen2) && pair.parent2.equals(specimen1))) {
        if (random.nextDouble() < pair.chance) {
          // Check for mutations first
          for (Mutation mutation : pair.mutations) {
            if (random.nextDouble() < mutation.chance) {
              return Optional.of(mutation.result);
            }
          }

          // Normal offspring
          if (!pair.offspring.isEmpty()) {
            String offspring = pair.offspring.get(random.nextInt(pair.offspring.size()));
            return Optional.of(offspring);
          }
        }
      }
    }
    return Optional.empty();
  }

  private static class BreedingPair {
    final String parent1;
    final String parent2;
    final List<String> offspring;
    final double chance;
    final List<Mutation> mutations;

    BreedingPair(String parent1, String parent2, List<String> offspring,
        double chance, List<Mutation> mutations) {
      this.parent1 = parent1;
      this.parent2 = parent2;
      this.offspring = offspring;
      this.chance = chance;
      this.mutations = mutations;
    }
  }

  private static class Mutation {
    final String result;
    final double chance;

    Mutation(String result, double chance) {
      this.result = result;
      this.chance = chance;
    }
  }
}
