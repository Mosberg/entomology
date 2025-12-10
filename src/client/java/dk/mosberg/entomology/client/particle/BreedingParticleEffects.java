package dk.mosberg.entomology.client.particle;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Handles particle effects for breeding mechanics.
 * Displays hearts, sparkles, and failure effects based on breeding outcomes.
 */
public class BreedingParticleEffects {
  private static final Random RANDOM = new Random();

  public static void register() {
    ClientTickEvents.END_CLIENT_TICK.register(client -> {
      // Breeding particle effects are triggered via explicit calls
      // from the BreedingMechanic when events occur
    });
  }

  /**
   * Spawn heart particles for successful breeding attempt.
   */
  public static void spawnBreedingHearts(World world, Vec3d pos) {
    MinecraftClient client = MinecraftClient.getInstance();
    if (client.world == null) {
      return;
    }

    for (int i = 0; i < 7; i++) {
      double offsetX = RANDOM.nextGaussian() * 0.02;
      double offsetY = RANDOM.nextDouble() * 0.5 + 0.5;
      double offsetZ = (RANDOM.nextDouble() - 0.5) * 0.02;

      client.particleManager.addParticle(ParticleTypes.HEART,
          pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ,
          0.0, 0.0, 0.0);
    }
  }

  /**
   * Spawn success particles for completed breeding.
   */
  public static void spawnBreedingSuccess(World world, Vec3d pos) {
    MinecraftClient client = MinecraftClient.getInstance();
    if (client.world == null) {
      return;
    }

    // Hearts
    spawnBreedingHearts(world, pos);

    // Happy villager particles
    for (int i = 0; i < 15; i++) {
      double offsetX = (RANDOM.nextDouble() - 0.5) * 0.5;
      double offsetY = RANDOM.nextDouble() * 0.5;
      double offsetZ = (RANDOM.nextDouble() - 0.5) * 0.5;

      client.particleManager.addParticle(ParticleTypes.HAPPY_VILLAGER,
          pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ,
          0.0, 0.1, 0.0);
    }
  }

  /**
   * Spawn failure particles for failed breeding.
   */
  public static void spawnBreedingFailure(World world, Vec3d pos) {
    MinecraftClient client = MinecraftClient.getInstance();
    if (client.world == null) {
      return;
    }

    for (int i = 0; i < 10; i++) {
      double offsetX = (RANDOM.nextDouble() - 0.5) * 0.3;
      double offsetY = RANDOM.nextDouble() * 0.3;
      double offsetZ = (RANDOM.nextDouble() - 0.5) * 0.3;

      client.particleManager.addParticle(ParticleTypes.SMOKE,
          pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ,
          0.0, 0.02, 0.0);
    }
  }

  /**
   * Spawn mutation particles for offspring with mutations.
   */
  public static void spawnMutationEffect(World world, Vec3d pos) {
    MinecraftClient client = MinecraftClient.getInstance();
    if (client.world == null) {
      return;
    }

    for (int i = 0; i < 20; i++) {
      double offsetX = (RANDOM.nextDouble() - 0.5) * 0.4;
      double offsetY = RANDOM.nextDouble() * 0.6;
      double offsetZ = (RANDOM.nextDouble() - 0.5) * 0.4;

      client.particleManager.addParticle(ParticleTypes.ENCHANT,
          pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ,
          0.0, 0.05, 0.0);
    }
  }
}
