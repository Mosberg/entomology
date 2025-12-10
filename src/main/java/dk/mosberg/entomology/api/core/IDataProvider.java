package dk.mosberg.entomology.api.core;

import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Generic data provider interface for extensible, data-driven content.
 * Implementations can provide specimens, mechanics configs, research data, etc.
 *
 * @param <T> The type of data provided
 */
public interface IDataProvider<T> {
  /**
   * Gets the unique identifier for this data provider.
   *
   * @return provider identifier
   */
  Identifier getProviderId();

  /**
   * Gets the version of this data provider.
   * Used for compatibility checks and migrations.
   *
   * @return semantic version string (e.g., "1.2.0")
   */
  String getVersion();

  /**
   * Loads data from JSON configuration.
   *
   * @param config JSON configuration object
   * @return CompletableFuture that completes when loading is done
   */
  CompletableFuture<Void> loadData(JsonObject config);

  /**
   * Reloads all data from source.
   *
   * @return CompletableFuture that completes when reload is done
   */
  CompletableFuture<Void> reload();

  /**
   * Gets a specific data entry by identifier.
   *
   * @param id entry identifier
   * @return Optional containing the data if found
   */
  Optional<T> getData(Identifier id);

  /**
   * Gets all data entries.
   *
   * @return immutable collection of all data
   */
  Collection<T> getAllData();

  /**
   * Validates data integrity.
   *
   * @return validation result with any errors or warnings
   */
  ValidationResult validate();

  /**
   * Clears all loaded data.
   */
  void clear();

  /**
   * Gets the priority for this provider.
   * Higher priority providers are loaded first and can be overridden by lower
   * priority.
   *
   * @return priority value (default: 0, higher = earlier loading)
   */
  default int getPriority() {
    return 0;
  }

  /**
   * Checks if this provider supports hot-reloading.
   *
   * @return true if hot-reload is supported
   */
  default boolean supportsHotReload() {
    return true;
  }

  /**
   * Validation result for data integrity checks.
   */
  interface ValidationResult {
    boolean isValid();

    Collection<String> getErrors();

    Collection<String> getWarnings();

    static ValidationResult success() {
      return new SimpleValidationResult(true, java.util.Collections.emptyList(),
          java.util.Collections.emptyList());
    }

    static ValidationResult error(String... errors) {
      return new SimpleValidationResult(false, java.util.Arrays.asList(errors),
          java.util.Collections.emptyList());
    }
  }

  /**
   * Simple implementation of ValidationResult.
   */
  class SimpleValidationResult implements ValidationResult {
    private final boolean valid;
    private final Collection<String> errors;
    private final Collection<String> warnings;

    public SimpleValidationResult(boolean valid, Collection<String> errors, Collection<String> warnings) {
      this.valid = valid;
      this.errors = java.util.Collections.unmodifiableCollection(errors);
      this.warnings = java.util.Collections.unmodifiableCollection(warnings);
    }

    @Override
    public boolean isValid() {
      return valid;
    }

    @Override
    public Collection<String> getErrors() {
      return errors;
    }

    @Override
    public Collection<String> getWarnings() {
      return warnings;
    }
  }
}
