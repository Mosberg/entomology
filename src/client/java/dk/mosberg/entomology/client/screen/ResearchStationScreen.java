package dk.mosberg.entomology.client.screen;

import dk.mosberg.entomology.screen.ResearchStationScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

/**
 * GUI screen for research station.
 * Displays inventory slots and research progress.
 */
public class ResearchStationScreen extends HandledScreen<ResearchStationScreenHandler> {

  public ResearchStationScreen(ResearchStationScreenHandler handler, PlayerInventory inventory, Text title) {
    super(handler, inventory, title);
    this.backgroundHeight = 166;
    this.playerInventoryTitleY = this.backgroundHeight - 94;
  }

  @Override
  protected void init() {
    super.init();
    this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
  }

  @Override
  protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
    renderBackground(context, mouseX, mouseY, delta);
    int x = (this.width - this.backgroundWidth) / 2;
    int y = (this.height - this.backgroundHeight) / 2;

    // Simple gray background for GUI
    context.fill(x, y, x + this.backgroundWidth, y + this.backgroundHeight, 0xFFC6C6C6);

    // Draw borders
    context.fill(x, y, x + this.backgroundWidth, y + 1, 0xFF8B8B8B); // Top
    context.fill(x, y + this.backgroundHeight - 1, x + this.backgroundWidth, y + this.backgroundHeight, 0xFF8B8B8B); // Bottom
    context.fill(x, y, x + 1, y + this.backgroundHeight, 0xFF8B8B8B); // Left
    context.fill(x + this.backgroundWidth - 1, y, x + this.backgroundWidth, y + this.backgroundHeight, 0xFF8B8B8B); // Right
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    super.render(context, mouseX, mouseY, delta);
    this.drawMouseoverTooltip(context, mouseX, mouseY);
  }

  @Override
  protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
    super.drawForeground(context, mouseX, mouseY);

    // Draw additional UI elements
    Text instructions = Text.literal("Place jar + guide to research");
    int instructionX = (this.backgroundWidth - this.textRenderer.getWidth(instructions)) / 2;
    context.drawText(this.textRenderer, instructions, instructionX, 60, 0x404040, false);
  }
}
