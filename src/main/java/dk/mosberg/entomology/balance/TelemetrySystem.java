package dk.mosberg.entomology.balance;

import dk.mosberg.entomology.EntomologyMod;
import dk.mosberg.entomology.api.balance.IBalanceTuner;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Telemetry collection and balance tuning system.
 * Tracks gameplay metrics and dynamically adjusts parameters.
 */
public class TelemetrySystem {
  private static TelemetrySystem instance;

  private final Map<String, MetricCollector> collectors = new ConcurrentHashMap<>();
  private final Map<Identifier, IBalanceTuner> tuners = new ConcurrentHashMap<>();
  private final Map<Identifier, Double> adjustedValues = new ConcurrentHashMap<>();

  private boolean enabled = true;
  private long collectionInterval = 6000; // 5 minutes in ticks

  private TelemetrySystem() {
  }

  public static synchronized TelemetrySystem getInstance() {
    if (instance == null) {
      instance = new TelemetrySystem();
    }
    return instance;
  }

  /**
   * Records a metric value.
   */
  public void recordMetric(String key, double value) {
    if (!enabled) {
      return;
    }

    collectors.computeIfAbsent(key, k -> new MetricCollector(k))
        .record(value);
  }

  /**
   * Increments a counter metric.
   */
  public void incrementCounter(String key) {
    recordMetric(key, 1.0);
  }

  /**
   * Registers a balance tuner.
   */
  public void registerTuner(IBalanceTuner tuner) {
    tuners.put(tuner.getId(), tuner);
    EntomologyMod.LOGGER.debug("Registered balance tuner: {}", tuner.getId());
  }

  /**
   * Gets an adjusted parameter value.
   */
  public double getAdjustedValue(Identifier parameter, double baseValue) {
    if (!enabled) {
      return baseValue;
    }

    // Check cache
    Double cached = adjustedValues.get(parameter);
    if (cached != null) {
      return cached;
    }

    // Find tuner for this parameter
    IBalanceTuner tuner = findTunerForParameter(parameter);
    if (tuner == null) {
      return baseValue;
    }

    // Compute adjusted value
    TelemetryDataImpl telemetry = collectTelemetryData();
    double adjusted = tuner.computeAdjustedValue(baseValue, telemetry);
    adjustedValues.put(parameter, adjusted);

    return adjusted;
  }

  /**
   * Updates all balance tuners.
   */
  public void updateBalancing() {
    if (!enabled) {
      return;
    }

    TelemetryDataImpl telemetry = collectTelemetryData();

    for (IBalanceTuner tuner : tuners.values()) {
      try {
        Identifier param = tuner.getTargetParameter();
        double baseValue = 1.0; // Would get from config
        double adjusted = tuner.computeAdjustedValue(baseValue, telemetry);
        adjustedValues.put(param, adjusted);
      } catch (Exception e) {
        EntomologyMod.LOGGER.error("Error updating tuner: {}", tuner.getId(), e);
      }
    }

    EntomologyMod.LOGGER.debug("Updated {} balance parameters", adjustedValues.size());
  }

  /**
   * Resets telemetry data.
   */
  public void reset() {
    collectors.clear();
    adjustedValues.clear();
  }

  /**
   * Gets current metrics.
   */
  public Map<String, Double> getMetrics() {
    Map<String, Double> result = new HashMap<>();
    for (Map.Entry<String, MetricCollector> entry : collectors.entrySet()) {
      result.put(entry.getKey(), entry.getValue().getAverage());
    }
    return result;
  }

  /**
   * Finds tuner for a parameter.
   */
  private IBalanceTuner findTunerForParameter(Identifier parameter) {
    for (IBalanceTuner tuner : tuners.values()) {
      if (tuner.getTargetParameter().equals(parameter)) {
        return tuner;
      }
    }
    return null;
  }

  /**
   * Collects current telemetry data.
   */
  private TelemetryDataImpl collectTelemetryData() {
    Map<String, Double> metrics = new HashMap<>();
    long totalSamples = 0;

    for (Map.Entry<String, MetricCollector> entry : collectors.entrySet()) {
      MetricCollector collector = entry.getValue();
      metrics.put(entry.getKey(), collector.getAverage());
      totalSamples += collector.getSampleCount();
    }

    return new TelemetryDataImpl(metrics, collectionInterval, (int) totalSamples);
  }

  /**
   * Metric collector.
   */
  private static class MetricCollector {
    @SuppressWarnings("unused") // Used for identification
    private final String key;
    private double sum = 0.0;
    private long count = 0;
    private double min = Double.MAX_VALUE;
    private double max = Double.MIN_VALUE;

    MetricCollector(String key) {
      this.key = key;
    }

    void record(double value) {
      sum += value;
      count++;
      min = Math.min(min, value);
      max = Math.max(max, value);
    }

    double getAverage() {
      return count > 0 ? sum / count : 0.0;
    }

    @SuppressWarnings("unused") // Public API method for metric analysis
    public double getMin() {
      return min;
    }

    @SuppressWarnings("unused") // Public API method for metric analysis
    public double getMax() {
      return max;
    }

    long getSampleCount() {
      return count;
    }
  }

  /**
   * Telemetry data implementation.
   */
  private static class TelemetryDataImpl implements IBalanceTuner.ITelemetryData {
    private final Map<String, Double> metrics;
    private final long timePeriod;
    private final int sampleSize;

    TelemetryDataImpl(Map<String, Double> metrics, long timePeriod, int sampleSize) {
      this.metrics = metrics;
      this.timePeriod = timePeriod;
      this.sampleSize = sampleSize;
    }

    @Override
    public double getMetric(String key) {
      return metrics.getOrDefault(key, 0.0);
    }

    @Override
    public Map<String, Double> getAllMetrics() {
      return new HashMap<>(metrics);
    }

    @Override
    public long getTimePeriod() {
      return timePeriod;
    }

    @Override
    public int getSampleSize() {
      return sampleSize;
    }
  }

  /**
   * Enables or disables telemetry.
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * Checks if telemetry is enabled.
   */
  public boolean isEnabled() {
    return enabled;
  }
}
