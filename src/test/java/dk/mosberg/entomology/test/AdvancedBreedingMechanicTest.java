package dk.mosberg.entomology.test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dk.mosberg.entomology.EntomologyMod;
import dk.mosberg.entomology.mechanics.impl.AdvancedBreedingMechanic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for AdvancedBreedingMechanic.
 */
class AdvancedBreedingMechanicTest {
  private AdvancedBreedingMechanic mechanic;

  @BeforeEach
  void setUp() {
    mechanic = new AdvancedBreedingMechanic();
    mechanic.onInitialize();
    mechanic.onEnable();
  }

  @Test
  void testMechanicConfiguration() {
    JsonObject config = new JsonObject();
    config.addProperty("enabled", true);
    config.addProperty("globalMutationRate", 0.1);
    config.addProperty("breedingCooldown", 3000);

    // Add required breeding pairs
    JsonArray breedingPairs = new JsonArray();
    config.add("breedingPairs", breedingPairs);

    mechanic.configure(config);

    JsonObject retrieved = mechanic.getConfiguration();
    assertEquals(0.1, retrieved.get("globalMutationRate").getAsDouble(), 0.001);
    assertEquals(3000, retrieved.get("breedingCooldown").getAsInt());
  }

  @Test
  void testMechanicId() {
    assertEquals(EntomologyMod.id("advanced_breeding"), mechanic.getId());
  }

  @Test
  void testMechanicVersion() {
    assertEquals("2.0.0", mechanic.getVersion());
  }

  @Test
  void testMechanicCategory() {
    assertEquals("BREEDING", mechanic.getCategory().toString());
  }

  @Test
  void testMechanicPriority() {
    assertEquals(600, mechanic.getPriority());
  }

  @Test
  void testLifecycleStates() {
    AdvancedBreedingMechanic testMechanic = new AdvancedBreedingMechanic();

    // Check initial state
    assertTrue(testMechanic.getState() != null);

    testMechanic.onInitialize();
    // State should change after initialization
    assertTrue(testMechanic.getState() != null);

    testMechanic.onEnable();
    testMechanic.onDisable();
    testMechanic.onShutdown();
  }

  @Test
  void testPerformanceMetrics() {
    var metrics = mechanic.getPerformanceMetrics();
    assertNotNull(metrics);
    assertEquals(0, metrics.getTotalExecutions());

    metrics.reset();
    assertEquals(0, metrics.getTotalExecutions());
  }

  @Test
  void testConfigParameters() {
    var params = mechanic.getConfigParameters();
    assertNotNull(params);
    assertTrue(params.containsKey("enabled"));
    assertTrue(params.containsKey("breedingPairs"));
    assertTrue(params.containsKey("globalMutationRate"));
  }
}
