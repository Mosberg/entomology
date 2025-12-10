# Integration Guide - Upgrading EntomologyMod to Advanced Architecture

## Overview

This guide explains how to integrate the new advanced systems into the existing `EntomologyMod.java` entry point.

## Step-by-Step Integration

### Step 1: Add System Integration Call

**In `EntomologyMod.java` `onInitialize()` method, add:**

```java
@Override
public void onInitialize() {
    LOGGER.info("Initializing Entomology v2.0.0");
    LOGGER.info("Architecture: Modular | Data-Driven | API-Extensible");

    // Initialize advanced systems (NEW)
    SystemIntegration.getInstance().initialize();

    // Initialize core systems (EXISTING)
    ConfigManager.getInstance();
    ModRegistry.initialize();
    DataDrivenRegistry.bootstrap();

    // Register content (EXISTING)
    registerContent();
    registerCreativeTab();

    // Register commands (EXISTING)
    CommandRegistrationCallback.EVENT.register(EntomologyCommands::register);

    // Register to item groups (EXISTING)
    // ... existing code ...

    LOGGER.info("Mod initialization complete!");
}
```

### Step 2: Add Shutdown Hook

**Add new method to `EntomologyMod.java`:**

```java
/**
 * Called during mod shutdown.
 * Ensures clean resource cleanup.
 */
public static void shutdown() {
    LOGGER.info("Shutting down Entomology...");
    SystemIntegration.getInstance().shutdown();
    LOGGER.info("Entomology shutdown complete");
}
```

**Register shutdown hook in `onInitialize()`:**

```java
// Add shutdown hook
Runtime.getRuntime().addShutdownHook(new Thread(() -> {
    shutdown();
}));
```

### Step 3: Add Reload Command

**Create new command class `src/main/java/dk/mosberg/entomology/command/ReloadCommand.java`:**

```java
package dk.mosberg.entomology.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dk.mosberg.entomology.integration.SystemIntegration;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ReloadCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("entomology")
            .then(CommandManager.literal("reload")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(ReloadCommand::reload)
            )
        );
    }

    private static int reload(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(
            () -> Text.literal("Reloading Entomology systems..."),
            true
        );

        try {
            SystemIntegration.getInstance().reload();

            context.getSource().sendFeedback(
                () -> Text.literal("§aReload complete!"),
                true
            );
            return 1;
        } catch (Exception e) {
            context.getSource().sendError(
                Text.literal("§cReload failed: " + e.getMessage())
            );
            return 0;
        }
    }
}
```

**Register in `EntomologyCommands.java`:**

```java
public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                           CommandRegistryAccess registryAccess,
                           CommandManager.RegistrationEnvironment environment) {
    // Existing commands...

    // Add reload command
    ReloadCommand.register(dispatcher);
}
```

### Step 4: Update ModRegistry

**Modify `ModRegistry.java` to use ComponentRegistry:**

```java
public static synchronized void initialize() {
    if (initialized) {
        LOGGER.warn("Attempted to initialize registry twice");
        return;
    }

    LOGGER.info("Initializing Entomology systems...");

    // Get system integration
    SystemIntegration integration = SystemIntegration.getInstance();
    ComponentRegistry registry = integration.getRegistry();

    // 1. Initialize API
    EntomologyAPI api = EntomologyAPI.getInstance();
    LOGGER.info("API initialized (version {})", api.getApiVersion());

    // 2. Register core mechanics with new system
    registerMechanics(registry);

    // 3. Register component types
    registerComponents(api);

    // 4. Register schema validators
    registerValidators(api);

    initialized = true;
    LOGGER.info("Entomology initialization complete");
}

private static void registerMechanics(ComponentRegistry registry) {
    // Advanced mechanics are registered in SystemIntegration
    // Legacy mechanics can be migrated here

    LOGGER.info("Registered mechanics via component registry");
}
```

### Step 5: Migrate Existing Mechanics

**Convert `BreedingMechanic.java` to use new system:**

```java
// OLD: Direct instantiation
public class BreedingMechanic implements IMechanic {
    // ... old implementation
}

// NEW: Extend AbstractMechanic
public class BreedingMechanicV2 extends AbstractMechanic {
    public BreedingMechanicV2() {
        super(
            EntomologyMod.id("breeding_v2"),
            "2.0.0",
            MechanicCategory.BREEDING,
            600
        );
    }

    @Override
    protected void registerParameters() {
        registerParameter("enabled", "Enable breeding", Boolean.class, true, false);
        // ... more parameters
    }

    @Override
    protected void applyConfiguration(JsonObject config) {
        // Read configuration
    }

    @Override
    protected IMechanicResult executeInternal(IMechanicContext context) {
        // Breeding logic
        return IMechanicResult.success();
    }

    @Override
    public boolean appliesTo(IMechanicContext context) {
        return context.getType() == IMechanicContext.ContextType.BREEDING;
    }
}
```

**Register in SystemIntegration:**

```java
registry.register(
    EntomologyMod.id("breeding_v2"),
    IAdvancedMechanic.class,
    BreedingMechanicV2::new
);
```

### Step 6: Update Configuration Loading

**Modify `ConfigManager.java` to work alongside `SchemaConfigManager`:**

```java
public static synchronized ConfigManager getInstance() {
    if (instance == null) {
        Path configPath = Paths.get("config", EntomologyMod.MODID);
        instance = new ConfigManager(configPath);
        instance.initialize();

        // Initialize advanced config manager
        SystemIntegration.getInstance(); // Triggers initialization
    }
    return instance;
}
```

### Step 7: Add Telemetry Hooks

**In gameplay code, add telemetry recording:**

```java
// When specimen is captured
TelemetrySystem.getInstance().incrementCounter("specimens_captured");
TelemetrySystem.getInstance().recordMetric("capture_success_rate", 0.75);

// When breeding occurs
TelemetrySystem.getInstance().incrementCounter("breeding_attempts");
TelemetrySystem.getInstance().recordMetric("breeding_success_rate", 0.5);

// When rare specimen spawns
TelemetrySystem.getInstance().incrementCounter("rare_spawns");
```

### Step 8: Use Balance Tuning

**Adjust spawn rates dynamically:**

```java
public double getSpawnWeight(String specimenId) {
    double baseWeight = 10.0;

    // Get adjusted value from telemetry
    double adjusted = TelemetrySystem.getInstance()
        .getAdjustedValue(
            EntomologyMod.id("spawn_weight_" + specimenId),
            baseWeight
        );

    return adjusted;
}
```

### Step 9: Testing Integration

**Create integration test:**

```java
@Test
void testSystemIntegration() {
    SystemIntegration integration = SystemIntegration.getInstance();

    // Initialize
    integration.initialize();
    assertTrue(integration.isInitialized());

    // Check components
    ComponentRegistry registry = integration.getRegistry();
    List<IAdvancedMechanic> mechanics = registry.getAllMechanics();
    assertFalse(mechanics.isEmpty());

    // Check configuration
    SchemaConfigManager config = integration.getConfigManager();
    assertNotNull(config);

    // Check telemetry
    TelemetrySystem telemetry = integration.getTelemetry();
    assertNotNull(telemetry);

    // Shutdown
    integration.shutdown();
    assertFalse(integration.isInitialized());
}
```

### Step 10: Update fabric.mod.json

**Add lifecycle hooks:**

```json
{
  "schemaVersion": 1,
  "id": "entomology",
  "version": "2.0.0",
  "entrypoints": {
    "main": ["dk.mosberg.entomology.EntomologyMod"],
    "client": ["dk.mosberg.entomology.client.EntomologyModClient"]
  },
  "mixins": ["entomology.mixins.json"],
  "depends": {
    "fabricloader": ">=0.15.0",
    "fabric-api": ">=0.92.0",
    "minecraft": ">=1.21.0",
    "java": ">=21"
  }
}
```

## Gradual Migration Strategy

### Phase 1: Parallel Systems (Weeks 1-2)

- Keep existing systems running
- Initialize new systems alongside
- Test new APIs with simple mechanics
- Verify configurations load correctly

### Phase 2: Feature Parity (Weeks 3-4)

- Migrate one mechanic at a time
- Compare behavior with old system
- Run regression tests
- Fix compatibility issues

### Phase 3: Deprecation (Weeks 5-6)

- Mark old APIs as `@Deprecated`
- Add migration warnings in logs
- Update documentation
- Provide migration scripts

### Phase 4: Removal (Week 7+)

- Remove old systems
- Clean up unused code
- Final testing
- Release v2.0.0

## Backward Compatibility

### Configuration Migration

**Automatic migration of old configs:**

```java
public JsonObject migrateConfig(JsonObject oldConfig) {
    JsonObject newConfig = new JsonObject();

    // Copy version
    if (!oldConfig.has("version")) {
        newConfig.addProperty("version", "2.0.0");
    }

    // Migrate breeding settings
    if (oldConfig.has("breeding")) {
        JsonObject oldBreeding = oldConfig.getAsJsonObject("breeding");
        JsonObject newBreeding = new JsonObject();

        // Copy with defaults
        newBreeding.addProperty("enabled",
            oldBreeding.has("enabled")
                ? oldBreeding.get("enabled").getAsBoolean()
                : true
        );

        newConfig.add("breeding", newBreeding);
    }

    return newConfig;
}
```

### API Compatibility Layer

**Wrapper for old API:**

```java
@Deprecated(since = "2.0.0", forRemoval = true)
public class LegacyMechanicWrapper extends AbstractMechanic {
    private final IMechanic legacyMechanic;

    public LegacyMechanicWrapper(IMechanic legacy) {
        super(legacy.getId(), "1.0.0", MechanicCategory.CUSTOM,
            legacy.getPriority());
        this.legacyMechanic = legacy;
    }

    @Override
    protected IMechanicResult executeInternal(IMechanicContext context) {
        // Convert context to legacy format
        MechanicContext legacyContext = convertContext(context);

        // Execute legacy mechanic
        MechanicResult legacyResult = legacyMechanic.execute(
            "specimen_id", legacyContext
        );

        // Convert result to new format
        return convertResult(legacyResult);
    }
}
```

## Troubleshooting

### Issue: Systems not initializing

**Solution**: Check initialization order

```java
// Ensure proper order
SystemIntegration.getInstance().initialize(); // First
ConfigManager.getInstance(); // Then legacy systems
ModRegistry.initialize();
```

### Issue: Configuration not loading

**Solution**: Verify schema paths

```java
// Check paths exist
Path schemaPath = Paths.get("config", "entomology", "schema");
if (!Files.exists(schemaPath)) {
    Files.createDirectories(schemaPath);
}
```

### Issue: Mechanics not executing

**Solution**: Check lifecycle state

```java
IAdvancedMechanic mechanic = registry.get(id, IAdvancedMechanic.class).orElse(null);
if (mechanic != null) {
    System.out.println("State: " + mechanic.getState());
    // Should be ENABLED
}
```

## Performance Considerations

### Lazy Loading

```java
// Don't initialize everything at startup
registry.register(id, type, factory); // Factory not called yet

// Components instantiated on first use
registry.get(id, type); // Now factory is called
```

### Caching

```java
// Cache frequently accessed configs
private final Map<String, JsonObject> configCache = new ConcurrentHashMap<>();

public JsonObject getConfig(String name) {
    return configCache.computeIfAbsent(name, k ->
        configManager.loadConfig(k, k + "-schema.json")
    );
}
```

### Async Loading

```java
// Load data asynchronously
CompletableFuture.runAsync(() -> {
    SystemIntegration.getInstance().initialize();
}).thenRun(() -> {
    LOGGER.info("Systems initialized in background");
});
```

## Validation

### Pre-Integration Checklist

- [ ] All new classes compile
- [ ] Tests pass
- [ ] Schemas are valid
- [ ] Configs migrate correctly
- [ ] No circular dependencies
- [ ] Documentation updated

### Post-Integration Checklist

- [ ] Mod loads successfully
- [ ] All mechanics execute
- [ ] Configuration reloads work
- [ ] Telemetry collects data
- [ ] No performance regression
- [ ] Logs show proper initialization

## Rollback Plan

If integration causes issues:

1. **Disable new systems**:

   ```java
   // In EntomologyMod.onInitialize()
   // Comment out:
   // SystemIntegration.getInstance().initialize();
   ```

2. **Revert to legacy configs**:

   ```bash
   cp config/entomology/mechanics.json.backup config/entomology/mechanics.json
   ```

3. **Remove new dependencies**:
   ```gradle
   // Comment out in build.gradle:
   // implementation "com.github.erosb:everit-json-schema:1.14.4"
   ```

## Support

For integration help:

- GitHub Issues: Technical problems
- Discord: Real-time support
- Documentation: API reference

---

**Integration Version**: 2.0.0
**Last Updated**: December 2025
