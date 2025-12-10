package dk.mosberg.entomology.data;

import net.minecraft.util.Identifier;

import java.util.List;

/**
 * Item definition loaded from JSON.
 */
public record ItemDefinition(
        String id,
        String type,
        List<Identifier> captureTargets,
        int durability) {
}
