package dk.mosberg.entomology.data;

import net.minecraft.util.Identifier;

/**
 * Specimen definition loaded from JSON.
 * Represents a capturable insect/creature with gameplay properties.
 */
public record SpecimenDefinition(
        String id,
        Identifier entityType,
        String displayNameKey,
        String descriptionKey,
        String rarity, // common, uncommon, rare, epic, legendary
        double size, // relative size multiplier
        int experienceValue, // XP dropped when researched
        boolean canBreed // whether this specimen can breed
) {
    // Default constructor for backward compatibility
    public SpecimenDefinition(String id, Identifier entityType, String displayNameKey, String descriptionKey) {
        this(id, entityType, displayNameKey, descriptionKey, "common", 1.0, 5, true);
    }
}
