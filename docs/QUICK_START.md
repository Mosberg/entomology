# Entomology Mod - Quick Start Guide

## Installation

1. Download the latest JAR from the releases page
2. Place `entomology-2.0.0.jar` in your Minecraft `mods/` folder
3. Launch Minecraft with Fabric Loader 0.15.0+
4. Configure via `config/entomologyjson/mechanics.json`

## Basic Usage

### In-Game Commands

```
/entomology reload      - Reload all configurations (requires OP level 2)
/entomology stats       - View telemetry statistics
/entomology validate    - Validate all configurations
```

### Configuration Files

Located in `config/entomologyjson/`:

- `mechanics.json` - Main mechanics configuration
- `specimens.json` - Specimen definitions
- `breeding.json` - Breeding rules

### Basic Configuration Example

```json
{
  "advanced_breeding": {
    "enabled": true,
    "globalMutationRate": 0.05,
    "inheritanceFidelity": 0.85,
    "breedingCooldown": 3000,
    "breedingPairs": []
  },
  "advanced_environmental": {
    "enabled": true,
    "checkInterval": 100,
    "strictMode": false,
    "specimens": {}
  }
}
```

## For Developers

### Adding Custom Mechanics

```java
public class MyMechanic extends AbstractMechanic {
    public MyMechanic() {
        super(
            EntomologyMod.id("my_mechanic"),
            "1.0.0",
            MechanicCategory.CUSTOM,
            500
        );
    }

    @Override
    protected void registerParameters() {
        registerParameter("enabled", "Enable mechanic", Boolean.class, true, false);
    }

    @Override
    protected IMechanicResult executeInternal(IMechanicContext context) {
        // Implementation here
        return IMechanicResult.success();
    }
}
```

### Registering Custom Mechanics

```java
// In your mod initialization
ComponentRegistry registry = ComponentRegistry.getInstance();
registry.register(
    EntomologyMod.id("my_mechanic"),
    IAdvancedMechanic.class,
    MyMechanic::new
);
```

## API Usage

### Executing Mechanics

```java
// Build context
IMechanicContext context = IMechanicContext.builder()
    .world(world)
    .position(blockPos)
    .type(IMechanicContext.ContextType.BREEDING)
    .data("key", value)
    .build();

// Execute mechanic
IAdvancedMechanic mechanic = registry.get(
    EntomologyMod.id("advanced_breeding"),
    IAdvancedMechanic.class
);
IMechanicResult result = mechanic.execute(context);

// Handle result
if (result.isSuccess()) {
    result.getData("offspring").ifPresent(offspring -> {
        // Handle offspring
    });
}
```

### Collecting Telemetry

```java
TelemetrySystem telemetry = TelemetrySystem.getInstance();
telemetry.recordMetric("breeding.success", 1.0);
telemetry.recordMetric("breeding.failure", 0.0);

// Get adjusted values
double adjustedRate = telemetry.getAdjustedValue("mutation.rate", 0.05);
```

## Troubleshooting

### Common Issues

**Issue:** Mod not loading
**Solution:** Ensure Fabric Loader 0.15.0+ and Fabric API 0.92.0+ are installed

**Issue:** Configuration not loading
**Solution:** Check `logs/latest.log` for JSON validation errors

**Issue:** Commands not working
**Solution:** Ensure you have OP level 2 (`/op <username>`)

### Debug Mode

Enable debug logging in `log4j2.xml`:

```xml
<Logger level="debug" name="dk.mosberg.entomology"/>
```

## Further Reading

- `API_DOCUMENTATION.md` - Complete API reference
- `CONTRIBUTING.md` - Development guidelines
- `INTEGRATION_GUIDE.md` - Integration examples
- `IMPLEMENTATION_SUMMARY.md` - Full system overview

## Support

- GitHub Issues: Report bugs and feature requests
- Wiki: Detailed documentation and guides
- Discord: Community support (if available)
