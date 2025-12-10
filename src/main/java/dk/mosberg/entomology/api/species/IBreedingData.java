package dk.mosberg.entomology.api.species;

import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

/**
 * Breeding configuration for a species.
 * Defines compatibility, offspring, and mutation rules.
 */
public interface IBreedingData {
  /**
   * Gets species that this species can breed with.
   *
   * @return list of compatible species IDs
   */
  List<Identifier> getCompatibleSpecies();

  /**
   * Gets possible offspring from breeding.
   *
   * @return list of possible offspring configurations
   */
  List<IOffspringConfig> getOffspringConfigs();

  /**
   * Gets the base breeding success chance.
   *
   * @return probability (0.0 - 1.0)
   */
  double getBaseSuccessChance();

  /**
   * Gets the time required for breeding (in ticks).
   *
   * @return breeding time in ticks
   */
  int getBreedingTime();

  /**
   * Gets environmental requirements for breeding.
   *
   * @return map of requirement keys to values
   */
  Map<String, Object> getBreedingRequirements();

  /**
   * Gets mutation configurations.
   *
   * @return list of possible mutations
   */
  List<IMutationConfig> getMutations();

  /**
   * Configuration for possible offspring.
   */
  interface IOffspringConfig {
    /**
     * Gets the offspring species ID.
     *
     * @return species identifier
     */
    Identifier getSpeciesId();

    /**
     * Gets the probability of this offspring.
     *
     * @return probability (0.0 - 1.0)
     */
    double getChance();

    /**
     * Gets trait inheritance rules.
     *
     * @return map of trait IDs to inheritance data
     */
    Map<Identifier, ITraitInheritance> getTraitInheritance();

    /**
     * Gets minimum number of offspring.
     *
     * @return minimum count
     */
    int getMinCount();

    /**
     * Gets maximum number of offspring.
     *
     * @return maximum count
     */
    int getMaxCount();
  }

  /**
   * Configuration for mutations.
   */
  interface IMutationConfig {
    /**
     * Gets the mutated species ID.
     *
     * @return mutated species identifier
     */
    Identifier getMutatedSpeciesId();

    /**
     * Gets the mutation probability.
     *
     * @return probability (0.0 - 1.0)
     */
    double getMutationChance();

    /**
     * Gets conditions required for mutation.
     *
     * @return map of condition keys to values
     */
    Map<String, Object> getMutationConditions();

    /**
     * Gets traits that trigger this mutation.
     *
     * @return list of required trait IDs
     */
    List<Identifier> getTriggerTraits();
  }

  /**
   * Trait inheritance configuration.
   */
  interface ITraitInheritance {
    /**
     * Gets the inheritance mode.
     *
     * @return inheritance mode
     */
    InheritanceMode getMode();

    /**
     * Gets the inheritance chance override.
     *
     * @return probability, or null to use trait default
     */
    Double getChanceOverride();

    /**
     * Inheritance modes.
     */
    enum InheritanceMode {
      /** Always inherit from parent */
      GUARANTEED,
      /** Use trait's default inheritance chance */
      NORMAL,
      /** Never inherit */
      BLOCKED,
      /** Inherit from dominant parent */
      DOMINANT,
      /** Blend from both parents */
      BLENDED
    }
  }
}
