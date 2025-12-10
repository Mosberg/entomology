package dk.mosberg.entomology.registry;

import dk.mosberg.entomology.EntomologyMod;
import dk.mosberg.entomology.api.EntomologyAPI;
import dk.mosberg.entomology.mechanics.BreedingMechanic;
import dk.mosberg.entomology.mechanics.EnvironmentalMechanic;

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
    EntomologyMod.LOGGER.info("API initialized (version {})", api.getApiVersion());

    // 2. Register core mechanics
    registerMechanics(api);

    // 3. Register component types
    registerComponents(api);

    // 4. Register schema validators
    registerValidators(api);

    initialized = true;
    EntomologyMod.LOGGER.info("Entomology initialization complete");
  }

  private static void registerMechanics(EntomologyAPI api) {
    api.registerMechanic(new BreedingMechanic());
    api.registerMechanic(new EnvironmentalMechanic());

    EntomologyMod.LOGGER.info("Registered {} mechanics", api.getMechanics().size());
  }

  private static void registerComponents(EntomologyAPI api) {
    // Component registration will be added here
    EntomologyMod.LOGGER.info("Component registration complete");
  }

  private static void registerValidators(EntomologyAPI api) {
    // Schema validator registration will be added here
    EntomologyMod.LOGGER.info("Schema validator registration complete");
  }

  public static boolean isInitialized() {
    return initialized;
  }
}
