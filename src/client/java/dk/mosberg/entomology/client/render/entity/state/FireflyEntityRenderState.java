package dk.mosberg.entomology.client.render.entity.state;

import net.minecraft.client.render.entity.state.LivingEntityRenderState;

/**
 * Custom render state for firefly entities.
 * Extends LivingEntityRenderState to store firefly-specific rendering data.
 *
 * Compatible with Minecraft 1.21+ EntityRenderState system.
 */
public class FireflyEntityRenderState extends LivingEntityRenderState {
  // Additional firefly-specific rendering state can be added here
  // For example: bioluminescence intensity, flash pattern, glow phase
  public float glowIntensity = 1.0f;
  public boolean isFlashing = false;
}
