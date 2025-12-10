package dk.mosberg.entomology.test;

import dk.mosberg.entomology.EntomologyMod;
import dk.mosberg.entomology.registry.advanced.ComponentRegistry;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for ComponentRegistry.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ComponentRegistryTest {
  private ComponentRegistry registry;

  @BeforeEach
  void setUp() {
    registry = ComponentRegistry.getInstance();
    registry.clear();
  }

  @Test
  void testRegisterAndGet() {
    Identifier id = EntomologyMod.id("test_component");
    String testValue = "Hello, World!";

    registry.registerSingleton(id, testValue);

    var result = registry.get(id, String.class);
    assertTrue(result.isPresent());
    assertEquals(testValue, result.get());
  }

  @Test
  void testRegisterWithFactory() {
    Identifier id = EntomologyMod.id("test_factory");

    registry.register(id, String.class, () -> "Factory Created");

    var result = registry.get(id, String.class);
    assertTrue(result.isPresent());
    assertEquals("Factory Created", result.get());
  }

  @Test
  void testDependencyResolution() {
    Identifier depId = EntomologyMod.id("dependency");
    Identifier mainId = EntomologyMod.id("main");

    registry.registerSingleton(depId, "Dependency");
    registry.registerSingleton(mainId, "Main", depId);

    registry.initializeAll();

    var main = registry.get(mainId, String.class);
    var dep = registry.get(depId, String.class);

    assertTrue(main.isPresent());
    assertTrue(dep.isPresent());
  }

  @Test
  void testGetAllByType() {
    registry.registerSingleton(EntomologyMod.id("string1"), "First");
    registry.registerSingleton(EntomologyMod.id("string2"), "Second");
    registry.registerSingleton(EntomologyMod.id("int1"), 42);

    var strings = registry.getAll(String.class);
    assertEquals(2, strings.size());

    var integers = registry.getAll(Integer.class);
    assertEquals(1, integers.size());
  }

  @Test
  void testDuplicateRegistration() {
    Identifier id = EntomologyMod.id("duplicate");

    registry.registerSingleton(id, "First");

    assertThrows(IllegalArgumentException.class, () -> {
      registry.registerSingleton(id, "Second");
    });
  }

  @Test
  void testClear() {
    registry.registerSingleton(EntomologyMod.id("test"), "Value");

    assertEquals(1, registry.getAll(String.class).size());

    registry.clear();

    assertEquals(0, registry.getAll(String.class).size());
  }
}
