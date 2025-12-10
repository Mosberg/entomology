package dk.mosberg.entomology.registry;

import dk.mosberg.entomology.EntomologyMod;
import dk.mosberg.entomology.api.EntomologyAPI;
import dk.mosberg.entomology.api.mechanics.IAdvancedMechanic;
import dk.mosberg.entomology.mechanics.BreedingMechanic;
import dk.mosberg.entomology.mechanics.EnvironmentalMechanic;
import dk.mosberg.entomology.registry.advanced.ComponentRegistry;
import dk.mosberg.entomology.schema.SpecimenSchemaValidator;

/**
 * Central registry for initializing all mod systems.
 * Handles registration order and dependencies.
 */
public final class ModRegistry {
  private static boolean initialized = false;

  private ModRegistry() {
  }

  /**
   * Initializes all mod systems in correct order.
   * Must be called during mod initialization.
   */
  public static synchronized void initialize() {
    if (initialized) {
      EntomologyMod.LOGGER.warn("Attempted to initialize registry twice");
      return;
    }

    EntomologyMod.LOGGER.info("Initializing Entomology systems...");

    // 1. Initialize API
    EntomologyAPI api = EntomologyAPI.getInstance();
    ComponentRegistry registry = ComponentRegistry.getInstance();
    EntomologyMod.LOGGER.info("API initialized (version {})", api.getApiVersion());

    // 2. Register core mechanics
    registerMechanics(api, registry);

    // 3. Register component types
    registerComponents(api);

    // 4. Register schema validators
    registerValidators(api);

    initialized = true;
    EntomologyMod.LOGGER.info("Entomology initialization complete");
  }

  private static void registerMechanics(EntomologyAPI api, ComponentRegistry registry) {
    // Register legacy mechanics with API
    api.registerMechanic(new BreedingMechanic());
    api.registerMechanic(new EnvironmentalMechanic());

    // Advanced mechanics are registered via SystemIntegration
    // Get all advanced mechanics from component registry
    var advancedMechanics = registry.getAllMechanics();
    for (IAdvancedMechanic mechanic : advancedMechanics) {
      EntomologyMod.LOGGER.debug("Advanced mechanic available: {} (v{})",
          mechanic.getId(), mechanic.getVersion());
    }

    EntomologyMod.LOGGER.info("Registered {} legacy mechanics, {} advanced mechanics",
        api.getMechanics().size(), advancedMechanics.size());
  }

  private static void registerComponents(EntomologyAPI api) {
    // Component factories registered here
    // Example: api.registerSpecimenComponent(id, factory);
    EntomologyMod.LOGGER.info("Component registration complete");
  }

  private static void registerValidators(EntomologyAPI api) {
    // Register specimen schema validator
    try {
      api.registerSchemaValidator("specimen", new SpecimenSchemaValidator());
      EntomologyMod.LOGGER.info("Registered specimen schema validator");
    } catch (Exception e) {
      EntomologyMod.LOGGER.error("Failed to register validators", e);
    }
    EntomologyMod.LOGGER.info("Schema validator registration complete");
  }

  public static boolean isInitialized() {
    return initialized;
  }
}
