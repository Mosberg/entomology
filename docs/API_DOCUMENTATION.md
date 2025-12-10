# Entomology Mod - Advanced API Documentation

## Overview

The Entomology mod provides a comprehensive, data-driven framework for implementing insect collection, breeding, and research mechanics in Minecraft. The architecture emphasizes:

- **Extensibility**: Plugin-based APIs for community contributions
- **Data-Driven Design**: JSON-configured mechanics and content
- **Version Compatibility**: Forward-compatible APIs with migration support
- **Performance**: Optimized for multiplayer and large-scale deployments
- **Maintainability**: Clean separation of concerns and modular components

## Architecture

### Layer Overview

```
┌─────────────────────────────────────────────────────────┐
│                    Mod Entry Point                      │
│                  (EntomologyMod.java)                   │
└─────────────────────────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        ▼                   ▼                   ▼
┌──────────────┐   ┌────────────────┐  ┌──────────────┐
│  API Layer   │   │ Registry Layer │  │ Config Layer │
│              │   │                │  │              │
│ - IDataProv. │   │ - Components   │  │ - Schema     │
│ - IMechanic  │   │ - Lifecycle    │  │ - Hot-reload │
│ - ISpecies   │   │ - Dependencies │  │ - Validation │
└──────────────┘   └────────────────┘  └──────────────┘
        │                   │                   │
        └───────────────────┼───────────────────┘
                            ▼
        ┌───────────────────────────────────────┐
        │         Component Layer               │
        │  ┌─────────────────────────────────┐  │
        │  │   Mechanics Engine              │  │
        │  │  - Breeding                     │  │
        │  │  - Environmental                │  │
        │  │  - Spawning                     │  │
        │  │  - Research                     │  │
        │  └─────────────────────────────────┘  │
        │  ┌─────────────────────────────────┐  │
        │  │   Balance System                │  │
        │  │  - Telemetry                    │  │
        │  │  - Adaptive Tuning              │  │
        │  │  - Difficulty Scaling           │  │
        │  └─────────────────────────────────┘  │
        └───────────────────────────────────────┘
                            │
                            ▼
        ┌───────────────────────────────────────┐
        │         Data Layer                    │
        │  - JSON Definitions                   │
        │  - Schema Validation                  │
        │  - Runtime Data                       │
        └───────────────────────────────────────┘
```

## Core APIs

### 1. Data Provider API (`IDataProvider<T>`)

**Purpose**: Generic interface for extensible data sources.

**Key Methods**:

- `loadData(JsonObject)`: Asynchronous data loading
- `reload()`: Hot-reload support
- `validate()`: Data integrity checks
- `getPriority()`: Load order control

**Example Usage**:

```java
public class CustomSpeciesProvider implements IDataProvider<SpeciesData> {
    @Override
    public Identifier getProviderId() {
        return new Identifier("mymod", "custom_species");
    }

    @Override
    public CompletableFuture<Void> loadData(JsonObject config) {
        // Load custom species data
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public Optional<SpeciesData> getData(Identifier id) {
        return Optional.ofNullable(speciesMap.get(id));
    }
}
```

### 2. Advanced Mechanics API (`IAdvancedMechanic`)

**Purpose**: Pluggable gameplay systems with lifecycle management.

**Key Features**:

- Lifecycle hooks (init, enable, disable, shutdown)
- Performance metrics tracking
- Configuration validation
- Dependency resolution

**Example Implementation**:

```java
public class CustomMechanic extends AbstractMechanic {
    public CustomMechanic() {
        super(
            new Identifier("mymod", "custom"),
            "1.0.0",
            MechanicCategory.CUSTOM,
            500 // priority
        );
    }

    @Override
    protected void registerParameters() {
        registerParameter("enabled", "Enable mechanic",
            Boolean.class, true, false);
    }

    @Override
    protected IMechanicResult executeInternal(IMechanicContext ctx) {
        // Implement mechanic logic
        return IMechanicResult.success();
    }
}
```

### 3. Species Data API (`ISpeciesData`)

**Purpose**: Immutable species definitions with traits and breeding data.

**Key Components**:

- Rarity tiers (Common → Legendary)
- Environmental requirements
- Breeding compatibility
- Trait inheritance rules

**JSON Schema**:

```json
{
  "id": "mymod:custom_butterfly",
  "displayName": "species.mymod.custom_butterfly",
  "rarity": "rare",
  "spawnWeight": 5,
  "traits": {
    "flight_speed": {
      "level": 8,
      "inheritable": true,
      "inheritanceChance": 0.7
    }
  },
  "breedingData": {
    "compatibleSpecies": ["entomology:butterfly"],
    "baseSuccessChance": 0.4,
    "breedingTime": 2400
  }
}
```

### 4. Balance Tuner API (`IBalanceTuner`)

**Purpose**: Adaptive balance adjustment based on telemetry.

**Tuning Strategies**:

- Linear interpolation
- Exponential curves
- Logarithmic scaling
- PID controllers
- Custom formulas

**Example**:

```java
public class SpawnRateTuner implements IBalanceTuner {
    @Override
    public double computeAdjustedValue(double baseValue,
                                      ITelemetryData telemetry) {
        double captureRate = telemetry.getMetric("captures_per_hour");

        // Reduce spawn rate if players are capturing too frequently
        if (captureRate > 50.0) {
            return baseValue * 0.8;
        }

        return baseValue;
    }

    @Override
    public TuningStrategy getStrategy() {
        return TuningStrategy.LINEAR;
    }
}
```

## Component Registry System

### Registration

```java
ComponentRegistry registry = ComponentRegistry.getInstance();

// Register with dependencies
registry.register(
    EntomologyMod.id("my_mechanic"),
    IAdvancedMechanic.class,
    () -> new MyMechanic(),
    EntomologyMod.id("dependency_mechanic")
);

// Register singleton
registry.registerSingleton(
    EntomologyMod.id("telemetry"),
    TelemetrySystem.getInstance()
);
```

### Lifecycle Management

The registry automatically:

1. Resolves dependency order (topological sort)
2. Initializes components in correct sequence
3. Handles circular dependency detection
4. Manages enable/disable states
5. Ensures clean shutdown

## Configuration System

### Schema-Driven Configuration

**Features**:

- JSON Schema validation
- Version migration
- Hot-reload support
- Type-safe access
- Change listeners

**Example**:

```java
SchemaConfigManager config = new SchemaConfigManager(configPath, schemaPath);

// Load with validation
JsonObject mechanicsConfig = config.loadConfig(
    "mechanics",
    "mechanics.schema.json"
);

// Type-safe access
double mutationRate = config.get(
    "mechanics",
    "breeding.mutationRate",
    Double.class,
    0.05
);

// Listen for changes
config.addListener("mechanics", updatedConfig -> {
    // React to configuration changes
    reloadMechanics(updatedConfig);
});
```

### Configuration Hierarchy

1. **Default values** (hardcoded)
2. **Schema defaults** (from JSON schema)
3. **Config file** (user overrides)
4. **Runtime overrides** (admin commands)

## Telemetry and Balance

### Metrics Collection

```java
TelemetrySystem telemetry = TelemetrySystem.getInstance();

// Record metrics
telemetry.recordMetric("breeding_attempts", 1.0);
telemetry.recordMetric("mutation_rate", 0.08);
telemetry.incrementCounter("rare_spawns");

// Get adjusted values
double spawnRate = telemetry.getAdjustedValue(
    EntomologyMod.id("spawn_rate"),
    baseSpawnRate
);
```

### Balance Tuning

Balance tuners automatically adjust gameplay parameters based on collected metrics:

- **Too easy**: Reduce spawn rates, increase breeding difficulty
- **Too hard**: Increase spawn rates, boost mutation chances
- **Unbalanced**: Adjust rarity tiers, modify environmental requirements

## Data-Driven Configuration

### Specimen Definitions

Location: `data/entomology/specimen/*.json`

```json
{
  "id": "entomology:monarch_butterfly",
  "displayName": "specimen.entomology.monarch_butterfly.name",
  "description": "specimen.entomology.monarch_butterfly.desc",
  "rarity": "rare",
  "spawnBiomes": ["minecraft:plains", "minecraft:sunflower_plains"],
  "spawnWeight": 3,
  "environmental": {
    "optimalTemperature": { "min": 20, "max": 30 },
    "lightPreference": "bright",
    "timePreference": "diurnal"
  }
}
```

### Breeding Configuration

Location: `data/entomology/mechanics/breeding_config.json`

```json
{
  "version": "2.0.0",
  "enabled": true,
  "globalMutationRate": 0.05,
  "breedingPairs": [
    {
      "parent1": "entomology:butterfly",
      "parent2": "entomology:butterfly",
      "offspring": ["entomology:butterfly"],
      "mutations": ["entomology:monarch_butterfly"],
      "successChance": 0.5,
      "mutationChance": 0.1
    }
  ]
}
```

## Extension Points

### Custom Mechanics

1. Extend `AbstractMechanic`
2. Implement `executeInternal()`
3. Register parameters
4. Register in component registry

### Custom Data Providers

1. Implement `IDataProvider<T>`
2. Define data structure
3. Implement validation
4. Register with priority

### Custom Balance Tuners

1. Implement `IBalanceTuner`
2. Define tuning strategy
3. Register target parameter
4. Hook into telemetry system

## Best Practices

### 1. Version Compatibility

Always implement `IVersioned` for components that may evolve:

```java
@Override
public String getVersion() {
    return "2.1.0";
}

@Override
public String getMinCompatibleVersion() {
    return "2.0.0";
}
```

### 2. Lifecycle Management

Use lifecycle hooks for proper resource management:

```java
@Override
public void onInitialize() {
    // Load resources, register listeners
}

@Override
public void onShutdown() {
    // Clean up resources, save state
}
```

### 3. Thread Safety

Use thread-safe collections for shared data:

```java
private final Map<Identifier, Data> cache = new ConcurrentHashMap<>();
```

### 4. Performance

- Cache frequently accessed data
- Use lazy initialization for expensive operations
- Profile mechanics with performance metrics

### 5. Configuration

- Provide sensible defaults
- Validate all user input
- Support hot-reload where possible

## Migration Guide

### From Legacy System

1. **Update EntomologyMod.java**:

   - Replace direct registration with ComponentRegistry
   - Use SchemaConfigManager for configuration

2. **Refactor Mechanics**:

   - Extend AbstractMechanic
   - Implement new interfaces
   - Add configuration validation

3. **Update Data Files**:

   - Add version fields
   - Validate against schemas
   - Test hot-reload

4. **Add Tests**:
   - Unit tests for mechanics
   - Integration tests for systems
   - Validation tests for configs

## Performance Considerations

### Optimization Tips

1. **Lazy Loading**: Only instantiate components when needed
2. **Caching**: Cache computed values and validation results
3. **Batching**: Process multiple operations together
4. **Async Operations**: Use CompletableFuture for I/O

### Monitoring

Track performance metrics:

- Mechanic execution times
- Data load times
- Memory usage
- Cache hit rates

## Troubleshooting

### Common Issues

**Issue**: Circular dependency error
**Solution**: Review dependency declarations, ensure acyclic graph

**Issue**: Configuration validation fails
**Solution**: Check JSON against schema, verify version compatibility

**Issue**: Mechanic not executing
**Solution**: Check lifecycle state, verify appliesTo() logic

**Issue**: Hot-reload not working
**Solution**: Ensure supportsHotReload() returns true, check file watchers

## Community Contributions

### Adding Custom Content

1. Fork the repository
2. Create data-driven JSON files
3. Add schemas if introducing new formats
4. Submit pull request with examples

### Adding New Mechanics

1. Implement `IAdvancedMechanic`
2. Add comprehensive tests
3. Document configuration options
4. Provide usage examples

### Reporting Issues

Include:

- Mod version
- Minecraft version
- Configuration files
- Log files
- Steps to reproduce

## API Stability

- **Stable**: Core interfaces (IDataProvider, IAdvancedMechanic)
- **Evolving**: Balance APIs (may add features)
- **Experimental**: Advanced features (subject to change)

Version guarantees:

- **Major**: Breaking changes
- **Minor**: New features, backward compatible
- **Patch**: Bug fixes only

## Resources

- **GitHub**: https://github.com/Mosberg/entomology
- **Wiki**: https://github.com/Mosberg/entomology/wiki
- **Discord**: [Community Server]
- **Issue Tracker**: https://github.com/Mosberg/entomology/issues
