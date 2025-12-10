# Entomology Schema Documentation

This directory contains JSON Schema files for validating all data structures used in the Entomology mod. These schemas follow the JSON Schema Draft 2020-12 specification and are compatible with Minecraft 1.21.10 data formats.

## 📋 Schema Files Overview

### Core Gameplay Schemas

#### `specimen.schema.json`

Defines insect specimen data structures including:

- Entity properties and behavior traits
- Environmental preferences and habitat requirements
- Breeding mechanics and genetic traits
- Capture difficulty and rarity settings
- Taxonomic classification

#### `research.schema.json`

Defines research progression system:

- Multi-stage research requirements
- Prerequisites and unlock trees
- Specimen observation requirements
- Experience rewards and unlocks
- Discovery facts and related research

#### `bug_net.schema.json`

Defines bug net tool configurations:

- Capture targets and success rates
- Durability and tier progression
- Special abilities (multi-capture, radius, etc.)
- Tool enchantability and repair materials

#### `breeding_config.schema.json`

Defines breeding mechanics:

- Valid breeding pairs and offspring
- Mutation conditions and probabilities
- Environmental requirements
- Egg-laying mechanics
- Global breeding settings

### Block and Item Schemas

#### `block.schema.json`

Defines functional block configurations:

- Container and display mechanics
- GUI types and inventory sizes
- Automation support (hoppers, redstone)
- Particle and sound effects
- Multiblock structures

#### `item.schema.json`

Defines item properties:

- Tool, consumable, and material types
- Durability and enchantability
- Food values and potion effects
- Spawn egg configurations
- Research item properties

### World Generation Schemas

#### `worldgen_biome.schema.json`

Defines custom biome data (Minecraft 1.19+ format):

- Temperature and precipitation
- Visual effects (fog, water, sky colors)
- Music and ambient sounds
- Mob spawning configuration
- Feature generation stages

#### `entity.schema.json`

Defines entity type configurations:

- Hitbox dimensions and attributes
- Behavioral properties
- Natural spawning rules
- Sound events and loot tables
- Client-side rendering

### Vanilla-Compatible Schemas

#### `recipe.schema.json`

Crafting recipe definitions (vanilla format):

- Shaped and shapeless crafting
- Smelting, blasting, smoking
- Stonecutting and smithing
- All vanilla recipe types supported

#### `advancement.schema.json`

Achievement/advancement definitions (vanilla format):

- Display properties and icons
- Criteria and requirements
- Rewards (recipes, loot, experience)
- Parent-child relationships

#### `loot_table.schema.json`

Loot table definitions (vanilla format):

- Multiple loot pools
- Weighted entries
- Conditions and functions
- All vanilla loot types supported

#### `tag.schema.json`

Tag definitions (vanilla format):

- Item, block, and entity tags
- Tag inheritance (# prefix)
- Required/optional entries

#### `damage_type.schema.json`

Damage type definitions (Minecraft 1.19.4+ format):

- Death message configuration
- Difficulty scaling
- Exhaustion values
- Visual and audio effects

### Configuration Schemas

#### `mechanics.schema.json`

Global mechanics configuration:

- Breeding system settings
- Environmental mechanics
- Research system toggles
- Capture mechanics
- Spawning and difficulty scaling
- Performance optimization

## 🔧 Usage

### IDE Integration

Most modern IDEs support JSON Schema validation automatically:

**Visual Studio Code:**

```json
{
  "$schema": "../schema/specimen.schema.json"
}
```

**IntelliJ IDEA:**
Settings → Languages & Frameworks → Schemas and DTDs → JSON Schema Mappings

### Command-Line Validation

Using `ajv-cli`:

```bash
npm install -g ajv-cli
ajv validate -s schema/specimen.schema.json -d specimen/bee.json
```

Using Python `jsonschema`:

```python
import json
import jsonschema

with open('schema/specimen.schema.json') as schema_file:
    schema = json.load(schema_file)

with open('specimen/bee.json') as data_file:
    data = json.load(data_file)

jsonschema.validate(instance=data, schema=schema)
```

## 📐 Schema Relationships

```
specimen.schema.json
├── Used by: /specimen/*.json
└── References: entity types, items, tags

research.schema.json
├── Used by: /research/*.json
└── References: specimen IDs, breeding recipes

breeding_config.schema.json
├── Used by: /mechanics/breeding_config.json
└── References: specimen IDs, blocks, biomes

bug_net.schema.json
├── Used by: /items/*_bug_net.json
└── References: entity types

block.schema.json
├── Used by: /blocks/*.json
└── References: items, sounds, particles

entity.schema.json
├── Used by: Custom entity definitions
└── References: biomes, loot tables, sounds

recipe.schema.json
├── Used by: /recipe/*.json
└── References: items, tags

advancement.schema.json
├── Used by: /advancement/*.json
└── References: items, recipes, loot tables

loot_table.schema.json
├── Used by: Loot table definitions
└── References: items, tags

mechanics.schema.json
├── Used by: /mechanics/*.json
└── Global configuration schema
```

## 🎯 Common Patterns

### Pattern Validation

All identifier patterns follow Minecraft naming conventions:

- `^[a-z0-9_]+$` - Simple identifiers (no namespace)
- `^(minecraft:|entomology:)[a-z0-9_]+$` - Namespaced identifiers
- `^[a-z0-9_]+_research$` - Research ID pattern

### Enumerations

Enums ensure valid values:

- Rarity: `COMMON`, `UNCOMMON`, `RARE`, `EPIC`, `LEGENDARY`
- Size: `TINY`, `SMALL`, `MEDIUM`, `LARGE`, `HUGE`
- Frame: `task`, `challenge`, `goal`

### Conditional Requirements

Many schemas use `oneOf`, `anyOf`, or conditional `if-then` rules:

```json
"oneOf": [
  { "required": ["entity_type", "display_name_key"] },
  { "required": ["name", "description"] }
]
```

## 🔍 Validation Best Practices

1. **Always include `$schema`** at the top of JSON files
2. **Use required fields** to catch missing data early
3. **Leverage enums** for restricted value sets
4. **Test with schema validators** before runtime
5. **Keep schemas updated** with code changes

## 📚 Schema Standards

All schemas follow:

- **JSON Schema Draft 2020-12**
- **Minecraft 1.21.10 data formats**
- **Fabric mod conventions**
- **Semantic versioning** via `$id` URIs

## 🐛 Common Validation Errors

### Missing Required Fields

```
Error: data should have required property 'id'
Solution: Add all required fields defined in schema
```

### Invalid Pattern

```
Error: data/entity_type should match pattern "^(minecraft:|entomology:)[a-z0-9_]+$"
Solution: Ensure proper namespace prefix (minecraft: or entomology:)
```

### Type Mismatch

```
Error: data/durability should be integer
Solution: Remove quotes from numeric values
```

### Out of Range

```
Error: data/capture_chance should be <= 1.0
Solution: Values must be within specified min/max bounds
```

## 📖 Additional Resources

- [JSON Schema Documentation](https://json-schema.org/)
- [Minecraft Wiki - Data Pack Format](https://minecraft.wiki/w/Data_pack)
- [Fabric Wiki - Data Generation](https://fabricmc.net/wiki/tutorial:datagen_setup)

## 📝 Schema Versioning

Schema `$id` URIs follow this pattern:

```
https://entomology.mod/schemas/{schema_name}.schema.json
```

When making breaking changes:

1. Update the schema version in `$id`
2. Document changes in this README
3. Provide migration guides for existing data files

---

**Last Updated:** December 10, 2025
**Schema Version:** 1.0.0
**Minecraft Version:** 1.21.10
**Mod Version:** 1.0.0
