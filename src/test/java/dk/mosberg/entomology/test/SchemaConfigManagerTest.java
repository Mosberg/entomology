package dk.mosberg.entomology.test;

import com.google.gson.JsonObject;
import dk.mosberg.entomology.config.advanced.SchemaConfigManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit tests for SchemaConfigManager.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SchemaConfigManagerTest {
  private Path tempDir;
  private Path configDir;
  private Path schemaDir;
  private SchemaConfigManager configManager;

  @BeforeAll
  void setUp() throws Exception {
    tempDir = Files.createTempDirectory("entomology-test");
    configDir = tempDir.resolve("config");
    schemaDir = tempDir.resolve("schema");
    Files.createDirectories(configDir);
    Files.createDirectories(schemaDir);

    configManager = new SchemaConfigManager(configDir, schemaDir);
  }

  @AfterAll
  void tearDown() throws Exception {
    // Clean up temp directory
    Files.walk(tempDir)
        .sorted((a, b) -> -a.compareTo(b))
        .forEach(path -> {
          try {
            Files.delete(path);
          } catch (Exception e) {
            // Ignore
          }
        });
  }

  @Test
  void testConfigSaveAndLoad() {
    JsonObject config = new JsonObject();
    config.addProperty("version", "1.0.0");
    config.addProperty("enabled", true);
    config.addProperty("testValue", 42);

    configManager.saveConfig("test", config);

    // Create simple schema for testing
    JsonObject schema = new JsonObject();
    schema.addProperty("$schema", "http://json-schema.org/draft-07/schema#");
    schema.addProperty("type", "object");

    String schemaPath = "test-schema.json";
    Path schemaFile = schemaDir.resolve(schemaPath);
    try {
      Files.writeString(schemaFile, schema.toString());
    } catch (Exception e) {
      fail("Failed to write schema file: " + e.getMessage());
    }

    JsonObject loaded = configManager.loadConfig("test", schemaPath);

    assertNotNull(loaded);
    assertEquals("1.0.0", loaded.get("version").getAsString());
    assertTrue(loaded.get("enabled").getAsBoolean());
    assertEquals(42, loaded.get("testValue").getAsInt());
  }

  @Test
  void testGetAndSet() {
    JsonObject config = new JsonObject();
    config.addProperty("version", "1.0.0");
    config.addProperty("testValue", 100);

    configManager.saveConfig("test2", config);

    int value = configManager.get("test2", "testValue", Integer.class, 0);
    assertEquals(100, value);

    configManager.set("test2", "testValue", 200);

    int updated = configManager.get("test2", "testValue", Integer.class, 0);
    assertEquals(200, updated);
  }

  @Test
  void testConfigListener() {
    final boolean[] listenerCalled = { false };

    configManager.addListener("test3", config -> {
      listenerCalled[0] = true;
    });

    JsonObject config = new JsonObject();
    config.addProperty("version", "1.0.0");
    configManager.saveConfig("test3", config);

    assertTrue(listenerCalled[0], "Listener should be called");
  }
}
