# Entomology - Advanced Architecture Documentation

## 📋 Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [API Layer](#api-layer)
3. [Component System](#component-system)
4. [Mechanics System](#mechanics-system)
5. [Configuration Management](#configuration-management)
6. [Schema Validation](#schema-validation)
7. [Extension Guide](#extension-guide)
8. [Performance Considerations](#performance-considerations)

---

## Architecture Overview

Entomology uses a **modular, component-based architecture** with clear separation of concerns:

```
┌─────────────────────────────────────────────────┐
│                  API Layer                      │
│  (IEntomologyAPI - Public extension interface)  │
└───────────────────┬─────────────────────────────┘
                    │
    ┌───────────────┼───────────────┐
    ▼               ▼               ▼
┌─────────┐   ┌──────────┐   ┌──────────┐
│Component│   │Mechanics │   │  Schema  │
│ System  │   │  System  │   │Validation│
└────┬────┘   └────┬─────┘   └────┬─────┘
     │             │              │
     └─────────────┼──────────────┘
                   ▼
        ┌─────────────────────┐
        │  Registry Layer     │
        │ (Centralized Init)  │
        └──────────┬──────────┘
                   │
        ┌──────────┴──────────┐
        ▼                     ▼
   ┌─────────┐         ┌──────────┐
   │  Data   │         │  Config  │
   │ Driven  │         │ Manager  │
   │Registry │         │          │
   └─────────┘         └──────────┘
```

### Design Principles

1. **Data-Driven**: All gameplay content defined in JSON
2. **Extensible**: Plugin system via public API
3. **Modular**: Components can be added/removed independently
4. **Thread-Safe**: Concurrent access properly synchronized
5. **Version-Safe**: Forward compatibility through schema validation
6. **Testable**: Unit tests for all core systems

---

## API Layer

### Getting the API Instance

```java
import dk.mosberg.entomology.api.EntomologyAPI;
import dk.mosberg.entomology.api.IEntomologyAPI;

public class MyMod {
    public void init() {
        IEntomologyAPI api = EntomologyAPI.getInstance();
        String version = api.getApiVersion(); // "1.0.0"
    }
}
```

### Registering Custom Mechanics

```java
import dk.mosberg.entomology.mechanics.IMechanic;
import net.minecraft.util.Identifier;

public class MyCustomMechanic implements IMechanic {
    private static final Identifier ID = new Identifier("mymod", "custom");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public int getPriority() {
        return 700; // Higher = executed first
    }

    @Override
    public boolean appliesTo(String specimenId, MechanicContext context) {
        // Return true if this mechanic should apply
        return specimenId.startsWith("mymod:");
    }

    @Override
    public MechanicResult execute(String specimenId, MechanicContext context) {
        // Implement mechanic logic
        return MechanicResult.success();
    }

    @Override
    public void configure(JsonObject config) {
        // Load configuration from JSON
    }
}

// Register the mechanic
api.registerMechanic(new MyCustomMechanic());
```

### Component Registration

```java
import dk.mosberg.entomology.component.ISpecimenComponent;

api.registerSpecimenComponent(
    new Identifier("mymod", "toxicity"),
    config -> new ToxicityComponent(config)
);
```

---

## Component System

Components allow attaching custom data and behavior to specimens without modifying core classes.

### Creating a Component

```java
public class ToxicityComponent implements ISpecimenComponent {
    private final Identifier type;
    private int toxicityLevel;

    public ToxicityComponent(JsonObject config) {
        this.type = new Identifier("mymod", "toxicity");
        this.toxicityLevel = config.get("level").getAsInt();
    }

    @Override
    public Identifier getType() {
        return type;
    }

    @Override
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("toxicity", toxicityLevel);
        return nbt;
    }

    @Override
    public void fromNbt(NbtCompound nbt) {
        toxicityLevel = nbt.getInt("toxicity");
    }

    @Override
    public ISpecimenComponent copy() {
        ToxicityComponent copy = new ToxicityComponent(new JsonObject());
        copy.toxicityLevel = this.toxicityLevel;
        return copy;
    }

    public int getToxicityLevel() {
        return toxicityLevel;
    }
}
```

### Using Components in JSON

```json
{
  "id": "poison_beetle",
  "name": "Poison Beetle",
  "components": {
    "mymod:toxicity": {
      "level": 5,
      "damage_per_second": 2
    }
  }
}
```

---

## Mechanics System

Mechanics define reusable gameplay behaviors that can be configured via JSON.

### Built-in Mechanics

#### Breeding Mechanic

- **Priority**: 600
- **Configurable**: Breeding pairs, offspring, mutations
- **Config File**: `config/entomology/mechanics.json`

```json
{
  "breeding": {
    "enabled": true,
    "baseChance": 0.25,
    "mutationChance": 0.05
  }
}
```

#### Environmental Mechanic

- **Priority**: 400
- **Features**: Biome preferences, temperature/humidity ranges, time-of-day
- **Spawn Weight**: Dynamically adjusts spawn rates

```json
{
  "environment": {
    "enabled": true,
    "updateInterval": 100
  }
}
```

### Mechanic Execution Order

Mechanics execute in **priority order** (highest first):

1. Custom mechanics (700+)
2. Breeding mechanic (600)
3. Default mechanics (500)
4. Environmental mechanic (400)
5. Cleanup mechanics (0-100)

---

## Configuration Management

Configuration is hierarchical and hot-reloadable:

```
Default Values → File Values → Runtime Overrides
```

### Configuration Files

Location: `config/entomology/`

- `core.json` - Logging, performance settings
- `mechanics.json` - Mechanic-specific settings
- `balance.json` - Gameplay balance values

### Accessing Configuration

```java
ConfigManager config = ConfigManager.getInstance();

// Get value with default
boolean enabled = config.get("mechanics", "breeding.enabled", true);
double chance = config.get("balance", "captureSuccessBase", 0.7);

// Set value (auto-saves)
config.set("mechanics", "breeding.enabled", false);

// Listen for changes
config.addListener("mechanics", newConfig -> {
    // React to configuration changes
});
```

### Configuration Hot-Reload

Files are monitored and automatically reloaded when changed. Use `/reload` command to force reload.

---

## Schema Validation

JSON schemas ensure data integrity and provide helpful error messages.

### Registering Validators

```java
import dk.mosberg.entomology.schema.BaseSchemaValidator;

public class MySchemaValidator extends BaseSchemaValidator {
    @Override
    protected void performValidation(JsonObject json) {
        requireStringField(json, "id");
        requireNumberInRange(json, "power", 1.0, 100.0);
        warnIfMissing(json, "description");
    }
}

api.registerSchemaValidator("specimens", new MySchemaValidator());
```

### Validation Results

```java
ValidationResult result = api.validateSchema("specimens", json);

if (!result.isValid()) {
    for (String error : result.getErrors()) {
        System.err.println("Error: " + error);
    }
}

for (String warning : result.getWarnings()) {
    System.out.println("Warning: " + warning);
}
```

---

## Extension Guide

### Creating an Extension Mod

**Step 1**: Add dependency to your `build.gradle`:

```gradle
repositories {
    // Add repository if needed
}

dependencies {
    modImplementation "dk.mosberg:entomology:1.0.0"
}
```

**Step 2**: Get API in your initializer:

```java
@Override
public void onInitialize() {
    IEntomologyAPI api = EntomologyAPI.getInstance();

    // Register your mechanics
    api.registerMechanic(new MyMechanic());

    // Register components
    api.registerSpecimenComponent(
        new Identifier("mymod", "mycomponent"),
        MyComponent::new
    );
}
```

**Step 3**: Add your JSON data files:

```
data/mymod/
  ├── specimens/
  │   └── my_specimen.json
  └── mechanics/
      └── my_mechanic_config.json
```

### Best Practices

1. **Namespace everything**: Use `modid:name` format
2. **Handle errors gracefully**: Never crash on invalid data
3. **Document schemas**: Provide JSON schema files
4. **Test thoroughly**: Write unit tests for mechanics
5. **Version carefully**: Use semantic versioning

---

## Performance Considerations

### Caching

The mod uses aggressive caching to minimize JSON parsing:

```java
// Config value
int cacheSize = config.get("core", "performance.cacheSize", 1000);
```

### Async Loading

Resource reloading happens asynchronously to avoid blocking:

```java
CompletableFuture<Void> reload = api.reloadData();
reload.thenRun(() -> {
    // Reload complete
});
```

### Optimization Tips

1. **Limit mechanic complexity**: Keep `appliesTo()` fast
2. **Batch operations**: Use multi-replacement tools
3. **Profile regularly**: Use JVM profiling tools
4. **Monitor logs**: Check for performance warnings

### Memory Usage

- Average: ~5MB for 100 specimens
- Peak during reload: ~10MB
- Scales linearly with content

---

## Testing

### Running Tests

```bash
./gradlew test
```

### Writing Tests

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@Test
void testMechanic() {
    MyMechanic mechanic = new MyMechanic();
    assertTrue(mechanic.appliesTo("test_specimen", context));
}
```

### Test Coverage

- Config system: ✅ 95%
- API layer: ✅ 90%
- Mechanics: ✅ 85%
- Components: ⚠️ 70% (WIP)

---

## Troubleshooting

### Common Issues

**Q: My mechanic isn't executing**

- Check priority order
- Verify `appliesTo()` returns true
- Check logs for registration errors

**Q: Config changes not applying**

- Ensure file is valid JSON
- Check file is in correct directory
- Try `/reload` command

**Q: Schema validation failing**

- Compare against schema documentation
- Check for required fields
- Verify data types match

### Debug Mode

Enable debug logging in `config/entomology/core.json`:

```json
{
  "logging": {
    "level": "DEBUG",
    "logMechanics": true
  }
}
```

---

## Migration Guide

### From Pre-Refactor Version

The refactor introduces breaking changes:

**Before:**

```java
DataDrivenRegistry.getSpecimen("bee");
```

**After:**

```java
EntomologyAPI api = EntomologyAPI.getInstance();
// Use API methods instead
```

### Version Compatibility

| API Version | Minecraft | Status     |
| ----------- | --------- | ---------- |
| 1.0.0       | 1.21.10   | ✅ Current |
| Future      | 1.21+     | 🔄 Planned |

---

## Contributing

See `CONTRIBUTING.md` for:

- Code style guidelines
- Pull request process
- Architecture decisions
- Roadmap

---

## License

MIT License - See `LICENSE` file for details
