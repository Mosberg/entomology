package dk.mosberg.entomology.item;

import dk.mosberg.entomology.data.BugNetDefinition;
import dk.mosberg.entomology.data.BugNetReloader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class BugNetItem extends Item {
  private final String netId;

  public BugNetItem(Settings settings, String netId) {
    super(settings);
    this.netId = netId;
  }

  @Override
  public ActionResult useOnEntity(ItemStack stack, PlayerEntity user,
      LivingEntity entity, Hand hand) {
    if (user.getEntityWorld().isClient()) {
      return ActionResult.SUCCESS;
    }

    BugNetDefinition def = BugNetReloader.get(netId);
    if (def == null) {
      user.sendMessage(Text.literal("§cBug net definition not found: " + netId), true);
      return ActionResult.FAIL;
    }

    // Check special abilities for multi-capture
    if (def.getSpecialAbilities().isMultiCapture()) {
      return attemptMultiCapture(stack, user, entity, hand, def);
    } else {
      return attemptSingleCapture(stack, user, entity, hand, def);
    }
  }

  private ActionResult attemptSingleCapture(ItemStack stack, PlayerEntity user,
      LivingEntity entity, Hand hand,
      BugNetDefinition def) {
    Identifier entityId = Registries.ENTITY_TYPE.getId(entity.getType());

    // Check if boss and not allowed
    if (entity instanceof net.minecraft.entity.boss.WitherEntity
        || entity instanceof net.minecraft.entity.boss.dragon.EnderDragonEntity) {
      if (!def.getSpecialAbilities().canCaptureBoss()) {
        user.sendMessage(Text.literal("This net cannot capture boss entities!").formatted(Formatting.RED), true);
        return ActionResult.FAIL;
      }
    }

    if (!def.canCapture(entityId)) {
      user.sendMessage(Text.literal("This net cannot capture " + entityId).formatted(Formatting.YELLOW), true);
      return ActionResult.PASS;
    }

    double distance = user.getBlockPos().toCenterPos().distanceTo(entity.getBlockPos().toCenterPos());
    double effectiveRange = def.getRange() + def.getSpecialAbilities().getCaptureRadius();

    if (distance > effectiveRange) {
      user.sendMessage(Text.literal(String.format("Too far! (Max range: %.1f blocks)", effectiveRange))
          .formatted(Formatting.YELLOW), true);
      return ActionResult.PASS;
    }

    float catchChance = calculateCatchChance(user, entity, def);

    if (user.getRandom().nextFloat() < catchChance) {
      captureEntity(entity, user, stack, hand, def);
      user.sendMessage(Text.literal("Captured " + entityId + "!").formatted(Formatting.GREEN), true);
      return ActionResult.SUCCESS;
    } else {
      user.sendMessage(Text.literal(String.format("Capture failed! (%.0f%% chance)", catchChance * 100))
          .formatted(Formatting.RED), true);
      stack.damage(def.getDurabilityPerCapture(), user, hand);
      return ActionResult.FAIL;
    }
  }

  private ActionResult attemptMultiCapture(ItemStack stack, PlayerEntity user,
      LivingEntity targetEntity, Hand hand,
      BugNetDefinition def) {
    World world = user.getEntityWorld();
    double radius = def.getRange() + def.getSpecialAbilities().getCaptureRadius();
    Box box = Box.of(user.getBlockPos().toCenterPos(), radius * 2, radius * 2, radius * 2);

    List<LivingEntity> nearbyEntities = world.getEntitiesByClass(
        LivingEntity.class, box,
        e -> e != user && def.canCapture(Registries.ENTITY_TYPE.getId(e.getType())));

    int maxCaptures = def.getSpecialAbilities().getMaxCaptures();
    int captured = 0;

    for (LivingEntity entity : nearbyEntities) {
      if (captured >= maxCaptures) {
        break;
      }

      float catchChance = calculateCatchChance(user, entity, def);
      if (user.getRandom().nextFloat() < catchChance) {
        captureEntity(entity, user, stack, hand, def);
        captured++;
      }
    }

    if (captured > 0) {
      user.sendMessage(Text.literal(String.format("Captured %d entities!", captured))
          .formatted(Formatting.GREEN), true);
      return ActionResult.SUCCESS;
    } else {
      user.sendMessage(Text.literal("No entities captured!").formatted(Formatting.RED), true);
      stack.damage(def.getDurabilityPerCapture(), user, hand);
      return ActionResult.FAIL;
    }
  }

  private float calculateCatchChance(PlayerEntity user, LivingEntity entity, BugNetDefinition def) {
    float catchChance = def.getCatchRate();

    // Speed penalty/bonus
    double entitySpeed = entity.getVelocity().length();
    if (entitySpeed > 0.1) {
      catchChance *= def.getSpeedBonus();
    }

    // Rarity modifier (example - read from entity NBT in real implementation)
    String rarity = "common"; // TODO: Get from entity data
    catchChance *= def.getRarityBonus(rarity);

    // Health modifier - lower health = easier capture
    float healthPercent = entity.getHealth() / entity.getMaxHealth();
    catchChance *= (1.0f + (1.0f - healthPercent) * 0.3f); // Up to 30% bonus at low health

    return Math.max(0.0f, Math.min(1.0f, catchChance));
  }

  private void captureEntity(LivingEntity entity, PlayerEntity user, ItemStack stack, Hand hand, BugNetDefinition def) {
    // TODO: Create captured insect item with entity data
    entity.remove(Entity.RemovalReason.DISCARDED);
    stack.damage(def.getDurabilityPerCapture(), user, hand);
  }
}
