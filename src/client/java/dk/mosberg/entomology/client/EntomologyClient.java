package dk.mosberg.entomology.client;

import dk.mosberg.entomology.EntomologyMod;
import dk.mosberg.entomology.client.hud.SpecimenHudOverlay;
import dk.mosberg.entomology.client.keybind.EntomologyKeybinds;
import dk.mosberg.entomology.client.particle.BreedingParticleEffects;
import dk.mosberg.entomology.client.particle.EnvironmentalParticleEffects;
import dk.mosberg.entomology.client.screen.ResearchStationScreen;
import dk.mosberg.entomology.client.tooltip.SpecimenTooltipHandler;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

/**
 * Client-side mod initializer for Entomology.
 * Registers HUD overlays, tooltips, and client-side rendering.
 */
public class EntomologyClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    // Register HUD overlay for specimen information
    SpecimenHudOverlay.register();

    // Register enhanced tooltips for specimen jars
    SpecimenTooltipHandler.register();

    // Register research station GUI screen
    HandledScreens.register(EntomologyMod.researchStationScreenHandler, ResearchStationScreen::new);

    // Register client proxy for screen opening
    ClientProxy.register();

    // Register particle effect systems
    BreedingParticleEffects.register();
    EnvironmentalParticleEffects.register();

    // Register keybindings
    EntomologyKeybinds.register();
  }
}
