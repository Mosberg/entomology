package dk.mosberg.entomology.integration;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the complete system.
 */
public class IntegrationTest {

  private SystemIntegration integration;

  @BeforeEach
  void setUp() {
    integration = SystemIntegration.getInstance();
  }

  @Test
  @DisplayName("System should initialize without errors")
  void testSystemInitialization() {
    assertDoesNotThrow(() -> {
      integration.initialize();
    });
  }

  @Test
  @DisplayName("System should reload successfully")
  void testSystemReload() {
    integration.initialize();
    assertDoesNotThrow(() -> {
      integration.reload();
    });
  }

  @Test
  @DisplayName("System should validate configurations")
  void testConfigurationValidation() {
    integration.initialize();
    boolean valid = integration.validate();
    assertTrue(valid, "System validation should succeed");
  }

  @Test
  @DisplayName("System should shutdown cleanly")
  void testSystemShutdown() {
    integration.initialize();
    assertDoesNotThrow(() -> {
      integration.shutdown();
    });
  }

  @Test
  @DisplayName("Component registry should have registered mechanics")
  void testMechanicsRegistration() {
    integration.initialize();
    var registry = dk.mosberg.entomology.registry.advanced.ComponentRegistry.getInstance();
    var mechanics = registry.getAllMechanics();

    assertFalse(mechanics.isEmpty(), "Should have registered mechanics");
    assertTrue(mechanics.size() >= 2, "Should have at least 2 advanced mechanics");
  }
}
