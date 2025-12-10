package dk.mosberg.entomology.item;

import dk.mosberg.entomology.EntomologyMod;
import dk.mosberg.entomology.data.DataDrivenRegistry;
import dk.mosberg.entomology.data.ItemDefinition;
import dk.mosberg.entomology.data.SpecimenDefinition;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

/**
 * Bug net for capturing insects defined in JSON.
 * Charges like a bow - hold right-click to charge, release to capture nearby
 * entity.
 */
public class BugNetItem extends Item {
  public BugNetItem(Settings settings) {
    super(settings);
  }

  private String getNetIdFromStack(ItemStack stack) {
    // Determine which bug net tier this is based on max damage
    int maxDamage = stack.getMaxDamage();
    if (maxDamage == 128) {
      return "basic_bug_net";
    }
    if (maxDamage == 256) {
      return "iron_bug_net";
    }
    if (maxDamage == 192) {
      return "golden_bug_net";
    }
    if (maxDamage == 512) {
      return "diamond_bug_net";
    }
    if (maxDamage == 1024) {
      return "netherite_bug_net";
    }
    return "basic_bug_net"; // fallback
  }

  @Override
  public int getMaxUseTime(ItemStack stack, LivingEntity user) {
    return 72000; // Same as bow
  }

  @Override
  public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
    // Called when charge completes (fully charged)
    if (!(user instanceof PlayerEntity player)) {
      return stack;
    }

    if (!world.isClient()) {
      performCapture(stack, world, player);
    }

    return stack;
  }

  @Override
  public ActionResult use(World world, PlayerEntity user, Hand hand) {
    user.setCurrentHand(hand);
    return ActionResult.CONSUME;
  }

  private void performCapture(ItemStack stack, World world, PlayerEntity player) {
    // Get the bug net definition for this specific net tier
    String netId = getNetIdFromStack(stack);
    ItemDefinition def = DataDrivenRegistry.getItem(netId);
    if (def == null) {
      EntomologyMod.LOGGER.warn("Bug net definition missing for: " + netId);
      return;
    }

    // Find nearest capturable entity in range (3 blocks)
    double range = 3.0;
    Box box = player.getBoundingBox().expand(range);
    List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, box, entity -> {
      if (entity == player) {
        return false;
      }

      @SuppressWarnings("deprecation")
      boolean match = def.captureTargets().stream()
          .anyMatch(id -> id.equals(entity.getType().getRegistryEntry().registryKey().getValue()));
      return match;
    });

    if (entities.isEmpty()) {
      player.sendMessage(Text.translatable("item.entomology.bug_net.no_target"), true);
      return;
    }

    // Capture the nearest entity
    LivingEntity target = entities.get(0);
    SpecimenDefinition specimen = DataDrivenRegistry.getSpecimenByEntityType(target.getType());
    if (specimen == null) {
      player.sendMessage(Text.translatable("item.entomology.bug_net.no_specimen"), true);
      return;
    }

    // Create specimen jar with captured insect
    ItemStack jarStack = new ItemStack(EntomologyMod.specimenJarItem);
    SpecimenJarItem.setSpecimenId(jarStack, specimen.id());

    // Play capture sound and remove entity
    world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ITEM_PICKUP,
        SoundCategory.PLAYERS, 0.7f, 1.2f);
    target.discard();

    // Give jar to player
    if (!player.getInventory().insertStack(jarStack)) {
      player.dropItem(jarStack, false);
    }

    // Damage the net
    stack.damage(1, player, player.getActiveHand());

    player.sendMessage(Text.translatable("item.entomology.bug_net.captured",
        Text.translatable(specimen.displayNameKey())), true);
  }
}
