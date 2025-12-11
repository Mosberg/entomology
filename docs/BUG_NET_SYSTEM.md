# Bug Net Data-Driven System - Quick Reference

## 📖 Overview

The bug net system is now fully data-driven, allowing for easy customization through JSON data packs. All bug net properties (durability, catch rate, range, etc.) are defined in JSON files.

---

## 📁 File Structure

```
src/main/resources/data/entomology/
├── items/
│   ├── basic_bug_net.json       (128 durability, 0.6 catch rate)
│   ├── iron_bug_net.json        (256 durability, 0.75 catch rate)
│   ├── golden_bug_net.json      (64 durability, 0.85 catch rate)
│   ├── diamond_bug_net.json     (512 durability, 0.9 catch rate)
│   └── netherite_bug_net.json   (768 durability, 0.95 catch rate, fireproof)
└── schemas/
    └── bug_net.schema.json      (JSON validation schema)
```

---

## 🔧 JSON Definition Format

```json
{
  "id": "netherite_bug_net",
  "tier": "netherite",
  "type": "bug_net",
  "durability": 768,
  "catch_rate": 0.95,
  "range": 10.0,
  "speed_bonus": 1.0,
  "enchantable": true,
  "enchantability": 15,
  "fireproof": true,
  "max_stack_size": 1,
  "durability_per_capture": 1,
  "repair_ingredient": "minecraft:netherite_ingot",
  "capture_targets": ["entomology:*"],
  "rarity_bonus": {
    "common": 1.0,
    "uncommon": 0.9,
    "rare": 0.75,
    "epic": 0.6,
    "legendary": 0.4
  },
  "special_abilities": {
    "multi_capture": true,
    "max_captures": 5,
    "capture_radius": 2.0,
    "auto_store": false,
    "capture_boss": false,
    "instant_capture": false
  }
}
```

---

## 🎮 Properties Explained

### Core Stats

- **`id`**: Unique identifier (must match filename without .json)
- **`tier`**: Display tier (basic, iron, golden, diamond, netherite)
- **`type`**: Always "bug_net"
- **`durability`**: Total uses before breaking
- **`max_stack_size`**: How many can stack (usually 1)
- **`durability_per_capture`**: Durability consumed per capture

### Capture Mechanics

- **`catch_rate`**: Base capture chance (0.0 - 1.0)
- **`range`**: Maximum capture distance in blocks
- **`speed_bonus`**: Multiplier for moving targets (1.0 = no bonus)
- **`capture_targets`**: Array of entity IDs (supports wildcards: "entomology:\*")

### Special Abilities

- **`multi_capture`**: Can capture multiple entities at once
- **`max_captures`**: Maximum entities per multi-capture
- **`capture_radius`**: Additional radius for multi-capture
- **`auto_store`**: Automatically store in inventory
- **`capture_boss`**: Can capture boss entities
- **`instant_capture`**: 100% catch rate (debug/creative)

### Rarity Bonuses

- **`rarity_bonus`**: Multipliers for different entity rarities
  - `common`: 1.0 (no penalty)
  - `uncommon`: 0.9 (slight penalty)
  - `rare`: 0.75 (harder to catch)
  - `epic`: 0.6 (much harder)
  - `legendary`: 0.4 (very difficult)

### Enchanting & Repair

- **`enchantable`**: Can be enchanted
- **`enchantability`**: Enchantment level (higher = better enchants)
- **`repair_ingredient`**: Item ID used for anvil repairs
- **`fireproof`**: Immune to fire/lava damage

---

## 🏗️ Code Architecture

### Loading System

```
Server Start
    ↓
ResourceLoader.get(ResourceType.SERVER_DATA)
    ↓
BugNetReloader.reload(ResourceManager)
    ↓
Loads *.json from data/entomology/items/*_bug_net.json
    ↓
Parses with BugNetDefinition constructor
    ↓
Stores in Map<String, BugNetDefinition>
```

### Registration System

```
EntomologyMod.registerContent()
    ↓
registerBugNet("basic_bug_net")
    ↓
BugNetReloader.get("basic_bug_net")
    ↓ (if found)
Applies durability, fireproof from JSON
    ↓
Creates BugNetItem with netId parameter
    ↓
Registered to Minecraft registry
```

### Runtime Usage

```
Player uses bug net
    ↓
BugNetItem.useOnEntity()
    ↓
BugNetReloader.get(netId)
    ↓
Gets BugNetDefinition
    ↓
Calculates catch chance with rarity bonuses
    ↓
Applies speed bonus if target moving
    ↓
Multi-capture if enabled
    ↓
Damages item based on durability_per_capture
```

---

## 🛠️ Adding a New Bug Net Tier

### Step 1: Create JSON Definition

Create `src/main/resources/data/entomology/items/emerald_bug_net.json`:

```json
{
  "id": "emerald_bug_net",
  "tier": "emerald",
  "type": "bug_net",
  "durability": 384,
  "catch_rate": 0.8,
  "range": 7.0,
  "speed_bonus": 1.15,
  "enchantable": true,
  "enchantability": 14,
  "fireproof": false,
  "max_stack_size": 1,
  "durability_per_capture": 1,
  "repair_ingredient": "minecraft:emerald",
  "capture_targets": ["entomology:*"],
  "rarity_bonus": {
    "common": 1.0,
    "uncommon": 0.95,
    "rare": 0.85,
    "epic": 0.7,
    "legendary": 0.5
  },
  "special_abilities": {
    "multi_capture": true,
    "max_captures": 3,
    "capture_radius": 1.5,
    "auto_store": false,
    "capture_boss": false,
    "instant_capture": false
  }
}
```

### Step 2: Add Registration Code

In `EntomologyMod.java`, add to `registerContent()`:

```java
public static Item emeraldBugNet; // Add field at top

private void registerContent() {
    // ... existing bug nets ...
    emeraldBugNet = registerBugNet("emerald_bug_net"); // Add this line
    // ... rest of registration ...
}
```

### Step 3: Add to Creative Tab (Optional)

In `EntomologyMod.java`, in `onInitialize()`:

```java
ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
    entries.add(basicBugNet);
    entries.add(ironBugNet);
    entries.add(emeraldBugNet); // Add this line
    entries.add(goldenBugNet);
    entries.add(diamondBugNet);
    entries.add(netheriteBugNet);
});
```

### Step 4: Add Textures

- Create `src/main/resources/assets/entomology/textures/item/emerald_bug_net.png` (16x16)
- Create model JSON if needed

### Step 5: Build and Test

```powershell
.\gradlew build
```

That's it! The new bug net tier is fully functional with all JSON-defined properties.

---

## 📦 Data Pack Customization

### Override Existing Bug Net

Create data pack at `datapacks/custom_balance/data/entomology/items/diamond_bug_net.json`:

```json
{
  "id": "diamond_bug_net",
  "tier": "diamond",
  "type": "bug_net",
  "durability": 1000,
  "catch_rate": 0.95,
  "range": 12.0,
  "speed_bonus": 1.0,
  "enchantable": true,
  "enchantability": 20,
  "fireproof": false,
  "max_stack_size": 1,
  "durability_per_capture": 1,
  "repair_ingredient": "minecraft:diamond",
  "capture_targets": ["entomology:*"],
  "rarity_bonus": {
    "common": 1.0,
    "rare": 0.9,
    "legendary": 0.7
  },
  "special_abilities": {
    "multi_capture": true,
    "max_captures": 8,
    "capture_radius": 3.0,
    "capture_boss": true
  }
}
```

The data pack will override the mod's default values. No code changes needed!

---

## 🔍 Validation

### JSON Schema

The schema at `src/main/resources/data/entomology/schemas/bug_net.schema.json` validates:

- Required fields
- Value ranges (catch_rate 0.0-1.0, durability > 0, etc.)
- Property types (numbers, booleans, strings)
- Array contents (capture_targets, rarity_bonus keys)

### Runtime Checks

`BugNetDefinition` constructor validates:

- Non-null required fields
- Valid ranges for numerical values
- Proper identifier format for capture_targets
- Special abilities configuration

---

## 📝 Best Practices

### Balance Guidelines

1. **Durability:** Higher tier = more durability (64-768 range)
2. **Catch Rate:** Basic 0.6 → Netherite 0.95 (never 1.0 except debug)
3. **Range:** 5-10 blocks (higher tier = longer range)
4. **Multi-Capture:** Limit to 3-5 captures (balance vs. overpowered)
5. **Rarity Penalties:** Legendary should be ~0.4-0.5 (challenging)

### Performance

- Keep `capture_targets` specific (avoid too many wildcards)
- Limit `max_captures` to reasonable values (< 10)
- Use `capture_radius` carefully (large = performance impact)

### Compatibility

- Use standard Minecraft IDs for `repair_ingredient`
- Check capture_targets exist before adding
- Test with different entity types (flying, ground, boss)

---

## 🐛 Debugging

### Log Messages

```
[INFO] Loaded bug net: basic_bug_net (tier: basic, catch rate: 0.6, range: 5.0, durability: 128)
[INFO] Loaded 5 bug net definitions
[INFO] Registered bug net basic_bug_net with durability 128 from JSON
```

If JSON is missing:

```
[WARN] Bug net basic_bug_net registered with fallback durability 128 (JSON not loaded)
```

### Common Issues

1. **Bug net uses wrong durability**

   - Check JSON file location: `data/entomology/items/*_bug_net.json`
   - Verify `id` field matches filename
   - Check BugNetReloader loaded definitions (log messages)

2. **Fireproof not working**

   - Ensure `fireproof: true` in JSON
   - Only netherite tier usually has this

3. **Multi-capture not working**
   - Check `multi_capture: true` in special_abilities
   - Verify `max_captures` > 1
   - Check `capture_radius` is reasonable

---

## 🎓 Example: Creative-Only Debug Net

```json
{
  "id": "debug_bug_net",
  "tier": "debug",
  "type": "bug_net",
  "durability": 9999,
  "catch_rate": 1.0,
  "range": 50.0,
  "speed_bonus": 1.0,
  "enchantable": false,
  "enchantability": 0,
  "fireproof": true,
  "max_stack_size": 1,
  "durability_per_capture": 0,
  "repair_ingredient": "minecraft:air",
  "capture_targets": ["minecraft:*", "entomology:*"],
  "rarity_bonus": {
    "common": 1.0,
    "legendary": 1.0
  },
  "special_abilities": {
    "multi_capture": true,
    "max_captures": 100,
    "capture_radius": 20.0,
    "auto_store": true,
    "capture_boss": true,
    "instant_capture": true
  }
}
```

Features:

- Infinite durability (no consumption)
- 100% catch rate
- 50 block range
- Can capture anything (including bosses)
- Perfect for testing/debugging

---

## 📚 Related Files

- **BugNetDefinition.java** - Data class parsing JSON
- **BugNetReloader.java** - Resource reloader loading JSON files
- **BugNetItem.java** - Item class using definitions
- **EntomologyMod.java** - Registration system
- **bug_net.schema.json** - JSON validation schema

---

**Quick Reference Version:** 1.0
**Last Updated:** December 2025
**Status:** ✅ Production Ready
