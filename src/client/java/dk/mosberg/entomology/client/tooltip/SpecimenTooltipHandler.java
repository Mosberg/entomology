package dk.mosberg.entomology.client.tooltip;

import dk.mosberg.entomology.data.DataDrivenRegistry;
import dk.mosberg.entomology.data.SpecimenDefinition;
import dk.mosberg.entomology.item.SpecimenJarItem;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

/**
 * Enhanced tooltips for specimen jars showing detailed stats and environmental
 * preferences.
 */
public class SpecimenTooltipHandler implements ItemTooltipCallback {

  public static void register() {
    ItemTooltipCallback.EVENT.register(new SpecimenTooltipHandler());
  }

  @Override
  public void getTooltip(ItemStack stack, Item.TooltipContext context, TooltipType type, List<Text> lines) {
    if (!(stack.getItem() instanceof SpecimenJarItem)) {
      return;
    }

    String specimenId = SpecimenJarItem.getSpecimenId(stack);
    if (specimenId == null) {
      lines.add(Text.literal("Empty").formatted(Formatting.GRAY, Formatting.ITALIC));
      return;
    }

    SpecimenDefinition specimen = DataDrivenRegistry.getSpecimen(specimenId);
    if (specimen == null) {
      lines.add(Text.literal("Unknown Specimen").formatted(Formatting.RED));
      return;
    }

    // Specimen ID
    lines.add(Text.literal(""));
    lines.add(Text.translatable("specimen.entomology." + specimen.id())
        .formatted(Formatting.GREEN, Formatting.BOLD));

    // Rarity
    Formatting rarityColor = getRarityColor(specimen.rarity());
    lines.add(Text.literal("Rarity: ").formatted(Formatting.GRAY)
        .append(Text.literal(specimen.rarity()).formatted(rarityColor)));

    // Size
    lines.add(Text.literal("Size: " + String.format("%.2f", specimen.size()))
        .formatted(Formatting.GRAY));

    // XP Value
    lines.add(Text.literal("Experience: " + specimen.experienceValue())
        .formatted(Formatting.YELLOW));

    // Breeding status
    if (specimen.canBreed()) {
      lines.add(Text.literal(""));
      lines.add(Text.literal("✓ Breedable").formatted(Formatting.LIGHT_PURPLE));
    }

    // Show entity type
    lines.add(Text.literal(""));
    lines.add(Text.literal("Entity: ").formatted(Formatting.GRAY)
        .append(Text.literal(specimen.entityType().toString()).formatted(Formatting.DARK_AQUA)));

    // Advanced info (shift to expand)
    if (type.isAdvanced()) {
      lines.add(Text.literal(""));
      lines.add(Text.literal("― Advanced Info ―").formatted(Formatting.DARK_GRAY));
      lines.add(Text.literal("ID: " + specimen.id()).formatted(Formatting.DARK_GRAY));
    }
  }

  private Formatting getRarityColor(String rarity) {
    return switch (rarity.toLowerCase()) {
      case "common" -> Formatting.WHITE;
      case "uncommon" -> Formatting.GREEN;
      case "rare" -> Formatting.BLUE;
      case "epic" -> Formatting.DARK_PURPLE;
      case "legendary" -> Formatting.GOLD;
      default -> Formatting.GRAY;
    };
  }
}
