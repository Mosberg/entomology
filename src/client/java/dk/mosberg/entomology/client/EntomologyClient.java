package dk.mosberg.entomology.client;

import dk.mosberg.entomology.EntomologyMod;
import dk.mosberg.entomology.client.hud.SpecimenHudOverlay;
import dk.mosberg.entomology.client.keybind.EntomologyKeybinds;
import dk.mosberg.entomology.client.model.FlyingInsectEntityModel;
import dk.mosberg.entomology.client.model.InsectEntityModel;
import dk.mosberg.entomology.client.particle.BreedingParticleEffects;
import dk.mosberg.entomology.client.particle.EnvironmentalParticleEffects;
import dk.mosberg.entomology.client.render.entity.EntomologyModelLayers;
import dk.mosberg.entomology.client.render.entity.FlyingInsectEntityRenderer;
import dk.mosberg.entomology.client.render.entity.InsectEntityRenderer;
import dk.mosberg.entomology.client.screen.ResearchStationScreen;
import dk.mosberg.entomology.client.tooltip.SpecimenTooltipHandler;
import dk.mosberg.entomology.entity.BeetleEntity;
import dk.mosberg.entomology.entity.CicadaEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.entity.EntityRendererFactories;

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

    // Register entity model layers
    registerModelLayers();

    // Register entity renderers
    registerEntityRenderers();

    // Register block entity renderers
    registerBlockEntityRenderers();
  }

  /**
   * Register entity model layers.
   */
  private void registerModelLayers() {
    EntityModelLayerRegistry.registerModelLayer(EntomologyModelLayers.INSECT,
        InsectEntityModel::getTexturedModelData);
    EntityModelLayerRegistry.registerModelLayer(EntomologyModelLayers.FLYING_INSECT,
        FlyingInsectEntityModel::getTexturedModelData);
  }

  /**
   * Register entity renderers for all custom entities.
   */
  private void registerEntityRenderers() {
    // Register custom renderers for insects
    EntityRendererFactories.register(EntomologyMod.beetle,
        (context) -> new InsectEntityRenderer<>(context, BeetleEntity.class));
    EntityRendererFactories.register(EntomologyMod.cicada,
        (context) -> new InsectEntityRenderer<>(context, CicadaEntity.class));

    // Flying insects
    EntityRendererFactories.register(EntomologyMod.butterfly,
        (context) -> new FlyingInsectEntityRenderer<>(context, "butterfly"));
    EntityRendererFactories.register(EntomologyMod.monarchButterfly,
        (context) -> new FlyingInsectEntityRenderer<>(context, "monarch_butterfly"));
    EntityRendererFactories.register(EntomologyMod.damselfly,
        (context) -> new FlyingInsectEntityRenderer<>(context, "damselfly"));
    EntityRendererFactories.register(EntomologyMod.firefly,
        (context) -> new FlyingInsectEntityRenderer<>(context, "firefly"));
    EntityRendererFactories.register(EntomologyMod.fly,
        (context) -> new FlyingInsectEntityRenderer<>(context, "fly"));
    EntityRendererFactories.register(EntomologyMod.mosquito,
        (context) -> new FlyingInsectEntityRenderer<>(context, "mosquito"));
  }

  /**
   * Register block entity renderers for display case and specimen jar.
   * Uses Minecraft 1.21 BlockEntityRenderer API with BlockEntityRenderState.
   *
   * Note: Block entity rendering deferred pending proper BlockEntityRenderState
   * implementation. Minecraft 1.21 requires custom render state classes.
   */
  private void registerBlockEntityRenderers() {
    // Block entity renderers for Display Case and Specimen Jar
    // Minecraft 1.21 API requires:
    // 1. Custom BlockEntityRenderState subclasses
    // 2. BlockEntityRenderer<T extends BlockEntity, S extends
    // BlockEntityRenderState>
    // 3. Registration via BlockEntityRendererFactories.register()
    //
    // Will implement once render state architecture is finalized
  }
}
