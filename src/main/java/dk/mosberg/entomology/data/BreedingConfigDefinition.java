package dk.mosberg.entomology.data;

import java.util.List;

/**
 * Breeding configuration definition loaded from JSON.
 * Defines breeding pairs, offspring, and mutation rules.
 */
public record BreedingConfigDefinition(
    List<BreedingPair> breedingPairs) {
  /**
   * Represents a breeding pair configuration.
   */
  public record BreedingPair(
      String parent1,
      String parent2,
      List<String> offspring,
      double chance,
      List<Mutation> mutations,
      Requirements requirements) {
    public BreedingPair(String parent1, String parent2, List<String> offspring, double chance) {
      this(parent1, parent2, offspring, chance, List.of(), null);
    }
  }

  /**
   * Mutation that can occur during breeding.
   */
  public record Mutation(
      String result,
      double chance,
      MutationConditions conditions) {
    public Mutation(String result, double chance) {
      this(result, chance, null);
    }
  }

  /**
   * Conditions required for a mutation to occur.
   */
  public record MutationConditions(
      String biome,
      String moonPhase,
      String weather,
      List<String> requiredBlocksNearby) {
  }

  /**
   * Environmental requirements for breeding.
   */
  public record Requirements(
      Integer minLightLevel,
      Integer maxLightLevel,
      Double minTemperature,
      Double maxTemperature,
      Boolean underground,
      Boolean requiresWaterNearby,
      Boolean requiresFlowersNearby,
      Boolean requiresLogsNearby) {
  }
}
