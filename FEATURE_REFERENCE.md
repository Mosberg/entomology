# Quick Feature Reference

## 🎮 In-Game Features

### HUD Overlay

**Location**: Top-right corner when holding specimen jar

**Displays**:

- Specimen name and entity type
- **Rarity** (color-coded):
  - 🟤 Common (default)
  - 🟢 Uncommon
  - 🔵 Rare
  - 🟣 Epic
  - 🟡 Legendary
- Size: TINY / SMALL / MEDIUM / LARGE
- Experience Value (XP earned when studied)
- Breeding Status: ✓ Can Breed / ✗ Cannot Breed

**Implementation**: `SpecimenHudOverlay.java`

---

### Enhanced Tooltips

**Trigger**: Hover over specimen jar in inventory

**Shows**:

- All HUD information in compact format
- Color-coded rarity text
- **Advanced Mode (F3+H)**: Shows entity type ID

**Implementation**: `SpecimenTooltipHandler.java`

---

### Research Station GUI

**How to Open**: Right-click Research Station block

**Slots**:

- **Left**: Specimen Jar (input)
- **Right**: Field Guide (for recording)

**Features**:

- Shift-click support
- Inventory synchronization
- Simple bordered interface

**Implementation**: `ResearchStationScreen.java`, `ResearchStationScreenHandler.java`

---

### Particle Effects

#### Breeding Particles

**Methods** (call from breeding mechanics):

```java
BreedingParticleEffects.spawnBreedingHearts(world, pos)      // 7 hearts
BreedingParticleEffects.spawnBreedingSuccess(world, pos)     // Hearts + happy villagers
BreedingParticleEffects.spawnBreedingFailure(world, pos)     // Smoke particles
BreedingParticleEffects.spawnMutationEffect(world, pos)      // Enchantment sparkles
```

#### Environmental Particles

**Methods** (for habitat indicators):

```java
EnvironmentalParticleEffects.spawnHeatEffect(world, pos)      // Flame
EnvironmentalParticleEffects.spawnColdEffect(world, pos)      // Snowflakes
EnvironmentalParticleEffects.spawnHumidityEffect(world, pos)  // Water drips
EnvironmentalParticleEffects.spawnNightEffect(world, pos)     // Portal particles
EnvironmentalParticleEffects.spawnOptimalEffect(world, pos)   // Happy villager
EnvironmentalParticleEffects.spawnPoorEffect(world, pos)      // Angry villager
```

**Implementation**: `BreedingParticleEffects.java`, `EnvironmentalParticleEffects.java`

---

### Advancement System

#### 1. Root: "Entomology"

**Trigger**: Obtain a specimen jar (empty or full)
**Icon**: Glass Bottle
**Reward**: None

#### 2. First Capture: "First Catch"

**Trigger**: Obtain specimen jar with captured specimen
**Icon**: Specimen Jar
**Frame**: Task
**Reward**: None

#### 3. Collector: "Collector"

**Trigger**: Have 10 or more specimen jars in inventory
**Icon**: Specimen Jar
**Frame**: Goal (purple)
**Reward**: None

#### 4. Rare Find: "Rare Find!"

**Trigger**: Obtain specimen jar with LEGENDARY rarity specimen
**Icon**: Specimen Jar
**Frame**: Challenge (gold border)
**Reward**: **100 Experience**

**Files**: `data/entomology/advancement/*.json`

---

## 🔧 Technical Reference

### Specimen Data Model

**File**: `SpecimenDefinition.java`

**Fields**:

```java
String id;                    // Unique identifier
String entityType;           // e.g., "minecraft:bee"
String displayNameKey;       // Translation key
String descriptionKey;       // Translation key
Rarity rarity;              // COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
Size size;                  // TINY, SMALL, MEDIUM, LARGE
int experienceValue;        // Default: 10
boolean canBreed;           // Default: false
```

**JSON Example**:

```json
{
  "id": "bee",
  "entity_type": "minecraft:bee",
  "display_name_key": "specimen.entomology.bee",
  "description_key": "specimen.entomology.bee.desc",
  "rarity": "UNCOMMON",
  "size": "SMALL",
  "experience_value": 15,
  "can_breed": true
}
```

**Legacy Support**: All new fields are optional; omitted fields use defaults.

---

### Localization Keys

#### Advancements

```json
"advancement.entomology.root.title": "Entomology"
"advancement.entomology.root.description": "Begin your journey..."
"advancement.entomology.first_capture.title": "First Catch"
"advancement.entomology.first_capture.description": "Capture your first..."
"advancement.entomology.collector.title": "Collector"
"advancement.entomology.collector.description": "Collect 10 specimens"
"advancement.entomology.rare_find.title": "Rare Find!"
"advancement.entomology.rare_find.description": "Discover a legendary..."
```

#### Keybindings (UI only, not functional yet)

```json
"category.entomology": "Entomology"
"key.entomology.field_guide": "Open Field Guide"
"key.entomology.info_toggle": "Toggle Specimen Info"
"key.entomology.research_station": "Research Station"
```

---

### Client Registration

**File**: `EntomologyClient.java`

**Registered Systems**:

1. `SpecimenHudOverlay.register()` - HUD rendering
2. `SpecimenTooltipHandler.register()` - Tooltip enhancement
3. `HandledScreens.register(...)` - Research Station GUI
4. `ClientProxy.register()` - Screen opening handler
5. `BreedingParticleEffects.register()` - Breeding particles
6. `EnvironmentalParticleEffects.register()` - Environmental particles

---

## 📝 Adding New Specimens

### Step 1: Create JSON

**Path**: `src/main/resources/data/entomology/specimen/my_specimen.json`

```json
{
  "id": "butterfly",
  "entity_type": "minecraft:parrot",
  "display_name_key": "specimen.entomology.butterfly",
  "description_key": "specimen.entomology.butterfly.desc",
  "rarity": "RARE",
  "size": "TINY",
  "experience_value": 25,
  "can_breed": true
}
```

### Step 2: Add Localization

**Path**: `src/main/resources/assets/entomology/lang/en_us.json`

```json
{
  "specimen.entomology.butterfly": "Butterfly Specimen",
  "specimen.entomology.butterfly.desc": "A delicate butterfly with colorful wings.",
  "research.entomology.butterfly.page": "Butterflies are pollinators..."
}
```

### Step 3: Reload

- No recompilation needed!
- Use `/reload` command in-game (if implemented)
- Or restart game to load new specimen

---

## 🎨 Rarity Color Codes

**HUD/Tooltip Colors**:

- **COMMON**: `0xAAAAAA` (light gray) - default
- **UNCOMMON**: `0x55FF55` (green)
- **RARE**: `0x5555FF` (blue)
- **EPIC**: `0xAA00AA` (purple)
- **LEGENDARY**: `0xFFAA00` (gold)

**Implementation**: `SpecimenHudOverlay.getRarityColor()`

---

## 🐛 Debugging

### Enable Info Display

**Method**: Call `EntomologyKeybinds.isInfoEnabled()`
**Default**: Enabled

### Advanced Tooltip Mode

**Key**: F3 + H (Minecraft default)
**Shows**: Entity type ID in specimen tooltip

### Check Loaded Specimens

**Code**:

```java
ConfigManager.getSpecimenDefinitions()  // Map<String, SpecimenDefinition>
```

---

## 📊 Statistics

- **HUD Update**: Every frame when holding specimen jar
- **Tooltip Rendering**: On hover with 100ms debounce
- **Particle Count**:
  - Breeding Hearts: 7 particles
  - Breeding Success: 22 particles (7 hearts + 15 happy villagers)
  - Breeding Failure: 10 smoke particles
  - Mutation: 20 enchantment particles
  - Environmental: 2-3 particles per effect

---

## 🔗 Related Files

**Data Files**:

- Specimens: `data/entomology/specimen/*.json`
- Advancements: `data/entomology/advancement/*.json`
- Localization: `assets/entomology/lang/en_us.json`

**Java Classes**:

- HUD: `client/hud/SpecimenHudOverlay.java`
- Tooltips: `client/tooltip/SpecimenTooltipHandler.java`
- Particles: `client/particle/*.java`
- Screens: `client/screen/ResearchStationScreen.java`
- Data Model: `schema/SpecimenDefinition.java`

**Build**: `gradle.properties`, `build.gradle`
