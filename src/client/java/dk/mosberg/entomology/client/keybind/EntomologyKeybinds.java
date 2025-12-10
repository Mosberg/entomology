package dk.mosberg.entomology.client.keybind;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Manages keybindings for Entomology mod.
 * Provides shortcuts for field guide, info toggle, and research station.
 * Keybindings can be changed in Options > Controls > Key Binds in Minecraft
 * settings.
 */
public final class EntomologyKeybinds {
  private static KeyBinding fieldGuideKey;
  private static KeyBinding infoToggleKey;
  private static KeyBinding researchStationKey;

  private static boolean infoEnabled = true;

  private EntomologyKeybinds() {
    // Utility class
  }

  /**
   * Register keybindings and their handlers.
   * All keybindings are fully configurable in Minecraft's key binds menu.
   */
  public static void register() {
    // Create custom category for Entomology keybindings
    KeyBinding.Category entomologyCategory = KeyBinding.Category.create(
        Identifier.of("entomologyjson", "entomology"));

    // Register keybindings using Fabric API helper
    fieldGuideKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "key.entomologyjson.field_guide",
        InputUtil.Type.KEYSYM,
        InputUtil.UNKNOWN_KEY.getCode(),
        entomologyCategory));

    infoToggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "key.entomologyjson.info_toggle",
        InputUtil.Type.KEYSYM,
        InputUtil.UNKNOWN_KEY.getCode(),
        entomologyCategory));

    researchStationKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "key.entomologyjson.research_station",
        InputUtil.Type.KEYSYM,
        InputUtil.UNKNOWN_KEY.getCode(),
        entomologyCategory));

    // Register tick handler for keybind actions
    ClientTickEvents.END_CLIENT_TICK.register(client -> {
      if (client.player == null || client.world == null) {
        return;
      }

      // Field Guide key pressed
      while (fieldGuideKey.wasPressed()) {
        client.player.sendMessage(
            Text.literal("Field Guide (Coming Soon)"),
            false);
      }

      // Info Toggle key pressed
      while (infoToggleKey.wasPressed()) {
        infoEnabled = !infoEnabled;
        client.player.sendMessage(
            Text.literal("Specimen Info: " + (infoEnabled ? "Enabled" : "Disabled")),
            true);
      }

      // Research Station key pressed
      while (researchStationKey.wasPressed()) {
        client.player.sendMessage(
            Text.literal("Research Station (Coming Soon)"),
            false);
      }
    });
  }

  /**
   * Check if specimen info display is enabled.
   *
   * @return true if info is enabled
   */
  public static boolean isInfoEnabled() {
    return infoEnabled;
  }
}
