package dk.mosberg.entomology.schema;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Validates specimen JSON definitions.
 *
 * Required Schema:
 * {
 * "id": "string",
 * "name": "string",
 * "rarity": number (0.0-1.0),
 * "capture_targets": ["entity:id"],
 * "description": "string" (optional)
 * }
 */
public class SpecimenSchemaValidator extends BaseSchemaValidator {

  @Override
  protected void performValidation(JsonObject json) {
    // Required fields
    requireStringField(json, "id");
    requireStringField(json, "name");
    requireNumberInRange(json, "rarity", 0.0, 1.0);

    // Capture targets
    if (!json.has("capture_targets")) {
      errors.add("Missing required field: capture_targets");
    } else if (!json.get("capture_targets").isJsonArray()) {
      errors.add("Field 'capture_targets' must be an array");
    } else {
      JsonArray targets = json.getAsJsonArray("capture_targets");
      if (targets.isEmpty()) {
        warnings.add("No capture targets specified");
      }

      for (int i = 0; i < targets.size(); i++) {
        if (!targets.get(i).isJsonPrimitive()) {
          errors.add("Capture target at index " + i + " must be a string");
        }
      }
    }

    // Optional fields
    warnIfMissing(json, "description");

    // Validate environmental preferences if present
    if (json.has("environmental")) {
      validateEnvironmental(json.getAsJsonObject("environmental"));
    }
  }

  private void validateEnvironmental(JsonObject env) {
    if (env.has("temperature_range")) {
      JsonObject range = env.getAsJsonObject("temperature_range");
      requireNumberField(range, "min");
      requireNumberField(range, "max");

      if (range.has("min") && range.has("max")) {
        double min = range.get("min").getAsDouble();
        double max = range.get("max").getAsDouble();
        if (min > max) {
          errors.add("Temperature min cannot be greater than max");
        }
      }
    }

    if (env.has("preferred_biomes")) {
      if (!env.get("preferred_biomes").isJsonArray()) {
        errors.add("Field 'preferred_biomes' must be an array");
      }
    }
  }
}
