# Entomology - Advanced Insect Collection Mod

**Version:** 1.0.0
**Minecraft:** 1.21.10
**Fabric Loader:** 0.18.1
**Fabric API:** 0.138.3+1.21.10

A comprehensive, data-driven Minecraft Fabric mod for insect collection, research, and breeding. Features custom entities, advanced rendering, modular architecture, and JSON-based configuration.

---

## 📋 Table of Contents

- [Features](#features)
- [Architecture Overview](#architecture-overview)
- [Complete File Documentation](#complete-file-documentation)
- [Installation](#installation)
- [Configuration](#configuration)
- [Development](#development)

---

## ✨ Features

### Gameplay

- **8 Custom Insects**: Beetle, Butterfly, Cicada, Damselfly, Firefly, Fly, Monarch Butterfly, Mosquito
- **5 Bug Net Tiers**: Basic, Iron, Golden, Diamond, Netherite (60%-100% catch rates)
- **Specimen Collection**: Capture and preserve insects in specimen jars
- **Research System**: Study specimens at the research station
- **Field Guide**: In-game encyclopedia with paginated insect information
- **Display Cases**: Showcase your collection
- **Breeding Mechanics**: Breed compatible insect species
- **Advancement System**: Track your entomologist progression

### Technical

- **Modular Architecture**: 11 packages with clear separation of concerns
- **Data-Driven**: All content defined in external JSON files
- **Schema Validation**: JSON schema validation for data integrity
- **Hot-Reloadable Config**: Change settings without restarting
- **Client/Server Separation**: Proper Fabric client/server architecture
- **Minecraft 1.21 Compatible**: Uses latest EntityRenderState system
- **Custom Render States**: Dedicated render states for each entity type
- **Particle Effects**: Breeding and environmental indicators
- **HUD Overlay**: Real-time specimen information display

---

## 🏗️ Architecture Overview

```
Entomology Mod Architecture
├── Core Layer (EntomologyMod.java)
│   ├── Initialization & Registration
│   ├── Shutdown Hooks
│   └── Creative Tab Management
│
├── Entity Layer (8 custom entities)
│   ├── AI Goals & Pathfinding
│   └── Attribute Registration
│
├── Client Layer (EntomologyClient.java)
│   ├── Entity Renderers & Models
│   ├── Custom Render States (NEW!)
│   ├── HUD & Tooltips
│   ├── Particle Systems
│   └── Screen Registration
│
├── Item Layer
│   ├── Bug Nets (5 tiers)
│   ├── Specimen Jars
│   └── Field Guide
│
├── Block Layer
│   ├── Research Station
│   ├── Display Case
│   └── Specimen Jar Block
│
├── Mechanics Layer
│   ├── Breeding System
│   ├── Environmental Preferences
│   └── Capture Logic
│
├── Configuration Layer
│   ├── Simple Config (JSON)
│   ├── Advanced Config (Schema-Validated)
│   └── Hot-Reload Support
│
├── Data Layer
│   ├── Specimen Definitions
│   ├── Research Entries
│   └── Recipe Data
│
├── API Layer
│   ├── Plugin System
│   ├── Mechanic Registration
│   └── Component System
│
└── Schema Layer
    ├── JSON Validation
    ├── Data Integrity
    └── Error Reporting
```

---

## 📁 Complete File Documentation

### Core Mod Files

#### **`EntomologyMod.java`**

**Location:** `src/main/java/dk/mosberg/entomology/`
**Purpose:** Main mod initializer and central registration hub

**Key Responsibilities:**

- Registers all 8 entity types with proper attributes
- Registers 5 bug net tiers (basic, iron, golden, diamond, netherite)
- Registers blocks (research station, display case, specimen jar)
- Creates block entities with proper synchronization
- Sets up creative tab with all mod items
- Initializes advanced systems (API, config, mechanics)
- Registers data reloaders for hot-reload support
- Manages shutdown hooks for clean resource cleanup

**Key Methods:**

```java
onInitialize()         // Main entry point
registerContent()      // Items & blocks registration
registerEntities()     // Entity types & attributes
registerCreativeTab()  // Custom creative tab
shutdown()            // Clean resource cleanup
```

**Dependencies:** Fabric API, all entity/item/block classes

---

### Entity System (8 Files)

#### **`BeetleEntity.java`**

**Location:** `src/main/java/dk/mosberg/entomology/entity/`
**Purpose:** Ground-dwelling beetle mob

**Attributes:**

- Health: 4.0 ❤❤
- Speed: 0.15 (slow)

**AI Goals (Priority Order):**

1. SwimGoal - Prevent drowning
2. EscapeDangerGoal (speed 1.2) - Flee from threats
3. WanderAroundFarGoal (speed 0.6) - Explore area
4. LookAroundGoal - Random looking

**Extends:** PathAwareEntity

---

#### **`CicadaEntity.java`**

**Purpose:** Ground-dwelling cicada mob (similar to beetle)

**Attributes:**

- Health: 3.0 ❤❤
- Speed: 0.12 (very slow)

**AI Goals:** Same as BeetleEntity

---

#### **`ButterflyEntity.java`**

**Purpose:** Flying butterfly mob

**Special Features:**

- Uses `FlightMoveControl` for smooth flying
- Uses `BirdNavigation` for flight pathfinding
- Can avoid traps (`canAvoidTraps() = true`)

**Attributes:**

- Health: 2.0 ❤
- Flying Speed: 0.3
- Movement Speed: 0.2

**AI Goals:**

1. SwimGoal
2. EscapeDangerGoal (speed 1.5) - Fast escape
3. WanderAroundFarGoal (speed 0.8) - Gentle wandering
4. LookAroundGoal

---

#### **`MonarchButterflyEntity.java`**

**Purpose:** Special monarch butterfly variant

**Features:** Identical to ButterflyEntity with unique texture (orange/black pattern)

---

#### **`DamselflyEntity.java`**

**Purpose:** Aquatic flying insect

**Features:**

- Flying AI with water preference
- Hovers near water sources
- Similar to butterfly but smaller

---

#### **`FireflyEntity.java`**

**Purpose:** Bioluminescent nocturnal insect

**Special Features:**

- Emits light (glow effect for atmosphere)
- Nocturnal behavior (more active at night)
- Spawns near water at night

---

#### **`FlyEntity.java`**

**Purpose:** Common flying pest

**Features:**

- Erratic flight patterns
- Fast movement speed
- Spawns anywhere

---

#### **`MosquitoEntity.java`**

**Purpose:** Small flying insect

**Features:**

- Nocturnal preference
- Attracted to players (within range)
- Very small hitbox

---

### Client Rendering System (20+ Files)

#### **`EntomologyClient.java`**

**Location:** `src/client/java/dk/mosberg/entomology/client/`
**Purpose:** Client-side mod initializer

**Key Responsibilities:**

- Registers entity model layers (INSECT, FLYING_INSECT)
- Registers entity renderers for all 8 entities
- Registers screen handlers (research station GUI)
- Initializes HUD overlay
- Registers tooltips
- Registers particle systems
- Registers keybindings
- Sets up Mod Menu integration

**Key Methods:**

```java
onInitializeClient()         // Main entry point
registerModelLayers()        // Entity model layers
registerEntityRenderers()    // Entity renderers
registerBlockEntityRenderers() // Block renderers (deferred)
```

---

#### **`InsectEntityModel.java`**

**Location:** `src/client/java/dk/mosberg/entomology/client/model/`
**Purpose:** 3D model for ground insects (beetle, cicada)

**Model Parts:**

- **Body:** 4x3x6 box (main body segment)
- **Head:** 3x2x2 box (front segment)
- **6 Legs:** 1x3x1 each (3 pairs)

**Texture Size:** 32x32 pixels

**Animation:**

- Walking leg movement using sine wave
- Legs move based on entity age
- Front, middle, and back legs alternate

**Usage:** Used by InsectEntityRenderer

---

#### **`FlyingInsectEntityModel.java`**

**Location:** `src/client/java/dk/mosberg/entomology/client/model/`
**Purpose:** 3D model for flying insects

**Model Parts:**

- **Body:** 2x2x4 thin box
- **Head:** 2x2x2 box
- **2 Wings:** 6x0x6 flat panels (paper-thin)
- **2 Legs:** 1x2x1 each

**Texture Size:** 32x32 pixels

**Animation:**

- Wing flapping using sine wave
- Wings rotate opposite directions
- Smooth flapping at 0.5Hz frequency

**Usage:** Used by FlyingInsectEntityRenderer

---

#### **`InsectEntityRenderer.java`**

**Location:** `src/client/java/dk/mosberg/entomology/client/render/entity/`
**Purpose:** Renderer for ground-dwelling insects

**Features:**

- Texture selection based on entity class
- Uses InsectEntityModel
- Creates custom render states
- Shadow radius: 0.3 blocks

**Texture Mapping:**

- `BeetleEntity` → `textures/entity/beetle.png`
- `CicadaEntity` → `textures/entity/cicada.png`
- Others → `textures/entity/insect.png` (fallback)

**Render States:**

- Returns `BeetleEntityRenderState` for beetles
- Returns `CicadaEntityRenderState` for cicadas
- Returns `LivingEntityRenderState` for others

---

#### **`FlyingInsectEntityRenderer.java`**

**Location:** `src/client/java/dk/mosberg/entomology/client/render/entity/`
**Purpose:** Renderer for flying insects

**Features:**

- Configurable texture via constructor
- Uses FlyingInsectEntityModel
- Creates entity-specific render states
- Shadow radius: 0.3 blocks

**Texture Mapping (by texture name):**

- `butterfly` → ButterflyEntityRenderState
- `monarch_butterfly` → MonarchButterflyEntityRenderState
- `damselfly` → DamselflyEntityRenderState
- `firefly` → FireflyEntityRenderState
- `fly` → FlyEntityRenderState
- `mosquito` → MosquitoEntityRenderState

---

#### **`EntomologyModelLayers.java`**

**Location:** `src/client/java/dk/mosberg/entomology/client/render/entity/`
**Purpose:** Model layer registry identifiers

**Constants:**

```java
INSECT         // Ground insect model layer
FLYING_INSECT  // Flying insect model layer
```

**Usage:** Referenced when registering models and renderers

---

### Custom Render States (8 Files - NEW!)

**Location:** `src/client/java/dk/mosberg/entomology/client/render/entity/state/`

All render state classes extend `LivingEntityRenderState` and follow Minecraft 1.21+ requirements for separating rendering data from entity data.

#### **`BeetleEntityRenderState.java`**

**Purpose:** Stores rendering-specific data for beetles
**Future Extensions:** Shell color, size variations, damage state

#### **`CicadaEntityRenderState.java`**

**Purpose:** Stores rendering-specific data for cicadas
**Future Extensions:** Wing transparency, sound visualization, seasonal variations

#### **`ButterflyEntityRenderState.java`**

**Purpose:** Stores rendering-specific data for butterflies
**Future Extensions:** Wing flap phase, color pattern, flight altitude

#### **`MonarchButterflyEntityRenderState.java`**

**Purpose:** Stores rendering-specific data for monarch butterflies
**Future Extensions:** Migration state, wing wear pattern, breeding readiness

#### **`DamselflyEntityRenderState.java`**

**Purpose:** Stores rendering-specific data for damselflies
**Future Extensions:** Iridescence effect, body color, hover stability

#### **`FireflyEntityRenderState.java`**

**Purpose:** Stores rendering-specific data for fireflies
**Current Fields:**

- `float glowIntensity` - Bioluminescence brightness (0.0-1.0)
- `boolean isFlashing` - Flash state for blinking effect

**Future Extensions:** Flash pattern timing, glow color

#### **`FlyEntityRenderState.java`**

**Purpose:** Stores rendering-specific data for flies
**Future Extensions:** Wing buzz rate, erratic flight pattern, landing state

#### **`MosquitoEntityRenderState.java`**

**Purpose:** Stores rendering-specific data for mosquitoes
**Future Extensions:** Proboscis extension, feeding state, blood level

---

### HUD & UI Components

#### **`SpecimenHudOverlay.java`**

**Location:** `src/client/java/dk/mosberg/entomology/client/hud/`
**Purpose:** Real-time HUD display

**Displays:**

- Specimen name
- Rarity tier with color coding
  - COMMON (gray)
  - UNCOMMON (green)
  - RARE (blue)
  - EPIC (purple)
  - LEGENDARY (gold)
- Condition percentage (health bar)

**Trigger:** Appears when holding specimen jar item

---

#### **`SpecimenTooltipHandler.java`**

**Location:** `src/client/java/dk/mosberg/entomology/client/tooltip/`
**Purpose:** Enhanced tooltips for items

**Shows on specimen jars:**

- Specimen species name
- Rarity tier (colored)
- Size (TINY/SMALL/MEDIUM/LARGE/HUGE)
- Breeding status (ready/cooldown)
- Condition percentage
- Capture timestamp
- Debug info (if enabled)

---

#### **`BreedingParticleEffects.java`**

**Location:** `src/client/java/dk/mosberg/entomology/client/particle/`
**Purpose:** Visual effects for breeding

**Particle Types:**

- ❤ Heart particles (successful breeding)
- 💔 Smoke particles (breeding failure)
- ✨ Sparkles (offspring spawned)

---

#### **`EnvironmentalParticleEffects.java`**

**Location:** `src/client/java/dk/mosberg/entomology/client/particle/`
**Purpose:** Environmental preference indicators

**Particle Types:**

- 🌿 Green particles (preferred biome/environment)
- 🔥 Red particles (unsuitable environment)
- 💧 Blue particles (water source nearby for aquatic species)

---

#### **`EntomologyKeybinds.java`**

**Location:** `src/client/java/dk/mosberg/entomology/client/keybind/`
**Purpose:** Custom keyboard shortcuts

**Keybindings:**

- `G` - Open Field Guide
- `H` - Toggle Specimen Info HUD
- `R` - Quick Research (when near research station)

---

#### **`ResearchStationScreen.java`**

**Location:** `src/client/java/dk/mosberg/entomology/client/screen/`
**Purpose:** GUI for research station block

**Features:**

- Specimen input slot (top)
- Player inventory (bottom)
- Research progress bar
- Unlock notification

---

#### **`EntomologyConfigScreen.java`**

**Location:** `src/client/java/dk/mosberg/entomology/client/config/`
**Purpose:** In-game configuration screen

**Configuration Options (all toggleable):**

- Enable Particles
- Enable Sounds
- Enable HUD
- Enable Breeding Mechanics
- Enable Research Progression
- Debug Mode

**Access:** Via Mod Menu → Entomology → Config

**Behavior:** Auto-saves on close

---

#### **`ModMenuIntegration.java`**

**Location:** `src/client/java/dk/mosberg/entomology/client/modmenu/`
**Purpose:** Mod Menu integration

**Implements:** `ModMenuApi`

**Provides:**

- Config screen factory
- Mod info display
- Update checker integration

---

#### **`ClientProxy.java`**

**Location:** `src/client/java/dk/mosberg/entomology/client/`
**Purpose:** Client-side proxy for operations

**Handles:**

- Field guide screen opening
- Client-only rendering calls
- Screen management

---

### Item System

#### **`BugNetItem.java`**

**Location:** `src/main/java/dk/mosberg/entomology/item/`
**Purpose:** Tool for capturing insects

**Tiers & Stats:**
| Tier | Durability | Catch Rate | Special |
|------|-----------|------------|---------|
| Basic | 64 uses | 60% | - |
| Iron | 256 uses | 70% | - |
| Golden | 192 uses | 85% | Fast |
| Diamond | 512 uses | 90% | - |
| Netherite | 1024 uses | 100% | Fireproof |

**Usage:**

1. Right-click near insect entity
2. Rolls catch chance based on tier
3. Creates specimen jar item if successful
4. Consumes durability on attempt

**NBT Data Stored:**

- Specimen type (entity ID)
- Capture timestamp
- Initial condition (100%)
- Rarity tier (calculated)
- Size (random within range)

---

#### **`SpecimenJarItem.java`**

**Location:** `src/main/java/dk/mosberg/entomology/item/`
**Purpose:** Container for captured specimens

**Extends:** BlockItem (can be placed as block)

**Features:**

- Right-click → View specimen info
- Sneak + right-click → Release specimen (spawns entity)
- Place on ground → Decorative block
- Condition degrades over time
- Cannot release if condition < 10%

**NBT Structure:**

```java
{
  "specimen": {
    "type": "entomology:butterfly",
    "rarity": "RARE",
    "captureTime": 1234567890,
    "condition": 85.5,
    "size": "MEDIUM",
    "canBreed": true,
    "breedingCooldown": 0
  }
}
```

**Condition Degradation:**

- Loses 0.1% per minute
- Stops degrading in research station
- Can be restored via special mechanics

---

#### **`FieldGuideItem.java`**

**Location:** `src/main/java/dk/mosberg/entomology/item/`
**Purpose:** In-game encyclopedia

**Features:**

- Right-click → Opens paginated GUI
- Shows only discovered species
- Each entry includes:
  - Species name & Latin name
  - Detailed description
  - Habitat information
  - Behavior notes
  - Breeding information
  - Rarity tier

**Unlocking:**

- Entries unlock when specimen is captured
- OR when specimen is researched at station
- Persistent per player

---

### Block System

#### **`ResearchStationBlock.java`**

**Location:** `src/main/java/dk/mosberg/entomology/block/`
**Purpose:** Block for studying specimens

**Features:**

- Right-click → Opens GUI
- Has block entity for inventory
- Research takes time (progress bar)
- Unlocks field guide entries
- Prevents specimen degradation

**Properties:**

- Material: Wood
- Hardness: 2.5
- Requires tool: Axe

---

#### **`DisplayCaseBlock.java`**

**Location:** `src/main/java/dk/mosberg/entomology/block/`
**Purpose:** Decorative showcase block

**Features:**

- Stores up to 9 specimen jars
- Visible through glass panels
- Right-click → Opens inventory GUI
- Specimens visible as items inside

**Properties:**

- Material: Glass + Wood
- Hardness: 1.5
- Transparent

---

#### **`SpecimenJarBlock.java`**

**Location:** `src/main/java/dk/mosberg/entomology/block/`
**Purpose:** Placed specimen jar (decorative)

**Features:**

- Retains all specimen NBT data
- Can be picked up (drops as item)
- Transparent glass material
- Shows specimen inside (via block entity renderer - planned)

**Properties:**

- Material: Glass
- Hardness: 0.3
- Transparent

---

### Block Entities

#### **`ResearchStationBlockEntity.java`**

**Location:** `src/main/java/dk/mosberg/entomology/block/entity/`
**Purpose:** Block entity for research station

**Features:**

- 1 slot inventory (specimen input)
- Research progress tracking (0-100%)
- Network synchronization
- Prevents specimen degradation

**Methods:**

```java
tick()           // Progress research
startResearch()  // Begin research process
completeResearch() // Unlock field guide entry
```

---

#### **`DisplayCaseBlockEntity.java`**

**Location:** `src/main/java/dk/mosberg/entomology/block/entity/`
**Purpose:** Block entity for display case

**Features:**

- 9 slot inventory (3x3 grid)
- Network synchronization
- Renders items inside case

---

#### **`SpecimenJarBlockEntity.java`**

**Location:** `src/main/java/dk/mosberg/entomology/block/entity/`
**Purpose:** Block entity for placed jars

**Features:**

- Stores specimen NBT data
- Condition tracking
- Network synchronization

---

### Configuration System

#### **`EntomologyConfig.java`**

**Location:** `src/main/java/dk/mosberg/entomology/config/`
**Purpose:** Simple JSON configuration

**File Location:** `config/entomology.json`

**Configuration Fields:**

```java
enableParticles         // Show particles (default: true)
enableSounds           // Play sounds (default: true)
enableHud              // Show HUD overlay (default: true)
enableBreedingMechanics // Enable breeding (default: true)
enableResearchProgression // Research unlocking (default: true)
netCaptureChance       // Catch rate multiplier (default: 1.0)
specimenJarMaxAge      // Max ticks before death (default: 24000)
debugMode              // Enable debug logging (default: false)
```

**Features:**

- Auto-loads on mod initialization
- Auto-saves when changed via GUI
- Pretty-printed JSON format
- Uses Gson for serialization

**Methods:**

```java
load()   // Load from disk or create defaults
save()   // Write to disk
```

---

#### **`ConfigManager.java`**

**Location:** `src/main/java/dk/mosberg/entomology/config/`
**Purpose:** Legacy configuration manager

**Features:**

- Singleton pattern
- Configuration validation
- Hot-reload support
- Event-based reload notifications

---

#### **`SchemaConfigManager.java`**

**Location:** `src/main/java/dk/mosberg/entomology/config/advanced/`
**Purpose:** Schema-validated configuration

**Features:**

- Loads JSON schemas from `data/entomology/schema/`
- Validates configurations before loading
- Provides detailed error messages with line numbers
- Supports multiple config types

**Supported Configs:**

- `mechanics.json` - Mechanic configurations
- `breeding_config.json` - Breeding rules

**Methods:**

```java
loadConfig(String name)              // Load & validate
saveConfig(String name, JsonObject)  // Validate & save
loadSchema(String name)              // Load JSON schema
validateConfig(JsonObject, JsonObject) // Validate against schema
```

**Error Reporting:**

- Shows exact line number of error
- Describes what's wrong
- Suggests fix if possible

---

### Mechanics System

#### **`IMechanic.java`**

**Location:** `src/main/java/dk/mosberg/entomology/mechanics/`
**Purpose:** Base interface for all mechanics

**Methods:**

```java
String getId()                 // Unique identifier
MechanicResult apply(context)  // Execute mechanic
boolean isEnabled()            // Check if active
```

---

#### **`MechanicContext.java`**

**Location:** `src/main/java/dk/mosberg/entomology/mechanics/`
**Purpose:** Context object for mechanic execution

**Contains:**

- `World` - World reference
- `PlayerEntity` - Player (optional)
- `Entity` - Target entity (optional)
- `BlockPos` - Position (optional)
- `Map<String, Object>` - Additional data

---

#### **`MechanicResult.java`**

**Location:** `src/main/java/dk/mosberg/entomology/mechanics/`
**Purpose:** Result from mechanic execution

**Contains:**

- `boolean success` - Whether mechanic succeeded
- `Map<String, Object> data` - Result data
- `List<String> errors` - Error messages

---

#### **`BreedingMechanic.java`**

**Location:** `src/main/java/dk/mosberg/entomology/mechanics/`
**Purpose:** Legacy breeding system

**Features:**

- Parent compatibility checking
- Offspring generation
- Success rate calculation
- Cooldown management

**Logic:**

1. Check if parents are compatible
2. Check environmental conditions
3. Roll success chance
4. Generate offspring with inherited traits
5. Apply breeding cooldown

---

#### **`EnvironmentalMechanic.java`**

**Location:** `src/main/java/dk/mosberg/entomology/mechanics/`
**Purpose:** Legacy environmental preferences

**Checks:**

- Biome compatibility
- Temperature requirements
- Humidity requirements
- Time of day preferences
- Weather conditions

---

#### **`AdvancedBreedingMechanic.java`**

**Location:** `src/main/java/dk/mosberg/entomology/mechanics/impl/`
**Purpose:** Schema-driven breeding system

**Configuration Format (JSON):**

```json
{
  "breedingPairs": [
    {
      "parent1": "entomology:butterfly",
      "parent2": "entomology:butterfly",
      "offspring": [
        {
          "type": "entomology:butterfly",
          "weight": 80,
          "traits": ["color_variant"]
        },
        {
          "type": "entomology:monarch_butterfly",
          "weight": 20,
          "traits": ["rare_pattern"]
        }
      ],
      "requirements": {
        "biome": "minecraft:plains",
        "temperature": [0.5, 1.0],
        "time": "day"
      },
      "cooldown": 6000
    }
  ]
}
```

**Features:**

- Weighted offspring selection
- Trait inheritance system
- Environmental requirements
- Mutation chances
- Configurable cooldowns

---

#### **`AdvancedEnvironmentalMechanic.java`**

**Location:** `src/main/java/dk/mosberg/entomology/mechanics/impl/`
**Purpose:** Schema-driven environmental system

**Configuration Format (JSON):**

```json
{
  "preferences": {
    "entomology:firefly": {
      "biomes": ["minecraft:plains", "minecraft:forest"],
      "temperature": [0.5, 0.9],
      "humidity": [0.6, 1.0],
      "timeOfDay": "night",
      "weather": ["clear", "rain"],
      "nearWater": true
    }
  }
}
```

**Features:**

- Per-species preferences
- Temperature & humidity ranges
- Time and weather preferences
- Water proximity requirements
- Biome whitelist/blacklist

---

#### **`AbstractMechanic.java`**

**Location:** `src/main/java/dk/mosberg/entomology/mechanics/base/`
**Purpose:** Base implementation for mechanics

**Implements:** IMechanic

**Provides:**

- Enable/disable functionality
- Logging integration
- Common helper methods
- Error handling

---

### Data & Schema System

#### **`DataDrivenRegistry.java`**

**Location:** `src/main/java/dk/mosberg/entomology/data/`
**Purpose:** Central registry for data-driven content

**Features:**

- Resource reload listener
- Parses JSON files from datapacks
- Hot-reload support
- Error reporting

**Inner Classes:**

##### `SpecimenReloader`

**Purpose:** Reloads specimen definitions
**Location:** `data/entomology/specimen/*.json`

**Example Specimen JSON:**

```json
{
  "type": "entomology:butterfly",
  "rarity": "UNCOMMON",
  "size": "SMALL",
  "canBreed": true,
  "preferredBiomes": ["minecraft:plains", "minecraft:forest"],
  "description": "A beautiful butterfly..."
}
```

##### `DefinitionReloader`

**Purpose:** Reloads item/block definitions
**Location:** `data/entomology/items/*.json`, `data/entomology/blocks/*.json`

##### `MechanicsReloader`

**Purpose:** Reloads mechanic configurations
**Location:** `data/entomology/mechanics/*.json`

**Methods:**

```java
bootstrap()  // Initialize registries
reload()     // Hot-reload all data
```

---

#### **`ISchemaValidator.java`**

**Location:** `src/main/java/dk/mosberg/entomology/schema/`
**Purpose:** Interface for schema validators

**Methods:**

```java
boolean validate(JsonObject json)  // Validate JSON
List<String> getErrors()           // Get error list
```

---

#### **`BaseSchemaValidator.java`**

**Location:** `src/main/java/dk/mosberg/entomology/schema/`
**Purpose:** Base schema validator implementation

**Features:**

- JSON schema loading
- Error collection
- Type checking
- Required field validation

---

#### **`SpecimenSchemaValidator.java`**

**Location:** `src/main/java/dk/mosberg/entomology/schema/`
**Purpose:** Validates specimen JSON files

**Schema:** `data/entomology/schema/specimen.schema.json`

**Validates:**

- Specimen type (must be valid entity ID)
- Rarity tier (COMMON/UNCOMMON/RARE/EPIC/LEGENDARY)
- Size (TINY/SMALL/MEDIUM/LARGE/HUGE)
- Breeding compatibility (boolean)
- Preferred biomes (array of biome IDs)
- Temperature/humidity ranges
- Description (string)

**Example Error:**

```
Error at line 5: Invalid rarity tier "SUPER_RARE"
  Expected one of: COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
```

---

### API & Integration

#### **`EntomologyAPI.java`**

**Location:** `src/main/java/dk/mosberg/entomology/api/`
**Purpose:** Public API for other mods

**Features:**

- Singleton pattern (`getInstance()`)
- Version information (`getApiVersion()`)
- Mechanic registration
- Component registration

**Usage Example:**

```java
EntomologyAPI api = EntomologyAPI.getInstance();
api.registerMechanic(new CustomMechanic());
List<IMechanic> mechanics = api.getMechanics();
```

**Version:** 1.0.0

---

#### **`IEntomologyPlugin.java`**

**Location:** `src/main/java/dk/mosberg/entomology/api/`
**Purpose:** Interface for plugin mods

**Methods:**

```java
void onInitialize(EntomologyAPI api)  // Called on init
String getPluginId()                  // Unique ID
```

**Usage:** Implement this interface in your plugin mod to extend Entomology

---

#### **`SystemIntegration.java`**

**Location:** `src/main/java/dk/mosberg/entomology/integration/`
**Purpose:** Integrates all advanced systems

**Features:**

- Singleton pattern
- System lifecycle management
- Configuration loading
- Component initialization
- Telemetry system (optional)

**Methods:**

```java
initialize()         // Start all systems
shutdown()           // Clean shutdown
loadConfigurations() // Load all configs
reload()             // Hot-reload systems
```

**Systems Managed:**

- Config system
- Mechanic system
- Component system
- Data registry
- Schema validation

---

### Screen & UI

#### **`ResearchStationScreenHandler.java`**

**Location:** `src/main/java/dk/mosberg/entomology/screen/`
**Purpose:** Screen handler for research station

**Extends:** ScreenHandler

**Features:**

- Single input slot for specimens
- Player inventory integration (27 slots + 9 hotbar)
- Shift-click support
- Network synchronization

**Slot IDs:**

- 0: Specimen input
- 1-27: Player inventory
- 28-36: Hotbar

**Methods:**

```java
canUse(PlayerEntity)              // Check access
transferSlot(PlayerEntity, int)   // Shift-click
onSlotClick(...)                  // Handle clicks
```

---

### Command System

#### **`EntomologyCommands.java`**

**Location:** `src/main/java/dk/mosberg/entomology/command/`
**Purpose:** Basic mod commands

**Commands:**

##### `/entomology reload`

**Permission:** OP level 2
**Purpose:** Hot-reload all configurations and data

##### `/entomology stats`

**Permission:** All players
**Purpose:** Show personal statistics (captures, research, breeding)

##### `/entomology validate`

**Permission:** OP level 2
**Purpose:** Validate all JSON files against schemas

---

#### **`AdvancedCommands.java`**

**Location:** `src/main/java/dk/mosberg/entomology/command/`
**Purpose:** Advanced debugging commands

**Commands:**

##### `/entomology debug`

**Permission:** OP level 2
**Purpose:** Toggle debug mode (shows extra logging)

##### `/entomology specimen <type> [rarity]`

**Permission:** OP level 2
**Purpose:** Give specimen jar with specified type and rarity
**Example:** `/entomology specimen butterfly RARE`

##### `/entomology research <player>`

**Permission:** OP level 2
**Purpose:** View player's research progress
**Shows:** Unlocked entries, capture count, breeding count

---

### Component System

#### **`ISpecimenComponent.java`**

**Location:** `src/main/java/dk/mosberg/entomology/component/`
**Purpose:** Interface for specimen components

**Methods:**

```java
onCapture(context)   // Called when captured
onRelease(context)   // Called when released
tick(context)        // Called each tick
serialize()          // Save to NBT
deserialize(nbt)     // Load from NBT
```

**Purpose:** Modular behavior system for specimens

**Planned Components:**

- `HealthComponent` - Condition management
- `AgingComponent` - Aging mechanics
- `BreedingComponent` - Breeding state
- `EnvironmentComponent` - Environment tracking

---

#### **`ComponentRegistry.java`**

**Location:** `src/main/java/dk/mosberg/entomology/registry/advanced/`
**Purpose:** Registry for components

**Features:**

- Component registration by ID
- Component factory pattern
- Component lookup

**Methods:**

```java
register(String id, ComponentFactory)  // Register component
create(String id)                      // Instantiate component
has(String id)                         // Check if exists
```

---

### Registry System

#### **`ModRegistry.java`**

**Location:** `src/main/java/dk/mosberg/entomology/registry/`
**Purpose:** Central registration system

**Features:**

- Registers mechanics with API
- Registers components
- Registers validators
- Lifecycle management

**Methods:**

```java
initialize()  // Called during mod init
```

**Registers:**

- BreedingMechanic
- EnvironmentalMechanic
- AdvancedBreedingMechanic
- AdvancedEnvironmentalMechanic
- SpecimenSchemaValidator

---

### Testing System

#### **`CoreSystemTest.java`**

**Location:** `src/test/java/dk/mosberg/entomology/`
**Purpose:** Tests core initialization

**Tests:**

- Mod initializes without errors
- Config manager loads
- Registry initializes
- Systems start correctly

---

#### **`AdvancedBreedingMechanicTest.java`**

**Location:** `src/test/java/dk/mosberg/entomology/test/`
**Purpose:** Tests breeding mechanics

**Tests (8 total):**

- Parent compatibility checking
- Offspring generation
- Trait inheritance
- Environmental requirement checking
- Success rate calculation
- Cooldown management
- Mutation system
- Error handling

---

#### **`ComponentRegistryTest.java`**

**Location:** `src/test/java/dk/mosberg/entomology/test/`
**Purpose:** Tests component system

**Tests (6 total):**

- Component registration
- Component lookup
- Component instantiation
- Duplicate prevention
- Invalid ID handling
- Factory pattern

---

#### **`SchemaConfigManagerTest.java`**

**Location:** `src/test/java/dk/mosberg/entomology/test/`
**Purpose:** Tests schema validation

**Tests (3 total):**

- Schema loading
- Valid JSON passes
- Invalid JSON rejected with proper errors

---

#### **`IntegrationTest.java`**

**Location:** `src/test/java/dk/mosberg/entomology/integration/`
**Purpose:** Integration testing

**Tests (7 total):**

- Full capture workflow
- Research station interaction
- Breeding workflow
- Data reloading
- Command execution
- API usage
- System integration

---

### Resource Files

#### **`fabric.mod.json`**

**Location:** `src/main/resources/`
**Purpose:** Mod metadata

**Key Fields:**

```json
{
  "schemaVersion": 1,
  "id": "entomology",
  "version": "1.0.0",
  "name": "Entomology",
  "description": "Insect collection and research mod",
  "authors": ["Mosberg"],
  "contact": {
    "homepage": "https://github.com/Mosberg/entomology",
    "sources": "https://github.com/Mosberg/entomology"
  },
  "license": "MIT",
  "environment": "*",
  "entrypoints": {
    "main": ["dk.mosberg.entomology.EntomologyMod"],
    "client": ["dk.mosberg.entomology.client.EntomologyClient"],
    "modmenu": ["dk.mosberg.entomology.client.modmenu.ModMenuIntegration"]
  },
  "depends": {
    "fabricloader": ">=0.18.1",
    "minecraft": "1.21.10",
    "java": ">=21",
    "fabric-api": "*"
  }
}
```

---

#### **Language Files**

##### **`en_us.json`**

**Location:** `src/main/resources/assets/entomology/lang/`
**Lines:** 280+

**Categories:**

- Item names (20+)
- Block names (3)
- Entity names (8)
- Specimen names (14)
- Research entries (14)
- Tooltips (20+)
- GUI text (10+)
- Achievement text (20+)
- Config options (3 NEW!)

**New Translations:**

```json
{
  "config.entomology.enable_particles": "Enable Particles",
  "config.entomology.enable_sounds": "Enable Sounds",
  "config.entomology.enable_hud": "Enable HUD"
}
```

##### **`da_dk.json`**

**Purpose:** Danish translations

##### **`de_de.json`**

**Purpose:** German translations

---

#### **Textures**

**Location:** `src/main/resources/assets/entomology/textures/entity/`

**Required Entity Textures (32x32 PNG):**

- ✅ `beetle.png` - Brown/black beetle
- ✅ `cicada.png` - Green/brown cicada
- ✅ `butterfly.png` - Colorful butterfly
- ✅ `monarch_butterfly.png` - Orange/black monarch
- ✅ `damselfly.png` - Blue/green damselfly
- ✅ `firefly.png` - Yellow/green with glow
- ✅ `fly.png` - Dark gray fly
- ✅ `mosquito.png` - Gray mosquito
- ✅ `insect.png` - Generic fallback

**Note:** Placeholder textures can be used initially

---

#### **Models**

**Location:** `src/main/resources/assets/entomology/models/`

**Item Models:** `item/*.json`
**Block Models:** `block/*.json`

---

#### **Data Files**

**Location:** `src/main/resources/data/entomology/`

##### **`advancement/`** - 13 achievements

- `root.json` - Root achievement
- `bug_beginnings.json` - Get first net
- `first_capture.json` - Capture first insect
- `first_find.json` - Discover first species
- `collector.json` - Capture 10 insects
- `curator.json` - Place display case
- `rare_find.json` - Find rare specimen
- `legend_lore.json` - Capture phantom
- `legendary_catch.json` - Capture endermite
- `all_nets.json` - Get all net tiers
- `all_species.json` - Capture all species
- `breed_insects.json` - Breed successfully
- `master_entomologist.json` - Complete encyclopedia

##### **`recipe/`** - 9 crafting recipes

- Bug nets (5 recipes)
- Display case
- Research station
- Specimen jar
- Field guide

##### **`specimen/`** - 14 specimen definitions

- Custom Entomology insects (8)
- Vanilla insects (6)

##### **`research/`** - 14 research entries

- One per specimen type

##### **`mechanics/`** - Mechanic configs

- `breeding_config.json` - Breeding rules

##### **`schema/`** - JSON schemas

- `specimen.schema.json`
- `breeding_config.schema.json`
- `advancement.schema.json`
- `block.schema.json`
- `bug_net.schema.json`
- `damage_type.schema.json`
- `entity.schema.json`
- And more...

---

## 🚀 Installation

### For Players

1. Install [Fabric Loader 0.18.1+](https://fabricmc.net/use/)
2. Install [Fabric API 0.138.3+](https://modrinth.com/mod/fabric-api)
3. Download Entomology mod jar
4. Place in `.minecraft/mods/` folder
5. Launch Minecraft 1.21.10

### For Developers

```bash
git clone https://github.com/Mosberg/entomology.git
cd entomology
./gradlew build
```

---

## ⚙️ Configuration

### Simple Config

**Location:** `config/entomology.json`

```json
{
  "enableParticles": true,
  "enableSounds": true,
  "enableHud": true,
  "enableBreedingMechanics": true,
  "enableResearchProgression": true,
  "netCaptureChance": 1.0,
  "specimenJarMaxAge": 24000,
  "debugMode": false
}
```

### In-Game Config

1. Install [Mod Menu](https://modrinth.com/mod/modmenu)
2. Open Mod Menu in-game
3. Find "Entomology"
4. Click "Config"
5. Toggle options as desired
6. Changes save automatically

---

## 🔧 Development

### Project Structure

```
entomology/
├── src/main/java/           (Server-side code)
├── src/client/java/         (Client-side code)
├── src/test/java/           (Unit tests)
├── src/main/resources/      (Assets & data)
├── gradle/                  (Gradle wrapper)
├── build.gradle             (Build config)
├── gradle.properties        (Versions)
└── README.md                (This file)
```

### Building

```bash
./gradlew build
```

Output: `build/libs/entomology-1.0.0.jar`

### Running Tests

```bash
./gradlew test
```

All 24 tests should pass

### Running in Development

```bash
./gradlew runClient  # Client
./gradlew runServer  # Server
```

### Code Quality

- Checkstyle enforced
- JavaDoc required for public APIs
- Line length: 120 characters
- Zero warnings/errors required

---

## 📊 Project Statistics

- **Total Java Files:** 80+
- **Lines of Code:** ~15,000
- **Client Files:** 20+
- **Server Files:** 60+
- **Test Files:** 5
- **Test Coverage:** 24 unit tests (all passing)
- **JSON Data Files:** 50+
- **Translations:** 3 languages (English, Danish, German)
- **Custom Entities:** 8 (all with AI)
- **Custom Render States:** 8 (NEW!)
- **Entity Models:** 2 (ground, flying)
- **Entity Renderers:** 2 (ground, flying)
- **Items:** 8 (5 nets, jar, guide, research station)
- **Blocks:** 3 (research station, display case, specimen jar)
- **Block Entities:** 3
- **Mechanics:** 4 (2 legacy, 2 advanced)
- **Advancements:** 13
- **Recipes:** 9
- **Commands:** 6

---

## ✅ Current Status

### Completed ✓

- ✅ All 8 entities registered with attributes
- ✅ Entity models with animations
- ✅ Entity renderers with custom render states (NEW!)
- ✅ All items registered and functional
- ✅ All blocks registered and functional
- ✅ Block entities with inventories
- ✅ Research station GUI
- ✅ Config screen (Mod Menu integration)
- ✅ HUD overlay
- ✅ Tooltips
- ✅ Particle effects
- ✅ Keybindings
- ✅ Commands
- ✅ Data-driven system
- ✅ Schema validation
- ✅ API system
- ✅ Component system
- ✅ Test suite (24 tests passing)
- ✅ 3 language files
- ✅ Build successful (0 errors)

### In Progress ⏳

- ⏳ Entity textures (need 32x32 PNG files)
- ⏳ Block entity renderers (deferred - needs BlockEntityRenderState)
- ⏳ Spawn eggs (deferred - needs DataComponentTypes testing)

### Planned 📋

- 📋 Biome-specific spawning
- 📋 Season system
- 📋 Advanced mutations
- 📋 Cross-mod compatibility

---

## 🤝 Contributing

1. Fork repository
2. Create feature branch
3. Commit changes
4. Push to branch
5. Open Pull Request

**Requirements:**

- Code must pass Checkstyle
- All tests must pass
- JavaDoc for public APIs
- Follow existing code style

---

## 📝 License

See [LICENSE](LICENSE) file

---

## 🙏 Credits

- **Author:** Mosberg
- **Framework:** Fabric
- **Minecraft Version:** 1.21.10
- **Rendering System:** Minecraft 1.21 EntityRenderState

---

## 📞 Support

- **Issues:** [GitHub Issues](https://github.com/Mosberg/entomology/issues)
- **Wiki:** [GitHub Wiki](https://github.com/Mosberg/entomology/wiki)
- **Discussions:** [GitHub Discussions](https://github.com/Mosberg/entomology/discussions)

---

**Last Updated:** December 11, 2025
**Mod Version:** 1.0.0
**Documentation Version:** 2.0 (Complete File Reference)
