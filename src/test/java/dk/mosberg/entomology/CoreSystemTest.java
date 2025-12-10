package dk.mosberg.entomology;

import dk.mosberg.entomology.config.ConfigManager;
import dk.mosberg.entomology.registry.ModRegistry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for mod initialization and core systems.
 */
@DisplayName("Entomology Core Tests")
public class CoreSystemTest {

  @BeforeAll
  static void setup() {
    // Initialize systems if needed for testing
  }

  @Test
  @DisplayName("Config Manager should initialize with defaults")
  void testConfigManager() {
    ConfigManager config = ConfigManager.getInstance();
    assertNotNull(config, "Config manager should not be null");

    // Test default value retrieval
    boolean enabled = config.get("mechanics", "breeding.enabled", true);
    assertTrue(enabled, "Default breeding should be enabled");
  }

  @Test
  @DisplayName("Registry should initialize without errors")
  void testRegistryInitialization() {
    assertDoesNotThrow(() -> ModRegistry.initialize(),
        "Registry initialization should not throw exceptions");

    assertTrue(ModRegistry.isInitialized(),
        "Registry should be marked as initialized");
  }
}
