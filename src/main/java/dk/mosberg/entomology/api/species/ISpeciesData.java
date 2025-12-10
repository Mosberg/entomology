package dk.mosberg.entomology.api.species;

import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Represents complete data for a specimen species.
 * Immutable data object for thread-safe access.
 */
public interface ISpeciesData {
  /**
   * Gets the unique species identifier.
   *
   * @return species identifier
   */
  Identifier getId();

  /**
   * Gets the display name translation key.
   *
   * @return translation key for display name
   */
  String getDisplayNameKey();

  /**
   * Gets the description translation key.
   *
   * @return translation key for description
   */
  String getDescriptionKey();

  /**
   * Gets the rarity tier.
   *
   * @return rarity (common, uncommon, rare, legendary, etc.)
   */
  Rarity getRarity();

  /**
   * Gets biomes where this species can spawn.
   *
   * @return collection of biome identifiers
   */
  Collection<Identifier> getSpawnBiomes();

  /**
   * Gets the base spawn weight.
   *
   * @return spawn weight (higher = more common)
   */
  int getSpawnWeight();

  /**
   * Gets traits for this species.
   *
   * @return map of trait identifiers to trait data
   */
  Map<Identifier, ITraitData> getTraits();

  /**
   * Gets breeding compatibility data.
   *
   * @return breeding data if this species can breed
   */
  Optional<IBreedingData> getBreedingData();

  /**
   * Gets environmental requirements.
   *
   * @return environmental data
   */
  IEnvironmentalData getEnvironmentalData();

  /**
   * Gets custom metadata.
   *
   * @return map of custom key-value pairs
   */
  Map<String, Object> getMetadata();

  /**
   * Checks if this species can spawn in a biome.
   *
   * @param biome biome to check
   * @return true if can spawn
   */
  default boolean canSpawnIn(Biome biome) {
    // Implementation would check biome against spawn biomes
    return false;
  }

  /**
   * Rarity tiers for species.
   */
  enum Rarity {
    COMMON(0, 1.0),
    UNCOMMON(1, 0.5),
    RARE(2, 0.1),
    EPIC(3, 0.05),
    LEGENDARY(4, 0.01);

    private final int tier;
    private final double baseSpawnChance;

    Rarity(int tier, double baseSpawnChance) {
      this.tier = tier;
      this.baseSpawnChance = baseSpawnChance;
    }

    public int getTier() {
      return tier;
    }

    public double getBaseSpawnChance() {
      return baseSpawnChance;
    }
  }
}
