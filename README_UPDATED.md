# Entomology - Advanced Insect Collection Mod

[![Build Status](https://img.shields.io/github/workflow/status/Mosberg/entomology/Build%20and%20Test)](https://github.com/Mosberg/entomology/actions)
[![License](https://img.shields.io/github/license/Mosberg/entomology)](LICENSE)
[![Version](https://img.shields.io/github/v/release/Mosberg/entomology)](https://github.com/Mosberg/entomology/releases)
[![Discord](https://img.shields.io/discord/placeholder?color=7289da&label=Discord&logo=discord&logoColor=white)](https://discord.gg/placeholder)

A comprehensive, data-driven Minecraft mod for collecting, breeding, and researching insects. Built with extensibility, performance, and maintainability in mind.

## ✨ Features

### 🦋 Specimen Collection

- **50+ unique insect species** with distinct behaviors
- **Rarity tiers** from common to legendary
- **Bug nets** in multiple tiers (basic → netherite)
- **Environmental requirements** for realistic spawning
- **Day/night cycles** affecting specimen activity

### 🧬 Advanced Breeding System

- **Genetic trait inheritance** with dominant/recessive genes
- **Mutations** for discovering rare species
- **Breeding compatibility** matrix
- **Trait visualization** in specimen details
- **Fully configurable** breeding pairs and offspring

### 🔬 Research & Progression

- **Research station** for studying specimens
- **Field guide** with discovered species encyclopedia
- **Advancement system** rewarding collection milestones
- **Specimen jars** for storage and display
- **Display cases** for showcasing collections

### ⚙️ Data-Driven Architecture

- **JSON-configured** specimens, mechanics, and balance
- **Hot-reload support** for live configuration changes
- **Schema validation** ensuring data integrity
- **Version migration** for forward compatibility
- **Extensible API** for community content

### 📊 Balance & Telemetry

- **Adaptive difficulty** adjusting to player behavior
- **Telemetry system** tracking gameplay metrics
- **Dynamic spawn rates** based on capture frequency
- **Configurable tuning** for servers and modpacks

## 🚀 Quick Start

### Installation

1. **Download** the latest release from [Releases](https://github.com/Mosberg/entomology/releases)
2. **Install** [Fabric Loader](https://fabricmc.net/use/)
3. **Place** the mod JAR in your `mods` folder
4. **Launch** Minecraft

### First Steps

1. **Craft a basic bug net** (sticks + string)
2. **Find insects** spawning in various biomes
3. **Capture specimens** by right-clicking
4. **Build a research station** to study them
5. **Breed insects** to discover new species
6. **Complete advancements** to unlock everything

## 🛠️ For Developers

### Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│                    Entomology Mod                       │
├─────────────────────────────────────────────────────────┤
│  API Layer          │ Extensible plugin system          │
│  Registry Layer     │ Component & lifecycle management  │
│  Config Layer       │ Schema-driven, hot-reload         │
│  Mechanics Engine   │ Breeding, spawning, research      │
│  Balance System     │ Telemetry & adaptive tuning       │
│  Data Layer         │ JSON definitions & validation     │
└─────────────────────────────────────────────────────────┘
```

### Key APIs

#### Data Provider API

```java
public class CustomDataProvider implements IDataProvider<SpeciesData> {
    @Override
    public CompletableFuture<Void> loadData(JsonObject config) {
        // Load custom species data
    }
}
```

#### Advanced Mechanics API

```java
public class CustomMechanic extends AbstractMechanic {
    @Override
    protected IMechanicResult executeInternal(IMechanicContext ctx) {
        // Implement custom gameplay logic
    }
}
```

#### Balance Tuner API

```java
public class CustomTuner implements IBalanceTuner {
    @Override
    public double computeAdjustedValue(double base, ITelemetryData telemetry) {
        // Adaptive balance adjustment
    }
}
```

### Documentation

- **[API Documentation](docs/API_DOCUMENTATION.md)** - Comprehensive API reference
- **[Contributing Guide](CONTRIBUTING.md)** - How to contribute
- **[Architecture](docs/ARCHITECTURE.md)** - System design details
- **[JavaDocs](https://mosberg.github.io/entomology/javadoc/)** - API reference

### Building from Source

```bash
# Clone repository
git clone https://github.com/Mosberg/entomology.git
cd entomology

# Build mod
./gradlew build

# Run development client
./gradlew runClient

# Run tests
./gradlew test

# Run with hot-reload
./gradlew runClient --continuous
```

### Testing

```bash
# Run all tests
./gradlew test

# Run with coverage
./gradlew test jacocoTestReport

# Run specific test
./gradlew test --tests ComponentRegistryTest

# Run checkstyle
./gradlew checkstyleMain checkstyleTest
```

## 📦 Configuration

### Config Files

Located in `config/entomology/`:

- **`mechanics.json`** - Breeding, spawning, environmental settings
- **`balance.json`** - Difficulty scaling and tuning parameters
- **`telemetry.json`** - Metrics collection settings

### Example Configuration

```json
{
  "version": "2.0.0",
  "breeding": {
    "enabled": true,
    "globalMutationRate": 0.05,
    "breedingCooldown": 6000
  },
  "spawning": {
    "enableDynamicRates": true,
    "baseSpawnWeight": 10
  },
  "balance": {
    "adaptiveDifficulty": true,
    "telemetryEnabled": true
  }
}
```

### Data Packs

Add custom specimens via data packs in `data/entomology/specimen/`:

```json
{
  "id": "mypack:custom_beetle",
  "displayName": "specimen.mypack.custom_beetle",
  "rarity": "rare",
  "spawnBiomes": ["minecraft:forest"],
  "traits": {
    "size": { "level": 8, "inheritable": true }
  }
}
```

## 🎮 Gameplay

### Bug Nets

| Tier      | Durability | Capture Chance | Special |
| --------- | ---------- | -------------- | ------- |
| Basic     | 64         | 50%            | -       |
| Iron      | 256        | 70%            | -       |
| Golden    | 192        | 90%            | Fast    |
| Diamond   | 512        | 85%            | Durable |
| Netherite | 1024       | 95%            | Best    |

### Rarity Tiers

- **Common** (70%) - Found everywhere
- **Uncommon** (20%) - Specific biomes
- **Rare** (8%) - Limited spawns
- **Epic** (1.8%) - Very rare
- **Legendary** (0.2%) - Extremely rare, often requires breeding

### Breeding Examples

```
Butterfly + Butterfly → Butterfly (50%)
                      → Monarch Butterfly (5% mutation)

Bee + Bee → Bee (80%)
          → Honey Bee (10% mutation)

Spider + Cave Spider → Cave Dweller Spider (30%)
```

## 🔧 Compatibility

### Requirements

- **Minecraft**: 1.21.x
- **Fabric Loader**: 0.15.0+
- **Fabric API**: 0.92.0+
- **Java**: 21+

### Tested Mods

✅ Compatible with:

- REI (Roughly Enough Items)
- JEI (Just Enough Items)
- Mod Menu
- WTHIT / Jade
- Biome mods (Biomes O' Plenty, etc.)

⚠️ May conflict with:

- Mods that heavily modify mob spawning
- Custom entity rendering mods

## 🐛 Troubleshooting

### Common Issues

**Issue**: Specimens not spawning
**Solution**: Check biome requirements, ensure spawn rates in config

**Issue**: Breeding not working
**Solution**: Verify compatibility, check breeding cooldown

**Issue**: Config changes not applying
**Solution**: Use `/entomology reload` or restart game

**Issue**: Performance issues
**Solution**: Reduce spawn rates, disable telemetry, lower view distance

### Debug Commands

```
/entomology reload          # Reload configuration
/entomology spawn <id>      # Spawn specimen
/entomology stats           # Show telemetry stats
/entomology validate        # Validate all data files
```

## 🤝 Contributing

We welcome contributions! See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

### Ways to Contribute

- 🐛 **Report bugs** via GitHub Issues
- 💡 **Suggest features** in Discussions
- 📝 **Improve documentation**
- 🎨 **Create textures** for new specimens
- 🔧 **Fix bugs** with pull requests
- 🌍 **Add translations**
- 📦 **Create data packs**

### Contributors

Thanks to all contributors! See [CONTRIBUTORS.md](CONTRIBUTORS.md).

## 📄 License

This project is licensed under the MIT License - see [LICENSE](LICENSE) for details.

## 🔗 Links

- **GitHub**: https://github.com/Mosberg/entomology
- **Discord**: [Join our community]
- **Wiki**: https://github.com/Mosberg/entomology/wiki
- **Issues**: https://github.com/Mosberg/entomology/issues
- **Modrinth**: [Coming soon]
- **CurseForge**: [Coming soon]

## 🙏 Acknowledgments

- Fabric team for the modding framework
- Minecraft community for inspiration
- Contributors and testers
- Open source dependencies

## 📊 Stats

- **50+** unique specimens
- **5** bug net tiers
- **100+** possible breeding combinations
- **20+** traits with inheritance
- **4** environmental factors
- **8** biome-specific spawns

## 🗺️ Roadmap

### Version 2.1 (Next)

- [ ] Custom entity models
- [ ] Specimen animations
- [ ] Sound effects
- [ ] More breeding pairs
- [ ] Research notes system

### Version 2.2 (Future)

- [ ] Multiplayer sync improvements
- [ ] Additional mechanics (migration, swarms)
- [ ] Integration with other mods
- [ ] Performance optimizations
- [ ] Advanced display options

### Version 3.0 (Long-term)

- [ ] Specimen ecosystems
- [ ] Weather effects
- [ ] Seasonal variations
- [ ] Custom biome spawning
- [ ] Advanced genetics visualization

## 📸 Screenshots

[Add screenshots here]

## 🎬 Videos

[Add video links here]

---

**Made with ❤️ by Mosberg**

_If you enjoy this mod, consider giving it a ⭐ on GitHub!_
