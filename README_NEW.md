# Entomology

[![Build Status](https://github.com/mosberg/entomology/workflows/Build%20and%20Test/badge.svg)](https://github.com/mosberg/entomology/actions)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.10-brightgreen.svg)](https://www.minecraft.net/)
[![API Version](https://img.shields.io/badge/API-1.0.0-blue.svg)](docs/ARCHITECTURE.md)

A **data-driven, extensible Minecraft mod** for collecting and studying insects with advanced breeding mechanics, environmental systems, and a powerful plugin API.

## ✨ Features

### 🎮 Gameplay

- **Bug Net**: Capture insects from the world
- **Specimen Jars**: Store and display captured specimens
- **Field Guide**: Browse collected specimens and research
- **Research Station**: Unlock new discoveries
- **Breeding System**: Breed specimens for rare mutations
- **Environmental Mechanics**: Specimens prefer specific biomes, temperatures, and times

### 🔧 Technical

- **100% Data-Driven**: All content defined in JSON
- **Extensible API**: Other mods can add mechanics, components, and specimens
- **Hot-Reload Config**: Change settings without restarting
- **Schema Validation**: Automatic validation of JSON data
- **Component System**: Attach custom behaviors to specimens
- **Modular Architecture**: Clean separation of concerns
- **Thread-Safe**: Concurrent access properly synchronized
- **Performance Optimized**: Aggressive caching and async loading

## 📦 Installation

### Requirements

- **Minecraft**: 1.21.10
- **Fabric Loader**: 0.18.1+
- **Fabric API**: 0.138.3+1.21.10
- **Java**: 21+

### Steps

1. Download the latest release from [Releases](https://github.com/mosberg/entomology/releases)
2. Place the `.jar` file in your `mods` folder
3. Launch Minecraft with Fabric

## 🚀 Quick Start

### For Players

1. **Craft a Bug Net** (stick + string in crafting table)
2. **Find insects** in the world (bees, butterflies, etc.)
3. **Right-click** to capture them
4. **Craft Specimen Jars** to store specimens
5. **Use the Field Guide** to track your collection
6. **Build a Research Station** to unlock breeding

### For Modders

```java
// Get the API
IEntomologyAPI api = EntomologyAPI.getInstance();

// Register a custom mechanic
api.registerMechanic(new MyCustomMechanic());

// Add a component type
api.registerSpecimenComponent(
    new Identifier("mymod", "toxicity"),
    ToxicityComponent::new
);

// Add JSON data
data/mymod/specimens/my_bug.json
```

See [ARCHITECTURE.md](docs/ARCHITECTURE.md) for full API documentation.

## 📖 Documentation

- **[Architecture Guide](docs/ARCHITECTURE.md)** - Complete technical documentation
- **[Contributing Guide](CONTRIBUTING.md)** - How to contribute
- **[JSON Schemas](docs/schemas/)** - Data format specifications
- **[Examples](src/main/resources/data/entomology/)** - Example JSON files

## 🏗️ Architecture

```
API Layer          ← Public extension interface
    ↓
Mechanics Layer    ← Breeding, environmental effects
    ↓
Component Layer    ← Modular specimen behaviors
    ↓
Registry Layer     ← Centralized initialization
    ↓
Config Layer       ← Hot-reloadable settings
```

**Key Design Principles:**

- ✅ Data-driven content
- ✅ Extensible plugin system
- ✅ Modular components
- ✅ Thread-safe operations
- ✅ Forward compatibility
- ✅ Comprehensive testing

## 🔨 Building

```bash
# Clone the repository
git clone https://github.com/mosberg/entomology.git
cd entomology

# Build the mod
./gradlew build

# Run the client
./gradlew runClient

# Run tests
./gradlew test
```

## 🧪 Testing

```bash
# Run all tests
./gradlew test

# Run with code coverage
./gradlew test jacocoTestReport

# Run checkstyle
./gradlew checkstyleMain checkstyleTest
```

## 📊 JSON Examples

### Specimen Definition

```json
{
  "id": "firefly",
  "name": "Firefly",
  "rarity": 0.3,
  "capture_targets": ["minecraft:silverfish"],
  "environmental": {
    "preferred_biomes": ["minecraft:swamp"],
    "time_preference": "night"
  },
  "components": {
    "entomology:bioluminescence": {
      "light_level": 8
    }
  }
}
```

### Breeding Configuration

```json
{
  "breeding_pairs": [
    {
      "parent1": "bee",
      "parent2": "bee",
      "offspring": ["bee"],
      "mutations": [
        {
          "result": "queen_bee",
          "chance": 0.05
        }
      ]
    }
  ]
}
```

See [docs/schemas/](docs/schemas/) for complete schemas.

## 🤝 Contributing

Contributions are welcome! Please read [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

### Development Workflow

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Run tests (`./gradlew test`)
5. Commit (`git commit -m 'Add amazing feature'`)
6. Push (`git push origin feature/amazing-feature`)
7. Open a Pull Request

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Fabric** - Mod loader and API
- **Mojang** - Minecraft
- **Contributors** - Everyone who has contributed to this project

## 📞 Contact

- **Issues**: [GitHub Issues](https://github.com/mosberg/entomology/issues)
- **Discussions**: [GitHub Discussions](https://github.com/mosberg/entomology/discussions)

---

_**Made with ❤️ by Mosberg for the Minecraft modding community**_
