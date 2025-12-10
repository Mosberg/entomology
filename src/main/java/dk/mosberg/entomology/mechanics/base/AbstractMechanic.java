package dk.mosberg.entomology.mechanics.base;

import com.google.gson.JsonObject;
import dk.mosberg.entomology.api.mechanics.IAdvancedMechanic;
import dk.mosberg.entomology.api.mechanics.IMechanicContext;
import dk.mosberg.entomology.api.mechanics.IMechanicResult;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Base implementation for advanced mechanics.
 * Provides lifecycle management, performance tracking, and configuration
 * validation.
 */
public abstract class AbstractMechanic implements IAdvancedMechanic {
  private final Identifier id;
  private final String version;
  private final MechanicCategory category;
  private final int priority;

  private LifecycleState state = LifecycleState.UNINITIALIZED;
  private JsonObject configuration = new JsonObject();
  private final Map<String, IConfigParameter> parameters = new LinkedHashMap<>();
  private final PerformanceMetricsImpl metrics = new PerformanceMetricsImpl();

  protected AbstractMechanic(Identifier id, String version, MechanicCategory category, int priority) {
    this.id = id;
    this.version = version;
    this.category = category;
    this.priority = priority;
  }

  @Override
  public Identifier getId() {
    return id;
  }

  @Override
  public String getVersion() {
    return version;
  }

  @Override
  public MechanicCategory getCategory() {
    return category;
  }

  @Override
  public int getPriority() {
    return priority;
  }

  @Override
  public JsonObject getConfiguration() {
    return configuration.deepCopy();
  }

  @Override
  public Map<String, IConfigParameter> getConfigParameters() {
    return Collections.unmodifiableMap(parameters);
  }

  @Override
  public Collection<Identifier> getDependencies() {
    return Collections.emptyList();
  }

  @Override
  public IPerformanceMetrics getPerformanceMetrics() {
    return metrics;
  }

  @Override
  public LifecycleState getState() {
    return state;
  }

  @Override
  public void onInitialize() {
    if (state != LifecycleState.UNINITIALIZED) {
      throw new IllegalStateException("Already initialized");
    }
    registerParameters();
    state = LifecycleState.INITIALIZED;
  }

  @Override
  public void onEnable() {
    if (state != LifecycleState.INITIALIZED && state != LifecycleState.DISABLED) {
      throw new IllegalStateException("Must be initialized before enabling");
    }
    state = LifecycleState.ENABLED;
  }

  @Override
  public void onDisable() {
    if (state != LifecycleState.ENABLED) {
      throw new IllegalStateException("Not enabled");
    }
    state = LifecycleState.DISABLED;
  }

  @Override
  public void onShutdown() {
    state = LifecycleState.SHUTDOWN;
    configuration = null;
  }

  @Override
  public void configure(JsonObject config) {
    ValidationResult result = validateConfiguration(config);
    if (!result.isValid()) {
      throw new IllegalArgumentException("Invalid configuration: " + result.getErrors());
    }
    this.configuration = config.deepCopy();
    applyConfiguration(config);
  }

  @Override
  public IMechanicResult execute(IMechanicContext context) {
    if (state != LifecycleState.ENABLED) {
      return IMechanicResult.skip();
    }

    long startTime = System.nanoTime();
    try {
      IMechanicResult result = executeInternal(context);
      long executionTime = (System.nanoTime() - startTime) / 1_000_000; // Convert to ms
      metrics.recordExecution(executionTime);
      return result;
    } catch (Exception e) {
      long executionTime = (System.nanoTime() - startTime) / 1_000_000;
      metrics.recordExecution(executionTime);
      return IMechanicResult.failure("Execution failed: " + e.getMessage());
    }
  }

  @Override
  public ValidationResult validateConfiguration(JsonObject config) {
    List<String> errors = new ArrayList<>();
    List<String> warnings = new ArrayList<>();

    for (Map.Entry<String, IConfigParameter> entry : parameters.entrySet()) {
      String paramName = entry.getKey();
      IConfigParameter param = entry.getValue();

      if (param.isRequired() && !config.has(paramName)) {
        errors.add("Required parameter missing: " + paramName);
      }
    }

    return new SimpleValidationResult(errors.isEmpty(), errors, warnings);
  }

  /**
   * Registers configuration parameters.
   * Override to define parameters.
   */
  protected abstract void registerParameters();

  /**
   * Applies configuration values.
   * Override to read and apply config.
   */
  protected abstract void applyConfiguration(JsonObject config);

  /**
   * Internal execution logic.
   * Override to implement mechanic behavior.
   */
  protected abstract IMechanicResult executeInternal(IMechanicContext context);

  /**
   * Registers a configuration parameter.
   */
  protected void registerParameter(String name, String description, Class<?> type,
      Object defaultValue, boolean required) {
    parameters.put(name, new ConfigParameter(name, description, type, defaultValue, required));
  }

  /**
   * Configuration parameter implementation.
   */
  private static class ConfigParameter implements IConfigParameter {
    private final String name;
    private final String description;
    private final Class<?> type;
    private final Object defaultValue;
    private final boolean required;

    ConfigParameter(String name, String description, Class<?> type, Object defaultValue, boolean required) {
      this.name = name;
      this.description = description;
      this.type = type;
      this.defaultValue = defaultValue;
      this.required = required;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public String getDescription() {
      return description;
    }

    @Override
    public Class<?> getType() {
      return type;
    }

    @Override
    public Object getDefaultValue() {
      return defaultValue;
    }

    @Override
    public boolean isRequired() {
      return required;
    }

    @Override
    public Object getMinValue() {
      return null;
    }

    @Override
    public Object getMaxValue() {
      return null;
    }
  }

  /**
   * Performance metrics implementation.
   */
  private static class PerformanceMetricsImpl implements IPerformanceMetrics {
    private final AtomicLong totalExecutions = new AtomicLong(0);
    private final AtomicLong totalTime = new AtomicLong(0);
    private final AtomicLong maxTime = new AtomicLong(0);
    private volatile long lastTime = 0;

    void recordExecution(long timeMs) {
      totalExecutions.incrementAndGet();
      totalTime.addAndGet(timeMs);
      lastTime = timeMs;

      long current = maxTime.get();
      while (timeMs > current) {
        if (maxTime.compareAndSet(current, timeMs)) {
          break;
        }
        current = maxTime.get();
      }
    }

    @Override
    public long getTotalExecutions() {
      return totalExecutions.get();
    }

    @Override
    public long getAverageExecutionTime() {
      long executions = totalExecutions.get();
      return executions > 0 ? totalTime.get() / executions : 0;
    }

    @Override
    public long getMaxExecutionTime() {
      return maxTime.get();
    }

    @Override
    public long getLastExecutionTime() {
      return lastTime;
    }

    @Override
    public void reset() {
      totalExecutions.set(0);
      totalTime.set(0);
      maxTime.set(0);
      lastTime = 0;
    }
  }

  /**
   * Simple validation result.
   */
  private static class SimpleValidationResult implements ValidationResult {
    private final boolean valid;
    private final Collection<String> errors;
    private final Collection<String> warnings;

    SimpleValidationResult(boolean valid, Collection<String> errors, Collection<String> warnings) {
      this.valid = valid;
      this.errors = Collections.unmodifiableCollection(errors);
      this.warnings = Collections.unmodifiableCollection(warnings);
    }

    @Override
    public boolean isValid() {
      return valid;
    }

    @Override
    public Collection<String> getErrors() {
      return errors;
    }

    @Override
    public Collection<String> getWarnings() {
      return warnings;
    }
  }
}
