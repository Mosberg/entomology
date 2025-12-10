package dk.mosberg.entomology.data;

import java.util.List;

/**
 * Block definition loaded from JSON.
 */
public record BlockDefinition(
        String id,
        int capacity,
        boolean displayEntity,
        List<String> inputs,
        List<String> outputs) {
}
