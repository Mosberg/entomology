package dk.mosberg.entomology.client;

import dk.mosberg.entomology.client.screen.FieldGuideScreen;
import dk.mosberg.entomology.data.DataDrivenRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

/**
 * Client-side proxy for opening screens.
 */
public class ClientProxy {

  public static void register() {
    // Initialization placeholder for future client-side registrations
  }

  public static void openFieldGuide() {
    MinecraftClient.getInstance().setScreen(
        new FieldGuideScreen(
            Text.translatable("item.entomology.field_guide"),
            DataDrivenRegistry.getResearchEntries()));
  }
}
