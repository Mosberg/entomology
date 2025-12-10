package dk.mosberg.entomology.api.mechanics;

import com.google.gson.JsonObject;
import dk.mosberg.entomology.api.core.ILifecycleAware;
import dk.mosberg.entomology.api.core.IVersioned;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Map;

/**
 * Advanced mechanic interface with lifecycle and version support.
 * Mechanics are pluggable gameplay systems that can be configured via JSON.
 */
public interface IAdvancedMechanic extends ILifecycleAware, IVersioned {
  /**
   * Gets the unique mechanic identifier.
   *
   * @return mechanic ID
   */
  Identifier getId();

  /**
   * Gets the mechanic category.
   *
   * @return category for organization
   */
  MechanicCategory getCategory();

  /**
   * Gets the execution priority.
   * Higher priority mechanics execute first.
   *
   * @return priority value
   */
  int getPriority();

  /**
   * Checks if this mechanic applies to a given context.
   *
   * @param context execution context
   * @return true if mechanic should execute
   */
  boolean appliesTo(IMechanicContext context);

  /**
   * Executes the mechanic logic.
   *
   * @param context execution context
   * @return execution result
   */
  IMechanicResult execute(IMechanicContext context);

  /**
   * Configures the mechanic from JSON data.
   *
   * @param config JSON configuration
   */
  void configure(JsonObject config);

  /**
   * Gets the current configuration.
   *
   * @return current config as JSON
   */
  JsonObject getConfiguration();

  /**
   * Validates the mechanic configuration.
   *
   * @param config configuration to validate
   * @return validation result
   */
  ValidationResult validateConfiguration(JsonObject config);

  /**
   * Gets configuration parameters.
   *
   * @return map of parameter names to metadata
   */
  Map<String, IConfigParameter> getConfigParameters();

  /**
   * Gets mechanic dependencies.
   *
   * @return collection of required mechanic IDs
   */
  Collection<Identifier> getDependencies();

  /**
   * Gets whether this mechanic can be disabled at runtime.
   *
   * @return true if can be disabled
   */
  default boolean isToggleable() {
    return true;
  }

  /**
   * Gets performance metrics for this mechanic.
   *
   * @return performance data
   */
  IPerformanceMetrics getPerformanceMetrics();

  /**
   * Mechanic categories for organization.
   */
  enum MechanicCategory {
    /** Breeding and genetics */
    BREEDING,
    /** Environmental interactions */
    ENVIRONMENTAL,
    /** Rarity and spawning */
    SPAWNING,
    /** Player interaction */
    INTERACTION,
    /** Research and progression */
    RESEARCH,
    /** Combat and behavior */
    BEHAVIOR,
    /** Economy and trading */
    ECONOMY,
    /** Custom category */
    CUSTOM
  }

  /**
   * Validation result interface.
   */
  interface ValidationResult {
    boolean isValid();

    Collection<String> getErrors();

    Collection<String> getWarnings();
  }

  /**
   * Configuration parameter metadata.
   */
  interface IConfigParameter {
    String getName();

    String getDescription();

    Class<?> getType();

    Object getDefaultValue();

    boolean isRequired();

    Object getMinValue();

    Object getMaxValue();
  }

  /**
   * Performance metrics interface.
   */
  interface IPerformanceMetrics {
    long getTotalExecutions();

    long getAverageExecutionTime();

    long getMaxExecutionTime();

    long getLastExecutionTime();

    void reset();
  }
}
