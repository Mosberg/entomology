package dk.mosberg.entomology.client.config;

import dk.mosberg.entomology.config.EntomologyConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

/**
 * Configuration screen for Entomology mod.
 * Can be opened via Mod Menu integration once modmenu dependency is added.
 */
public class EntomologyConfigScreen extends Screen {
  private final Screen parent;

  public EntomologyConfigScreen(Screen parent) {
    super(Text.translatable("config.entomology.title"));
    this.parent = parent;
  }

  @Override
  protected void init() {
    super.init();
    int centerX = this.width / 2;
    int startY = this.height / 4;

    // Add configuration options here
    addConfigOption(centerX, startY,
        Text.translatable("config.entomology.enable_particles"),
        Text.literal(String.valueOf(EntomologyConfig.enableParticles)));

    addConfigOption(centerX, startY + 30,
        Text.translatable("config.entomology.enable_sounds"),
        Text.literal(String.valueOf(EntomologyConfig.enableSounds)));

    addConfigOption(centerX, startY + 60,
        Text.translatable("config.entomology.enable_hud"),
        Text.literal(String.valueOf(EntomologyConfig.enableHud)));

    // Done button
    this.addDrawableChild(ButtonWidget.builder(
        Text.translatable("gui.done"),
        button -> this.close()).dimensions(centerX - 100, this.height - 40, 200, 20).build());
  }

  private void addConfigOption(int centerX, int y, Text label, Text value) {
    // Label on left
    ClickableWidget labelWidget = ButtonWidget.builder(label, button -> {
    })
        .dimensions(centerX - 200, y, 150, 20)
        .build();
    labelWidget.active = false;
    this.addDrawableChild(labelWidget);

    // Toggle button on right
    this.addDrawableChild(ButtonWidget.builder(value, button -> {
      // Toggle logic here
      button.setMessage(button.getMessage().getString().equals("true")
          ? Text.literal("false")
          : Text.literal("true"));
    }).dimensions(centerX + 50, y, 80, 20).build());
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    super.render(context, mouseX, mouseY, delta);

    // Draw title
    context.drawCenteredTextWithShadow(this.textRenderer, this.title,
        this.width / 2, 20, 0xFFFFFF);
  }

  @Override
  public void close() {
    // Save config changes before closing
    EntomologyConfig.save();
    if (this.client != null) {
      this.client.setScreen(parent);
    }
  }
}
