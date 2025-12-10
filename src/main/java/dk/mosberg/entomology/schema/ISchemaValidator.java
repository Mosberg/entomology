package dk.mosberg.entomology.schema;

import com.google.gson.JsonObject;
import dk.mosberg.entomology.api.IEntomologyAPI;

/**
 * Interface for JSON schema validators.
 * Validators ensure data integrity and provide helpful error messages.
 */
@FunctionalInterface
public interface ISchemaValidator {

  /**
   * Validates a JSON object against this schema.
   *
   * @param json the JSON to validate
   * @return validation result
   */
  IEntomologyAPI.ValidationResult validate(JsonObject json);
}
