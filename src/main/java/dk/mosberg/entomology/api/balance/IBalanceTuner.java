package dk.mosberg.entomology.api.balance;

import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

import java.util.Map;

/**
 * Interface for adaptive balance tuning.
 * Allows dynamic adjustment of gameplay parameters based on telemetry.
 */
public interface IBalanceTuner {
  /**
   * Gets the tuner identifier.
   *
   * @return tuner ID
   */
  Identifier getId();

  /**
   * Gets the target parameter this tuner affects.
   *
   * @return parameter identifier
   */
  Identifier getTargetParameter();

  /**
   * Computes the adjusted value for a parameter.
   *
   * @param baseValue base parameter value
   * @param telemetry current telemetry data
   * @return adjusted value
   */
  double computeAdjustedValue(double baseValue, ITelemetryData telemetry);

  /**
   * Gets the tuning strategy.
   *
   * @return strategy type
   */
  TuningStrategy getStrategy();

  /**
   * Configures the tuner from JSON.
   *
   * @param config configuration
   */
  void configure(JsonObject config);

  /**
   * Gets the adjustment bounds.
   *
   * @return min and max multipliers
   */
  AdjustmentBounds getBounds();

  /**
   * Gets the tuning rate (how quickly adjustments happen).
   *
   * @return rate (0.0 - 1.0)
   */
  double getTuningRate();

  /**
   * Tuning strategies.
   */
  enum TuningStrategy {
    /** Linear interpolation */
    LINEAR,
    /** Exponential curve */
    EXPONENTIAL,
    /** Logarithmic curve */
    LOGARITHMIC,
    /** Step-based thresholds */
    STEPPED,
    /** PID controller */
    PID,
    /** Custom formula */
    CUSTOM
  }

  /**
   * Adjustment bounds.
   */
  interface AdjustmentBounds {
    double getMinMultiplier();

    double getMaxMultiplier();

    default double clamp(double value) {
      return Math.max(getMinMultiplier(), Math.min(getMaxMultiplier(), value));
    }
  }

  /**
   * Telemetry data interface.
   */
  interface ITelemetryData {
    /**
     * Gets a metric value.
     *
     * @param key metric key
     * @return metric value
     */
    double getMetric(String key);

    /**
     * Gets all metrics.
     *
     * @return map of metrics
     */
    Map<String, Double> getAllMetrics();

    /**
     * Gets the time period covered (in ticks).
     *
     * @return time period
     */
    long getTimePeriod();

    /**
     * Gets the sample size.
     *
     * @return number of samples
     */
    int getSampleSize();
  }
}
