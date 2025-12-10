# Entomology - Data-Driven Fabric Mod

A JSON-driven Minecraft mod for insect collection and research, built for Fabric 1.21.10.

## ✨ New Features (Latest Update)

### Visual Enhancements

- 🎨 **HUD Overlay**: Real-time specimen information display with color-coded rarity tiers
- 📝 **Enhanced Tooltips**: Detailed specimen information with size, breeding status, and advanced debug info
- ✨ **Particle Effects**: Breeding visualization and environmental preference indicators
- 🖥️ **Research Station GUI**: Fully functional inventory interface

### Progression System

- 🏆 **4 Advancements**: From first capture to legendary collector
- 🎯 **Rarity Tiers**: Common, Uncommon, Rare, Epic, Legendary specimens
- 🧬 **Breeding System**: Backend support for specimen breeding mechanics
- 📚 **Experience Rewards**: Earn XP for rare discoveries

### Technical

- ✅ **Zero Compilation Errors**: Clean build with 0 Checkstyle violations
- 🏗️ **Modular Architecture**: 11 packages with clear separation of concerns
- 📦 **40+ Files**: Comprehensive implementation across client and server
- 🔄 **Backward Compatible**: Legacy JSON support with sensible defaults

**See [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md) for complete feature list.**

---

## Project Overview

This mod features a completely data-driven architecture where all items, blocks, entities, and research data are defined in external JSON files, allowing new content to be added without recompiling.

### Core Features

- **Bug Net**: Captures insects defined in JSON configuration
- **Specimen Jar**: Stores captured insects (item + block)
- **Research Station**: Identifies specimens and unlocks research entries
- **Field Guide**: Paginated book UI displaying researched insect data
- **HUD & Tooltips**: Real-time specimen information display
- **Particle Systems**: Visual feedback for breeding and environment
- **Advancements**: Progression tracking for collectors

### Architecture

```java
src/
├── main/java/
│   └── dk/mosberg/entomology/
│       ├── EntomologyMod.java          # Main mod initializer
│       ├── data/
│       │   ├── DataDrivenRegistry.java     # JSON loader with resource reloaders
│       │   ├── ItemDefinition.java         # Item data model
│       │   ├── BlockDefinition.java        # Block data model
│       │   ├── SpecimenDefinition.java     # Specimen data model
│       │   └── ResearchEntryDefinition.java # Research entry data model
│       ├── item/
│       │   ├── BugNetItem.java
│       │   ├── SpecimenJarItem.java
│       │   └── FieldGuideItem.java
│       ├── block/
│       │   ├── SpecimenJarBlock.java
│       │   └── ResearchStationBlock.java
│       └── block/entity/
│           ├── SpecimenJarBlockEntity.java
│           └── ResearchStationBlockEntity.java
├── client/java/
│   └── dk/mosberg/entomology/client/
│       ├── EntomologyClient.java       # Client initializer
│       ├── ClientProxy.java                # Client-side utilities
│       └── screen/
│           └── FieldGuideScreen.java       # Field guide UI
└── resources/
    ├── fabric.mod.json                     # Mod metadata
    ├── data/entomology/
    │   ├── items/*.json                    # Item definitions
    │   ├── blocks/*.json                   # Block definitions
    │   ├── specimens/*.json                # Insect specimens
    │   └── research/*.json                 # Research entries
    └── assets/entomology/
        ├── lang/en_us.json                 # Localization
        ├── models/item/*.json              # Item models
        ├── models/block/*.json             # Block models
        └── blockstates/*.json              # Block states
```

## Project Configuration

- **Minecraft**: 1.21.10
- **Fabric Loader**: 0.18.1
- **Fabric API**: 0.138.3+1.21.10
- **Yarn Mappings**: 1.21.10+build.3
- **Loom**: 1.14-SNAPSHOT
- **Java**: 21
- **Gradle**: 9.2.1

## JSON Data Format

### Specimen Definition (`data/entomology/specimens/*.json`)

```json
{
  "id": "bee",
  "entity_type": "minecraft:bee",
  "display_name_key": "specimen.entomology.bee",
  "description_key": "specimen.entomology.bee.desc"
}
```

### Item Definition (`data/entomology/items/*.json`)

```json
{
  "id": "bug_net",
  "type": "tool",
  "capture_targets": ["minecraft:bee", "minecraft:silverfish"],
  "durability": 128
}
```

### Block Definition (`data/entomology/blocks/*.json`)

```json
{
  "id": "specimen_jar",
  "capacity": 1,
  "display_entity": true
}
```

### Research Entry (`data/entomology/research/*.json`)

```json
{
  "id": "bee_research",
  "specimen_id": "bee",
  "page_key": "research.entomology.bee.page"
}
```

## Current Status

### ✅ Completed

- Project structure scaffolding
- Gradle build configuration with split source sets
- Data model classes (records)
- JSON resource reloaders
- Complete gameplay items and blocks
- Client-side UI framework
- Sample JSON data files (bee, silverfish specimens)
- Asset files (models, blockstates, lang)

### ⚠️ Known Issues

The project currently has compilation errors due to Minecraft 1.21.10 API changes:

1. **Block Entity NBT Methods**: `writeNbt`/`readNbt` signature changes
2. **ItemStack Damage API**: Changed from lambda to `Hand`/`EquipmentSlot`
3. **BlockEntityType Constructor**: Requires `Set<Block>` instead of single `Block`
4. **NBT getString()**: Now returns `Optional<String>`
5. **World.isClient**: Changed from field to method `isClient()`
6. **Resource API**: `Resource.get()` changed to `Resource.getInputStream()`
7. **Item Use Return**: `TypedActionResult` vs `ActionResult` API changes
8. **ItemStack NBT**: Moved to component system with `DataComponentTypes`

### 🔧 Required Fixes

To complete the project, these API incompatibilities need resolution:

```java
// 1. Fix BlockEntity NBT (remove WrapperLookup parameter)
@Override
public void writeNbt(NbtCompound nbt) {
    super.writeNbt(nbt);
    // ...
}

// 2. Fix ItemStack.damage()
stack.damage(1, user, Hand.MAIN_HAND);

// 3. Fix BlockEntityType with Set.of()
new BlockEntityType<>(factory, Set.of(SPECIMEN_JAR_BLOCK))

// 4. Fix NB string with .orElse()
specimenId = nbt.getString("SpecimenId").orElse("");

// 5. Fix World.isClient()
if (world.isClient()) { ... }

// 6. Fix ItemStack NBT with components
stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
```

## Building

```powershell
.\gradlew.bat build
```

## Adding New Insects

1. Create specimen definition: `data/entomology/specimens/new_insect.json`
2. Create research entry: `data/entomology/research/new_insect.json`
3. Add localization keys to `assets/entomology/lang/en_us.json`
4. Add entity type to bug net's `capture_targets` array

No code recompilation required!

## Schema Validation

The `DataDrivenRegistry` performs validation on load:

- Required fields checked
- Type validation for known structures
- Cross-references validated (e.g., research → specimen)
- Helpful error messages in logs

## Future Enhancements

- [ ] Fix Minecraft 1.21.10 API compatibility
- [ ] Add block entity renderers for specimen jars
- [ ] Implement research station GUI
- [ ] Add more default specimens
- [ ] Create custom textures (currently placeholders)
- [ ] Add breeding mechanics
- [ ] Implement collection bonuses
- [ ] Add achievement/advancement system

## License

MIT License - See LICENSE file

## Credits

Built with Fabric framework and Yarn mappings.
