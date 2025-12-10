package dk.mosberg.entomology.api.species;

import net.minecraft.util.Identifier;

import java.util.Map;

/**
 * Represents a trait that a species can have.
 * Traits are inheritable properties that affect behavior and appearance.
 */
public interface ITraitData {
  /**
   * Gets the trait identifier.
   *
   * @return trait ID
   */
  Identifier getId();

  /**
   * Gets the trait name translation key.
   *
   * @return translation key
   */
  String getNameKey();

  /**
   * Gets the trait description translation key.
   *
   * @return translation key
   */
  String getDescriptionKey();

  /**
   * Gets the trait type.
   *
   * @return trait type
   */
  TraitType getType();

  /**
   * Gets the trait level/strength.
   *
   * @return level (typically 1-10)
   */
  int getLevel();

  /**
   * Checks if this trait is inheritable.
   *
   * @return true if can be passed to offspring
   */
  boolean isInheritable();

  /**
   * Gets the inheritance chance (0.0 - 1.0).
   *
   * @return probability of passing to offspring
   */
  double getInheritanceChance();

  /**
   * Gets trait modifiers.
   *
   * @return map of modifier keys to values
   */
  Map<String, Double> getModifiers();

  /**
   * Types of traits.
   */
  enum TraitType {
    /** Affects appearance/visuals */
    COSMETIC,
    /** Affects behavior patterns */
    BEHAVIORAL,
    /** Affects stats/attributes */
    ATTRIBUTE,
    /** Affects rarity/spawning */
    SPAWN,
    /** Special unique trait */
    UNIQUE
  }
}
