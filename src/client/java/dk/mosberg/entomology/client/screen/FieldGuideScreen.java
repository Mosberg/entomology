package dk.mosberg.entomology.client.screen;

import dk.mosberg.entomology.data.ResearchEntryDefinition;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Paginated field guide screen showing research entries.
 */
public class FieldGuideScreen extends Screen {
  private final List<ResearchEntryDefinition> entries;
  private int page;

  public FieldGuideScreen(Text title, Collection<ResearchEntryDefinition> defs) {
    super(title);
    this.entries = new ArrayList<>(defs);
  }

  @Override
  protected void init() {
    super.init();
    int centerX = this.width / 2;
    int centerY = this.height / 2;

    this.addDrawableChild(ButtonWidget.builder(Text.literal("<"), b -> {
      if (page > 0) {
        page--;
      }
    }).dimensions(centerX - 60, centerY + 60, 20, 20).build());

    this.addDrawableChild(ButtonWidget.builder(Text.literal(">"), b -> {
      if (page < entries.size() - 1) {
        page++;
      }
    }).dimensions(centerX + 40, centerY + 60, 20, 20).build());
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    super.render(context, mouseX, mouseY, delta);

    if (entries.isEmpty()) {
      context.drawText(this.textRenderer,
          Text.translatable("screen.entomology.field_guide.empty"),
          this.width / 2 - 80, this.height / 2, 0xFFFFFF, false);
      return;
    }

    ResearchEntryDefinition current = entries.get(page);
    int x = this.width / 2 - 80;
    int y = this.height / 2 - 40;

    context.drawText(this.textRenderer,
        Text.translatable(current.pageKey()),
        x, y, 0xFFFFFF, false);

    String pageLabel = (page + 1) + " / " + entries.size();
    context.drawText(this.textRenderer, Text.literal(pageLabel),
        x, y + 60, 0xAAAAAA, false);
  }
}
