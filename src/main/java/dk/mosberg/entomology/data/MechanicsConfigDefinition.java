package dk.mosberg.entomology.data;

/**
 * Global mechanics configuration loaded from JSON.
 * Controls various gameplay systems and their settings.
 */
public record MechanicsConfigDefinition(
    BreedingConfig breeding,
    EnvironmentConfig environment,
    ResearchConfig research,
    CaptureConfig capture,
    SpawningConfig spawning,
    DisplayConfig display,
    DifficultyConfig difficulty) {
  public record BreedingConfig(
      boolean enabled,
      double baseChance,
      double mutationChance,
      int cooldownTicks) {
    public BreedingConfig() {
      this(true, 0.25, 0.05, 6000);
    }
  }

  public record EnvironmentConfig(
      boolean enabled,
      boolean affectHealth,
      boolean affectBehavior,
      int updateInterval) {
    public EnvironmentConfig() {
      this(true, true, true, 100);
    }
  }

  public record ResearchConfig(
      boolean enabled,
      int baseResearchTime,
      double experienceMultiplier,
      boolean requireFieldGuide) {
    public ResearchConfig() {
      this(true, 200, 1.0, true);
    }
  }

  public record CaptureConfig(
      boolean enabled,
      double baseChance,
      boolean consumeDurability,
      boolean allowRecapture) {
    public CaptureConfig() {
      this(true, 0.5, true, false);
    }
  }

  public record SpawningConfig(
      boolean enabled,
      double spawnRateMultiplier,
      boolean respectBiomeRequirements,
      int maxEntitiesPerChunk) {
    public SpawningConfig() {
      this(true, 1.0, true, 10);
    }
  }

  public record DisplayConfig(
      boolean enabled,
      boolean showParticles,
      boolean showLabels,
      double entityScale) {
    public DisplayConfig() {
      this(true, true, true, 1.0);
    }
  }

  public record DifficultyConfig(
      String mode,
      DifficultyScaling scaling) {
    public DifficultyConfig() {
      this("normal", new DifficultyScaling());
    }
  }

  public record DifficultyScaling(
      double captureChanceMultiplier,
      double breedingChanceMultiplier,
      double mutationChanceMultiplier,
      int researchTimeMultiplier) {
    public DifficultyScaling() {
      this(1.0, 1.0, 1.0, 1);
    }
  }

  public MechanicsConfigDefinition() {
    this(
        new BreedingConfig(),
        new EnvironmentConfig(),
        new ResearchConfig(),
        new CaptureConfig(),
        new SpawningConfig(),
        new DisplayConfig(),
        new DifficultyConfig());
  }
}
