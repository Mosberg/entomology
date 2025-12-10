# Missing Textures Guide - Entomology Mod

This document lists all texture files that need to be created for the mod to have custom visuals.

---

## 📁 Project Structure

```
src/main/resources/assets/entomology/textures/
├── gui/
│   └── research_station.png          [MISSING - REQUIRED]
├── item/
│   ├── basic_bug_net.png             [MISSING - Currently using fishing_rod]
│   ├── bug_net.png                   [MISSING - Currently using fishing_rod]
│   ├── iron_bug_net.png              [MISSING - Currently using iron_hoe]
│   ├── golden_bug_net.png            [MISSING - Currently using golden_hoe]
│   ├── diamond_bug_net.png           [MISSING - Currently using diamond_hoe]
│   ├── netherite_bug_net.png         [MISSING - Currently using netherite_hoe]
│   ├── field_guide.png               [MISSING - Currently using book]
│   ├── specimen_jar.png              [MISSING - Currently using glass_bottle]
│   └── spawn_eggs/                   [ALL USING VANILLA TEMPLATE]
│       ├── fly_spawn_egg.png         [Optional - Uses template_spawn_egg]
│       ├── butterfly_spawn_egg.png   [Optional - Uses template_spawn_egg]
│       ├── mosquito_spawn_egg.png    [Optional - Uses template_spawn_egg]
│       ├── cicada_spawn_egg.png      [Optional - Uses template_spawn_egg]
│       ├── damselfly_spawn_egg.png   [Optional - Uses template_spawn_egg]
│       ├── beetle_spawn_egg.png      [Optional - Uses template_spawn_egg]
│       ├── firefly_spawn_egg.png     [Optional - Uses template_spawn_egg]
│       └── monarch_butterfly_spawn_egg.png [Optional - Uses template_spawn_egg]
├── block/
│   ├── (specimen_jar)                [Using vanilla: glass + stripped_oak_log]
│   ├── (research_station)            [Using vanilla: crafting_table textures]
│   └── (display_case)                [Using vanilla: glass]
└── entity/
    ├── fly.png                       [MISSING - Custom entity texture]
    ├── butterfly.png                 [MISSING - Custom entity texture]
    ├── mosquito.png                  [MISSING - Custom entity texture]
    ├── cicada.png                    [MISSING - Custom entity texture]
    ├── damselfly.png                 [MISSING - Custom entity texture]
    ├── beetle.png                    [MISSING - Custom entity texture]
    ├── firefly.png                   [MISSING - Custom entity texture]
    └── monarch_butterfly.png         [MISSING - Custom entity texture]
```

---

## 🎨 Texture Specifications

### **GUI Textures**

#### `gui/research_station.png`

- **Resolution**: `256x256` pixels
- **Format**: PNG with transparency
- **Purpose**: GUI background for Research Station interface
- **Required Elements**:
  - Background container area (176x166 pixels visible area)
  - 2 input slots (specimen jar + field guide)
  - Player inventory slots (9x4 grid at bottom)
  - Progress indicator or research status area
  - Title area at top
- **Style**: Match Minecraft vanilla GUI aesthetics (gray background, slot outlines)
- **Reference**: Look at `minecraft:textures/gui/container/crafting_table.png`

---

### **Item Textures**

All item textures should be **16x16 pixels** (can be higher for HD resource packs, but 16x16 is standard).

#### Bug Nets (6 variants)

##### `item/basic_bug_net.png`

- **Resolution**: 16x16 pixels
- **Current Placeholder**: `minecraft:item/fishing_rod`
- **Design Notes**:
  - Simple wooden/stick handle
  - Basic mesh/net at the end
  - Durability: 64 uses
  - Should look crude/basic

##### `item/bug_net.png`

- **Resolution**: 16x16 pixels
- **Current Placeholder**: `minecraft:item/fishing_rod`
- **Design Notes**:
  - Better than basic, standard quality
  - Durability: 128 uses

##### `item/iron_bug_net.png`

- **Resolution**: 16x16 pixels
- **Current Placeholder**: `minecraft:item/iron_hoe`
- **Design Notes**:
  - Iron-reinforced handle
  - Sturdier mesh appearance
  - Gray/silver tones
  - Durability: 256 uses

##### `item/golden_bug_net.png`

- **Resolution**: 16x16 pixels
- **Current Placeholder**: `minecraft:item/golden_hoe`
- **Design Notes**:
  - Golden/yellow tones
  - Decorative but less durable than iron
  - Durability: 192 uses

##### `item/diamond_bug_net.png`

- **Resolution**: 16x16 pixels
- **Current Placeholder**: `minecraft:item/diamond_hoe`
- **Design Notes**:
  - Cyan/diamond tones
  - Very sturdy appearance
  - Durability: 512 uses

##### `item/netherite_bug_net.png`

- **Resolution**: 16x16 pixels
- **Current Placeholder**: `minecraft:item/netherite_hoe`
- **Design Notes**:
  - Dark gray/purple tones (netherite aesthetic)
  - Most premium appearance
  - Durability: 1024 uses (best)

#### Other Items

##### `item/field_guide.png`

- **Resolution**: 16x16 pixels
- **Current Placeholder**: `minecraft:item/book`
- **Design Notes**:
  - Book with insect illustrations on cover
  - Perhaps a butterfly or beetle icon
  - Brown leather binding
  - Research/encyclopedia feel

##### `item/specimen_jar.png`

- **Resolution**: 16x16 pixels
- **Current Placeholder**: `minecraft:item/glass_bottle`
- **Design Notes**:
  - Glass jar with cork/lid
  - Transparent body
  - Could show hint of captured specimen inside when filled

---

### **Spawn Egg Textures (Optional)**

Spawn eggs use Minecraft's built-in `template_spawn_egg` system, which auto-colors them. If you want **custom designs**:

- **Resolution**: 16x16 pixels each
- **Location**: `item/` or `item/spawn_eggs/`
- **Format**: PNG with transparency
- **Design**: Egg shape with insect-themed patterns
- **Current**: All use vanilla template (works fine, but custom is better)

**List**:

- `fly_spawn_egg.png`
- `butterfly_spawn_egg.png`
- `mosquito_spawn_egg.png`
- `cicada_spawn_egg.png`
- `damselfly_spawn_egg.png`
- `beetle_spawn_egg.png`
- `firefly_spawn_egg.png`
- `monarch_butterfly_spawn_egg.png`

---

### **Entity Textures**

These are used for rendering the actual insect mobs in the world.

#### Resolution & Format

- **Resolution**: Varies by entity (typically 32x32 or 64x64 for small entities)
- **Format**: PNG with transparency
- **Location**: `entity/` folder
- **Layout**: Depends on entity model (cuboid-based models use specific UV layouts)

#### Required Entity Textures

##### `entity/fly.png`

- **Suggested Size**: 32x32 pixels
- **Design**: Small flying insect, dark gray/black body, wings
- **Notes**: Common rarity, any biome

##### `entity/butterfly.png`

- **Suggested Size**: 32x32 or 64x32 pixels (for wing span)
- **Design**: Colorful wings (reds, oranges, yellows), delicate appearance
- **Notes**: Uncommon, meadows/jungles/forests

##### `entity/mosquito.png`

- **Suggested Size**: 32x32 pixels
- **Design**: Thin body, long legs, proboscis (stinger)
- **Notes**: Aggressive (red tint?), swamps/marshes

##### `entity/cicada.png`

- **Suggested Size**: 32x32 pixels
- **Design**: Large winged insect, greenish-brown tones
- **Notes**: Makes sounds, forests/jungles

##### `entity/damselfly.png`

- **Suggested Size**: 64x32 pixels (elongated body)
- **Design**: Long slender body, iridescent blue/green, 4 wings
- **Notes**: Near water (rivers/ponds)

##### `entity/beetle.png`

- **Suggested Size**: 32x32 pixels
- **Design**: Hard shell (elytra), dark brown/black, tough appearance
- **Notes**: Forests/jungles, any time

##### `entity/firefly.png`

- **Suggested Size**: 32x32 pixels
- **Design**: Dark body with glowing yellow/green abdomen
- **Notes**: Bioluminescent (should glow at night), swamps/rivers
- **Special**: May need emissive texture layer for glow effect

##### `entity/monarch_butterfly.png`

- **Suggested Size**: 64x32 pixels (large wings)
- **Design**: Distinctive orange wings with black veins and white spots
- **Notes**: Uncommon, plains/meadows, migratory

---

## 🔧 Current Placeholders Summary

| Texture                  | Status                 | Placeholder Used                             |
| ------------------------ | ---------------------- | -------------------------------------------- |
| **GUI**                  |                        |                                              |
| `research_station.png`   | **MISSING**            | None (needs creation)                        |
| **Items**                |                        |                                              |
| `basic_bug_net.png`      | Using placeholder      | `minecraft:item/fishing_rod`                 |
| `bug_net.png`            | Using placeholder      | `minecraft:item/fishing_rod`                 |
| `iron_bug_net.png`       | Using placeholder      | `minecraft:item/iron_hoe`                    |
| `golden_bug_net.png`     | Using placeholder      | `minecraft:item/golden_hoe`                  |
| `diamond_bug_net.png`    | Using placeholder      | `minecraft:item/diamond_hoe`                 |
| `netherite_bug_net.png`  | Using placeholder      | `minecraft:item/netherite_hoe`               |
| `field_guide.png`        | Using placeholder      | `minecraft:item/book`                        |
| `specimen_jar.png`       | Using placeholder      | `minecraft:item/glass_bottle`                |
| **Spawn Eggs**           |                        |                                              |
| All 8 spawn eggs         | Using vanilla template | `minecraft:item/template_spawn_egg`          |
| **Blocks**               |                        |                                              |
| `specimen_jar` block     | Using vanilla          | `minecraft:block/glass` + `stripped_oak_log` |
| `research_station` block | Using vanilla          | `minecraft:block/crafting_table_*`           |
| `display_case` block     | Using vanilla          | `minecraft:block/glass`                      |
| **Entities**             |                        |                                              |
| All 8 custom insects     | **MISSING**            | None (needs creation for rendering)          |

---

## 🎯 Priority Order

### **Critical (Breaks functionality without them)**

1. ✅ None - All placeholders work functionally

### **High Priority (Improves user experience significantly)**

1. `gui/research_station.png` - GUI looks broken without it
2. `item/basic_bug_net.png` - Main gameplay item
3. `item/bug_net.png` - Main gameplay item
4. `entity/fly.png` - Most common insect
5. `entity/butterfly.png` - Common insect

### **Medium Priority (Makes mod look more polished)**

6. `item/iron_bug_net.png`
7. `item/diamond_bug_net.png`
8. `item/netherite_bug_net.png`
9. `item/field_guide.png`
10. `item/specimen_jar.png`
11. All other entity textures

### **Low Priority (Nice to have)**

12. Custom spawn egg textures (vanilla template works fine)
13. `item/golden_bug_net.png` (gold is rarely used)

---

## 📐 Design Guidelines

### General Style

- **Match Minecraft's aesthetic**: Simple, pixelated, clear silhouettes
- **Use Minecraft's color palette**: Don't use colors that clash with vanilla
- **Readability**: Textures must be clear at 16x16 scale
- **Consistency**: Bug nets should have visual progression (basic → netherite)

### Color Themes by Tier

- **Basic**: Brown wood tones (#8B4513)
- **Stone/Iron**: Gray tones (#555555, #AAAAAA)
- **Golden**: Yellow/gold tones (#FFD700, #DAA520)
- **Diamond**: Cyan/light blue tones (#5DADE2, #00FFFF)
- **Netherite**: Dark purple-gray (#3D3D42, #664C4C)

### Entity Design Tips

- Keep entities small (insects are tiny)
- Use bright colors for visibility in-game
- Firefly should have emissive textures for glow effect
- Consider animation frames if entities will be animated

---

## 🛠️ Tools & Resources

### Recommended Software

- **Aseprite** (paid) - Best for pixel art
- **GIMP** (free) - Good for texture editing
- **Paint.NET** (free) - Simple and effective
- **Blockbench** (free) - For entity models + UV mapping

### Helpful Resources

- [Minecraft Texture Pack Guide](https://minecraft.fandom.com/wiki/Tutorials/Creating_a_resource_pack)
- [Nova Skin](https://minecraft.novaskin.me/) - Online texture editor
- View vanilla textures: `.minecraft/versions/1.21/1.21.jar` (open with WinRAR/7-Zip)

---

## ✅ Next Steps

1. **Start with GUI**: Create `research_station.png` (256x256)
2. **Create bug net set**: All 6 variants (16x16 each)
3. **Design key items**: field_guide.png, specimen_jar.png
4. **Model entities**: Create models in Blockbench, then textures
5. **Test in-game**: Use `/reload` to refresh textures without restarting

---

## 📝 Notes

- All textures must be **PNG format**
- Use **transparency** where appropriate (spawn eggs, GUIs)
- Texture pack structure must match exactly as listed above
- If textures are missing, game will use placeholders (won't crash)
- HD texture packs can use higher resolutions (32x32, 64x64, etc.)

---

**Mod Version**: 1.0.0
**Minecraft Version**: 1.21.10
**Last Updated**: December 10, 2025
