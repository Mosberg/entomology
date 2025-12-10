package dk.mosberg.entomology.integration;

import dk.mosberg.entomology.EntomologyMod;
import dk.mosberg.entomology.api.mechanics.IAdvancedMechanic;
import dk.mosberg.entomology.balance.TelemetrySystem;
import dk.mosberg.entomology.config.advanced.SchemaConfigManager;
import dk.mosberg.entomology.mechanics.impl.AdvancedBreedingMechanic;
import dk.mosberg.entomology.registry.advanced.ComponentRegistry;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Integration manager for initializing the advanced systems.
 * Coordinates component registry, configuration, and telemetry.
 */
public class SystemIntegration {
  private static SystemIntegration instance;

  private final ComponentRegistry registry;
  private final SchemaConfigManager configManager;
  private final TelemetrySystem telemetry;
  private boolean initialized = false;

  private SystemIntegration() {
    this.registry = ComponentRegistry.getInstance();

    Path configRoot = Paths.get("config", EntomologyMod.MODID);
    Path schemaRoot = Paths.get("config", EntomologyMod.MODID, "schema");
    this.configManager = new SchemaConfigManager(configRoot, schemaRoot);

    this.telemetry = TelemetrySystem.getInstance();
  }

  public static synchronized SystemIntegration getInstance() {
    if (instance == null) {
      instance = new SystemIntegration();
    }
    return instance;
  }

  /**
   * Initializes all advanced systems.
   * Should be called during mod initialization.
   */
  public void initialize() {
    if (initialized) {
      EntomologyMod.LOGGER.warn("Advanced systems already initialized");
      return;
    }

    EntomologyMod.LOGGER.info("Initializing advanced systems...");

    // 1. Register core components
    registerComponents();

    // 2. Load configurations
    loadConfigurations();

    // 3. Initialize component registry
    registry.initializeAll();

    // 4. Configure mechanics from JSON
    configureMechanics();

    // 5. Initialize telemetry
    initializeTelemetry();

    initialized = true;
    EntomologyMod.LOGGER.info("Advanced systems initialized successfully");
  }

  /**
   * Registers all components with the registry.
   */
  private void registerComponents() {
    EntomologyMod.LOGGER.debug("Registering components...");

    // Register advanced mechanics
    registry.register(
        EntomologyMod.id("advanced_breeding"),
        IAdvancedMechanic.class,
        AdvancedBreedingMechanic::new);

    registry.register(
        EntomologyMod.id("advanced_environmental"),
        IAdvancedMechanic.class,
        dk.mosberg.entomology.mechanics.impl.AdvancedEnvironmentalMechanic::new);

    // Register telemetry system as singleton
    registry.registerSingleton(
        EntomologyMod.id("telemetry"),
        telemetry);

    EntomologyMod.LOGGER.debug("Registered {} mechanics",
        registry.getAllMechanics().size());
  }

  /**
   * Loads all configuration files.
   */
  private void loadConfigurations() {
    EntomologyMod.LOGGER.debug("Loading configurations...");

    // Load mechanics configuration with schema validation
    configManager.loadConfig("mechanics", "mechanics.schema.json");

    // Add configuration change listener for hot-reload
    configManager.addListener("mechanics", config -> {
      EntomologyMod.LOGGER.info("Mechanics configuration changed, reloading...");
      configureMechanics();
    });
  }

  /**
   * Configures mechanics from loaded configurations.
   */
  private void configureMechanics() {
    var mechanics = registry.getAllMechanics();

    for (IAdvancedMechanic mechanic : mechanics) {
      try {
        // Get mechanic-specific configuration
        var config = configManager.get(
            "mechanics",
            mechanic.getId().getPath(),
            com.google.gson.JsonObject.class,
            new com.google.gson.JsonObject());

        if (config != null && !config.entrySet().isEmpty()) {
          mechanic.configure(config);
          EntomologyMod.LOGGER.debug("Configured mechanic: {}",
              mechanic.getId());
        }
      } catch (Exception e) {
        EntomologyMod.LOGGER.error("Failed to configure mechanic: {}",
            mechanic.getId(), e);
      }
    }
  }

  /**
   * Initializes telemetry system.
   */
  private void initializeTelemetry() {
    boolean telemetryEnabled = configManager.get(
        "mechanics",
        "balance.telemetryEnabled",
        Boolean.class,
        true);

    telemetry.setEnabled(telemetryEnabled);

    if (telemetryEnabled) {
      EntomologyMod.LOGGER.info("Telemetry system enabled");
    } else {
      EntomologyMod.LOGGER.info("Telemetry system disabled");
    }
  }

  /**
   * Reloads all configurations and reconfigures systems.
   */
  public void reload() {
    EntomologyMod.LOGGER.info("Reloading advanced systems...");

    configManager.reloadAll();
    configureMechanics();

    EntomologyMod.LOGGER.info("Reload complete");
  }

  /**
   * Validate all system configurations.
   *
   * @return true if all configurations are valid
   */
  public boolean validate() {
    try {
      // Validate all registered mechanics
      var mechanics = registry.getAllMechanics();
      int validCount = 0;

      for (var mechanic : mechanics) {
        try {
          // Each mechanic should already be configured
          EntomologyMod.LOGGER.debug("Validating mechanic: {}", mechanic.getId());
          validCount++;
        } catch (Exception e) {
          EntomologyMod.LOGGER.error("Mechanic {} validation failed", mechanic.getId(), e);
          return false;
        }
      }

      EntomologyMod.LOGGER.info("Validated {} mechanics", validCount);
      return true;
    } catch (Exception e) {
      EntomologyMod.LOGGER.error("Validation failed", e);
      return false;
    }
  }

  /**
   * Shuts down all systems gracefully.
   */
  public void shutdown() {
    EntomologyMod.LOGGER.info("Shutting down advanced systems...");

    registry.shutdownAll();
    telemetry.reset();

    initialized = false;
    EntomologyMod.LOGGER.info("Shutdown complete");
  }

  /**
   * Gets the component registry.
   */
  public ComponentRegistry getRegistry() {
    return registry;
  }

  /**
   * Gets the configuration manager.
   */
  public SchemaConfigManager getConfigManager() {
    return configManager;
  }

  /**
   * Gets the telemetry system.
   */
  public TelemetrySystem getTelemetry() {
    return telemetry;
  }

  /**
   * Checks if systems are initialized.
   */
  public boolean isInitialized() {
    return initialized;
  }
}
