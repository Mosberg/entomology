package dk.mosberg.entomology.client.particle;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Renders environmental effects like temperature, humidity, and time-of-day
 * indicators.
 * Visual feedback for specimen environmental preferences.
 */
public class EnvironmentalParticleEffects {
  private static final Random RANDOM = new Random();

  public static void register() {
    ClientTickEvents.END_CLIENT_TICK.register(client -> {
      // Environmental particle updates would be triggered here
    });
  }

  /**
   * Spawn particles indicating hot/warm environment.
   */
  public static void spawnHeatEffect(World world, Vec3d pos) {
    MinecraftClient client = MinecraftClient.getInstance();
    if (client.world == null) {
      return;
    }

    for (int i = 0; i < 3; i++) {
      double offsetX = (RANDOM.nextDouble() - 0.5) * 0.3;
      double offsetY = RANDOM.nextDouble() * 0.5;
      double offsetZ = (RANDOM.nextDouble() - 0.5) * 0.3;

      client.particleManager.addParticle(ParticleTypes.FLAME,
          pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ,
          0.0, 0.01, 0.0);
    }
  }

  /**
   * Spawn particles indicating cold environment.
   */
  public static void spawnColdEffect(World world, Vec3d pos) {
    MinecraftClient client = MinecraftClient.getInstance();
    if (client.world == null) {
      return;
    }

    for (int i = 0; i < 3; i++) {
      double offsetX = (RANDOM.nextDouble() - 0.5) * 0.3;
      double offsetY = RANDOM.nextDouble() * 0.5;
      double offsetZ = (RANDOM.nextDouble() - 0.5) * 0.3;

      client.particleManager.addParticle(ParticleTypes.SNOWFLAKE,
          pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ,
          0.0, -0.01, 0.0);
    }
  }

  /**
   * Spawn particles indicating humid environment.
   */
  public static void spawnHumidityEffect(World world, Vec3d pos) {
    MinecraftClient client = MinecraftClient.getInstance();
    if (client.world == null) {
      return;
    }

    for (int i = 0; i < 2; i++) {
      double offsetX = (RANDOM.nextDouble() - 0.5) * 0.3;
      double offsetY = RANDOM.nextDouble() * 0.5;
      double offsetZ = (RANDOM.nextDouble() - 0.5) * 0.3;

      client.particleManager.addParticle(ParticleTypes.DRIPPING_WATER,
          pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ,
          0.0, 0.0, 0.0);
    }
  }

  /**
   * Spawn particles for nighttime preference indicator.
   */
  public static void spawnNightEffect(World world, Vec3d pos) {
    MinecraftClient client = MinecraftClient.getInstance();
    if (client.world == null) {
      return;
    }

    for (int i = 0; i < 2; i++) {
      double offsetX = (RANDOM.nextDouble() - 0.5) * 0.3;
      double offsetY = RANDOM.nextDouble() * 0.5;
      double offsetZ = (RANDOM.nextDouble() - 0.5) * 0.3;

      client.particleManager.addParticle(ParticleTypes.PORTAL,
          pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ,
          0.0, 0.02, 0.0);
    }
  }

  /**
   * Spawn particles for optimal environment (specimen is happy).
   */
  public static void spawnOptimalEffect(World world, Vec3d pos) {
    MinecraftClient client = MinecraftClient.getInstance();
    if (client.world == null) {
      return;
    }

    double offsetX = (RANDOM.nextDouble() - 0.5) * 0.2;
    double offsetY = RANDOM.nextDouble() * 0.3;
    double offsetZ = (RANDOM.nextDouble() - 0.5) * 0.2;

    client.particleManager.addParticle(ParticleTypes.HAPPY_VILLAGER,
        pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ,
        0.0, 0.05, 0.0);
  }

  /**
   * Spawn particles for poor environment (specimen is unhappy).
   */
  public static void spawnPoorEffect(World world, Vec3d pos) {
    MinecraftClient client = MinecraftClient.getInstance();
    if (client.world == null) {
      return;
    }

    double offsetX = (RANDOM.nextDouble() - 0.5) * 0.2;
    double offsetY = RANDOM.nextDouble() * 0.3;
    double offsetZ = (RANDOM.nextDouble() - 0.5) * 0.2;

    client.particleManager.addParticle(ParticleTypes.ANGRY_VILLAGER,
        pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ,
        0.0, 0.02, 0.0);
  }
}
