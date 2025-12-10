package dk.mosberg.entomology.api.species;

import java.util.Map;

/**
 * Environmental requirements and preferences for a species.
 */
public interface IEnvironmentalData {
  /**
   * Gets the optimal temperature range (in Celsius).
   *
   * @return temperature range [min, max]
   */
  TemperatureRange getOptimalTemperature();

  /**
   * Gets the tolerable temperature range.
   * Outside this range, specimen health degrades.
   *
   * @return temperature range [min, max]
   */
  TemperatureRange getTolerableTemperature();

  /**
   * Gets the optimal humidity range (0.0 - 1.0).
   *
   * @return humidity range [min, max]
   */
  Range getOptimalHumidity();

  /**
   * Gets the light level preference.
   *
   * @return light preference
   */
  LightPreference getLightPreference();

  /**
   * Gets time of day preferences.
   *
   * @return time preference
   */
  TimePreference getTimePreference();

  /**
   * Gets custom environmental factors.
   *
   * @return map of factor keys to acceptable ranges
   */
  Map<String, Range> getCustomFactors();

  /**
   * Temperature range.
   */
  interface TemperatureRange {
    double getMin();

    double getMax();

    default boolean isWithinRange(double temperature) {
      return temperature >= getMin() && temperature <= getMax();
    }
  }

  /**
   * Generic numeric range.
   */
  interface Range {
    double getMin();

    double getMax();

    default boolean isWithinRange(double value) {
      return value >= getMin() && value <= getMax();
    }
  }

  /**
   * Light level preferences.
   */
  enum LightPreference {
    /** Requires darkness (0-3 light level) */
    DARK,
    /** Prefers dim light (4-7 light level) */
    DIM,
    /** Neutral about light */
    NEUTRAL,
    /** Prefers bright light (8-11 light level) */
    BRIGHT,
    /** Requires full light (12-15 light level) */
    FULL_LIGHT
  }

  /**
   * Time of day preferences.
   */
  enum TimePreference {
    /** Active during day only */
    DIURNAL,
    /** Active during night only */
    NOCTURNAL,
    /** Active during dawn/dusk */
    CREPUSCULAR,
    /** Active at all times */
    CATHEMERAL
  }
}
