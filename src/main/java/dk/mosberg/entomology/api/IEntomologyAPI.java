package dk.mosberg.entomology.api;

import dk.mosberg.entomology.component.ISpecimenComponent;
import dk.mosberg.entomology.mechanics.IMechanic;
import dk.mosberg.entomology.schema.ISchemaValidator;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Optional;

/**
 * Main API interface for Entomology mod.
 * Provides extensible access to all data-driven mechanics and component
 * systems.
 *
 * API Version: 1.0.0
 * Thread Safety: All methods are thread-safe unless otherwise specified
 *
 * @since 1.0.0
 */
public interface IEntomologyAPI {

  /**
   * Gets the API version for compatibility checking.
   *
   * @return semantic version string (e.g., "1.0.0")
   */
  String getApiVersion();

  /**
   * Registers a custom mechanic that will be applied to specimens.
   * Mechanics are executed in priority order (higher priority first).
   *
   * @param mechanic the mechanic to register
   * @throws IllegalArgumentException if mechanic is null or ID conflicts
   */
  void registerMechanic(IMechanic mechanic);

  /**
   * Unregisters a mechanic by its identifier.
   *
   * @param id the mechanic identifier
   * @return true if mechanic was removed, false if not found
   */
  boolean unregisterMechanic(Identifier id);

  /**
   * Gets all registered mechanics.
   *
   * @return immutable collection of mechanics sorted by priority
   */
  Collection<IMechanic> getMechanics();

  /**
   * Gets a specific mechanic by identifier.
   *
   * @param id the mechanic identifier
   * @return optional containing the mechanic if found
   */
  Optional<IMechanic> getMechanic(Identifier id);

  /**
   * Registers a custom component type for specimens.
   * Components allow attaching arbitrary data and behavior to specimens.
   *
   * @param componentType the component type identifier
   * @param factory       factory function to create component instances
   * @throws IllegalArgumentException if componentType is null or already
   *                                  registered
   */
  void registerSpecimenComponent(Identifier componentType, ISpecimenComponent.Factory factory);

  /**
   * Gets a specimen component factory by type.
   *
   * @param componentType the component type identifier
   * @return optional containing the factory if found
   */
  Optional<ISpecimenComponent.Factory> getSpecimenComponentFactory(Identifier componentType);

  /**
   * Registers a schema validator for custom JSON definitions.
   * Validators ensure data integrity at load time.
   *
   * @param schemaPath the JSON schema path (e.g., "items", "specimens")
   * @param validator  the validator implementation
   */
  void registerSchemaValidator(String schemaPath, ISchemaValidator validator);

  /**
   * Validates a JSON object against registered schema.
   *
   * @param schemaPath the schema path
   * @param json       the JSON to validate
   * @return validation result with errors if any
   */
  ValidationResult validateSchema(String schemaPath, com.google.gson.JsonObject json);

  /**
   * Reloads all data-driven content from resource packs.
   * This is async-safe and can be called from any thread.
   *
   * @return future that completes when reload finishes
   */
  java.util.concurrent.CompletableFuture<Void> reloadData();

  /**
   * Result of schema validation.
   */
  interface ValidationResult {
    boolean isValid();

    Collection<String> getErrors();

    Collection<String> getWarnings();
  }
}
