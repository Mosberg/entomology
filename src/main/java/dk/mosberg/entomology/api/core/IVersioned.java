package dk.mosberg.entomology.api.core;

/**
 * Interface for versioned components.
 * Enables compatibility checking and migration support.
 */
public interface IVersioned {
  /**
   * Gets the version string.
   * Should follow semantic versioning (e.g., "1.2.3").
   *
   * @return version string
   */
  String getVersion();

  /**
   * Gets the minimum compatible version.
   * Components with versions below this cannot be used.
   *
   * @return minimum compatible version
   */
  default String getMinCompatibleVersion() {
    return "1.0.0";
  }

  /**
   * Checks if this version is compatible with another version.
   *
   * @param otherVersion version to check against
   * @return true if compatible
   */
  default boolean isCompatibleWith(String otherVersion) {
    return compareVersions(otherVersion, getMinCompatibleVersion()) >= 0;
  }

  /**
   * Compares two semantic version strings.
   *
   * @param v1 first version
   * @param v2 second version
   * @return negative if v1 < v2, 0 if equal, positive if v1 > v2
   */
  static int compareVersions(String v1, String v2) {
    String[] parts1 = v1.split("\\.");
    String[] parts2 = v2.split("\\.");

    int length = Math.max(parts1.length, parts2.length);
    for (int i = 0; i < length; i++) {
      int part1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
      int part2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;

      if (part1 != part2) {
        return part1 - part2;
      }
    }

    return 0;
  }
}
