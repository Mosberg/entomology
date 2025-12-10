package dk.mosberg.entomology.data;

/**
 * Research entry definition loaded from JSON.
 */
public record ResearchEntryDefinition(
        String id,
        String specimenId,
        String pageKey) {
}
