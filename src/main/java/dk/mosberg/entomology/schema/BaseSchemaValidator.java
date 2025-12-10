package dk.mosberg.entomology.schema;

import com.google.gson.JsonObject;
import dk.mosberg.entomology.api.IEntomologyAPI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base implementation for schema validators with common validation helpers.
 */
public abstract class BaseSchemaValidator implements ISchemaValidator {

  protected final List<String> errors = new ArrayList<>();
  protected final List<String> warnings = new ArrayList<>();

  @Override
  public IEntomologyAPI.ValidationResult validate(JsonObject json) {
    errors.clear();
    warnings.clear();

    performValidation(json);

    return new ValidationResultImpl(errors.isEmpty(), errors, warnings);
  }

  /**
   * Performs the actual validation logic.
   * Implementations should add errors/warnings to the lists.
   *
   * @param json the JSON to validate
   */
  protected abstract void performValidation(JsonObject json);

  protected void requireField(JsonObject obj, String field) {
    if (!obj.has(field)) {
      errors.add("Missing required field: " + field);
    }
  }

  protected void requireStringField(JsonObject obj, String field) {
    requireField(obj, field);
    if (obj.has(field) && !obj.get(field).isJsonPrimitive()) {
      errors.add("Field '" + field + "' must be a string");
    }
  }

  protected void requireNumberField(JsonObject obj, String field) {
    requireField(obj, field);
    if (obj.has(field) && !obj.get(field).isJsonPrimitive()) {
      errors.add("Field '" + field + "' must be a number");
    }
  }

  protected void requireNumberInRange(JsonObject obj, String field, double min, double max) {
    requireNumberField(obj, field);
    if (obj.has(field) && obj.get(field).isJsonPrimitive()) {
      double value = obj.get(field).getAsDouble();
      if (value < min || value > max) {
        errors.add("Field '" + field + "' must be between " + min + " and " + max);
      }
    }
  }

  protected void warnIfMissing(JsonObject obj, String field) {
    if (!obj.has(field)) {
      warnings.add("Optional field '" + field + "' not specified, using default");
    }
  }

  private static class ValidationResultImpl implements IEntomologyAPI.ValidationResult {
    private final boolean valid;
    private final List<String> errors;
    private final List<String> warnings;

    ValidationResultImpl(boolean valid, List<String> errors, List<String> warnings) {
      this.valid = valid;
      this.errors = Collections.unmodifiableList(new ArrayList<>(errors));
      this.warnings = Collections.unmodifiableList(new ArrayList<>(warnings));
    }

    @Override
    public boolean isValid() {
      return valid;
    }

    @Override
    public java.util.Collection<String> getErrors() {
      return errors;
    }

    @Override
    public java.util.Collection<String> getWarnings() {
      return warnings;
    }
  }
}
