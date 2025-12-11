package dk.mosberg.entomology.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BugNetDefinition {
  private final String id;
  private final String tier;
  private final String type;
  private final Set<Identifier> captureTargets;
  private final int durability;
  private final int maxStackSize;
  private final int durabilityPerCapture;
  private final float catchRate;
  private final float range;
  private final float speedBonus;
  private final Map<String, Float> rarityBonus;
  private final boolean fireproof;
  private final boolean enchantable;
  private final int enchantability;
  private final Identifier repairIngredient;
  private final SpecialAbilities specialAbilities;

  public BugNetDefinition(JsonObject json) {
    this.id = json.get("id").getAsString();
    this.tier = json.get("tier").getAsString();
    this.type = json.get("type").getAsString();

    // Parse capture targets
    this.captureTargets = new HashSet<>();
    JsonArray targets = json.getAsJsonArray("capture_targets");
    for (int i = 0; i < targets.size(); i++) {
      captureTargets.add(Identifier.of(targets.get(i).getAsString()));
    }

    this.durability = json.get("durability").getAsInt();
    this.maxStackSize = json.get("max_stack_size").getAsInt();
    this.durabilityPerCapture = json.get("durability_per_capture").getAsInt();
    this.catchRate = json.get("catchRate").getAsFloat();
    this.range = json.get("range").getAsFloat();
    this.speedBonus = json.get("speedBonus").getAsFloat();

    // Parse rarity bonuses
    this.rarityBonus = new HashMap<>();
    JsonObject bonus = json.getAsJsonObject("rarityBonus");
    bonus.entrySet().forEach(e -> rarityBonus.put(e.getKey(), e.getValue().getAsFloat()));

    this.fireproof = json.has("fireproof") && json.get("fireproof").getAsBoolean();
    this.enchantable = !json.has("enchantable") || json.get("enchantable").getAsBoolean();
    this.enchantability = json.get("enchantability").getAsInt();
    this.repairIngredient = Identifier.of(json.get("repairIngredient").getAsString());

    // Parse special abilities
    this.specialAbilities = json.has("special_abilities")
        ? new SpecialAbilities(json.getAsJsonObject("special_abilities"))
        : new SpecialAbilities();
  }

  // Getters
  public String getId() {
    return id;
  }

  public String getTier() {
    return tier;
  }

  public String getType() {
    return type;
  }

  public Set<Identifier> getCaptureTargets() {
    return captureTargets;
  }

  public int getDurability() {
    return durability;
  }

  public int getMaxStackSize() {
    return maxStackSize;
  }

  public int getDurabilityPerCapture() {
    return durabilityPerCapture;
  }

  public float getCatchRate() {
    return catchRate;
  }

  public float getRange() {
    return range;
  }

  public float getSpeedBonus() {
    return speedBonus;
  }

  public float getRarityBonus(String rarity) {
    return rarityBonus.getOrDefault(rarity.toLowerCase(), 1.0f);
  }

  public boolean isFireproof() {
    return fireproof;
  }

  public boolean isEnchantable() {
    return enchantable;
  }

  public int getEnchantability() {
    return enchantability;
  }

  public Identifier getRepairIngredient() {
    return repairIngredient;
  }

  public SpecialAbilities getSpecialAbilities() {
    return specialAbilities;
  }

  public boolean canCapture(Identifier entityId) {
    return captureTargets.contains(entityId);
  }

  // Inner class for special abilities
  public static class SpecialAbilities {
    private final boolean autoCapture;
    private final float captureRadius;
    private final boolean multiCapture;
    private final int maxCaptures;
    private final boolean captureBoss;

    public SpecialAbilities() {
      this.autoCapture = false;
      this.captureRadius = 0.0f;
      this.multiCapture = false;
      this.maxCaptures = 1;
      this.captureBoss = false;
    }

    public SpecialAbilities(JsonObject json) {
      this.autoCapture = json.has("auto_capture") && json.get("auto_capture").getAsBoolean();
      this.captureRadius = json.has("capture_radius") ? json.get("capture_radius").getAsFloat() : 0.0f;
      this.multiCapture = json.has("multi_capture") && json.get("multi_capture").getAsBoolean();
      this.maxCaptures = json.has("max_captures") ? json.get("max_captures").getAsInt() : 1;
      this.captureBoss = json.has("capture_boss") && json.get("capture_boss").getAsBoolean();
    }

    public boolean isAutoCapture() {
      return autoCapture;
    }

    public float getCaptureRadius() {
      return captureRadius;
    }

    public boolean isMultiCapture() {
      return multiCapture;
    }

    public int getMaxCaptures() {
      return maxCaptures;
    }

    public boolean canCaptureBoss() {
      return captureBoss;
    }
  }
}
