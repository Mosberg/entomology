package dk.mosberg.entomology.client.hud;

import dk.mosberg.entomology.data.DataDrivenRegistry;
import dk.mosberg.entomology.data.SpecimenDefinition;
import dk.mosberg.entomology.item.SpecimenJarItem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * HUD overlay showing specimen information when holding a specimen jar.
 * Displays environmental preferences, rarity, and breeding compatibility.
 */
@SuppressWarnings("deprecation") // HudRenderCallback still functional
public class SpecimenHudOverlay implements HudRenderCallback {
  private static final int PANEL_WIDTH = 200;
  private static final int PANEL_HEIGHT = 80;
  private static final int PADDING = 8;
  private static final int BACKGROUND_COLOR = 0x88000000;
  private static final int BORDER_COLOR = 0xFF4CAF50;

  public static void register() {
    HudRenderCallback.EVENT.register(new SpecimenHudOverlay());
  }

  @Override
  public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
    MinecraftClient client = MinecraftClient.getInstance();
    if (client.player == null || client.isPaused()) {
      return;
    }

    ItemStack mainHand = client.player.getMainHandStack();
    ItemStack offHand = client.player.getOffHandStack();
    ItemStack jarStack = null;

    if (mainHand.getItem() instanceof SpecimenJarItem) {
      jarStack = mainHand;
    } else if (offHand.getItem() instanceof SpecimenJarItem) {
      jarStack = offHand;
    }

    if (jarStack == null) {
      return;
    }

    String specimenId = SpecimenJarItem.getSpecimenId(jarStack);
    if (specimenId == null) {
      return;
    }

    SpecimenDefinition specimen = DataDrivenRegistry.getSpecimen(specimenId);
    if (specimen == null) {
      return;
    }

    renderSpecimenPanel(drawContext, client, specimen);
  }

  private void renderSpecimenPanel(DrawContext context, MinecraftClient client, SpecimenDefinition specimen) {
    int screenWidth = client.getWindow().getScaledWidth();

    int x = screenWidth - PANEL_WIDTH - 10;
    int y = 10;

    // Draw background
    context.fill(x, y, x + PANEL_WIDTH, y + PANEL_HEIGHT, BACKGROUND_COLOR);

    // Draw border manually
    context.fill(x, y, x + PANEL_WIDTH, y + 1, BORDER_COLOR); // Top
    context.fill(x, y + PANEL_HEIGHT - 1, x + PANEL_WIDTH, y + PANEL_HEIGHT, BORDER_COLOR); // Bottom
    context.fill(x, y, x + 1, y + PANEL_HEIGHT, BORDER_COLOR); // Left
    context.fill(x + PANEL_WIDTH - 1, y, x + PANEL_WIDTH, y + PANEL_HEIGHT, BORDER_COLOR); // Right

    TextRenderer textRenderer = client.textRenderer;
    int textX = x + PADDING;
    int textY = y + PADDING;

    // Specimen name
    Text name = Text.translatable("specimen.entomology." + specimen.id())
        .formatted(Formatting.BOLD, Formatting.GREEN);
    context.drawText(textRenderer, name, textX, textY, 0xFFFFFF, true);
    textY += 12;

    // Rarity
    Formatting rarityColor = getRarityColor(specimen.rarity());
    Text rarityText = Text.literal("Rarity: ").formatted(Formatting.GRAY)
        .append(Text.literal(specimen.rarity()).formatted(rarityColor));
    context.drawText(textRenderer, rarityText, textX, textY, 0xFFFFFF, false);
    textY += 10;

    // Size
    Text sizeText = Text.literal("Size: " + String.format("%.1f", specimen.size()))
        .formatted(Formatting.GRAY);
    context.drawText(textRenderer, sizeText, textX, textY, 0xFFFFFF, false);
    textY += 10;

    // Experience value
    Text expText = Text.literal("XP Value: " + specimen.experienceValue())
        .formatted(Formatting.YELLOW);
    context.drawText(textRenderer, expText, textX, textY, 0xFFFFFF, false);
    textY += 10;

    // Breeding hint
    if (specimen.canBreed()) {
      Text breedText = Text.literal("✓ Can Breed").formatted(Formatting.LIGHT_PURPLE);
      context.drawText(textRenderer, breedText, textX, textY, 0xFFFFFF, false);
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
