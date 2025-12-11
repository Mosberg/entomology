package dk.mosberg.entomology.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class BugNetReloader implements SimpleSynchronousResourceReloadListener {
  private static final Logger LOGGER = LoggerFactory.getLogger("Entomology");
  private static final Gson GSON = new Gson();
  private static final Map<String, BugNetDefinition> BUG_NETS = new HashMap<>();
  private static final Identifier ID = Identifier.of("entomology", "bug_nets");

  @Override
  public Identifier getFabricId() {
    return ID;
  }

  @Override
  public void reload(ResourceManager manager) {
    BUG_NETS.clear();

    var resources = manager.findResources("items",
        path -> path.getPath().endsWith("_bug_net.json"));

    resources.forEach((id, resource) -> {
      try (BufferedReader reader = new BufferedReader(
          new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

        JsonObject json = GSON.fromJson(reader, JsonObject.class);
        BugNetDefinition def = new BugNetDefinition(json);
        BUG_NETS.put(def.getId(), def);

        LOGGER.info("Loaded bug net: {} (tier: {}, catch rate: {}, range: {}, durability: {})",
            def.getId(), def.getTier(), def.getCatchRate(), def.getRange(), def.getDurability());

      } catch (Exception e) {
        LOGGER.error("Failed to load bug net data from {}", id, e);
      }
    });

    LOGGER.info("Loaded {} bug net definitions", BUG_NETS.size());
  }

  public static BugNetDefinition get(String id) {
    return BUG_NETS.get(id);
  }

  public static Map<String, BugNetDefinition> getAll() {
    return new HashMap<>(BUG_NETS);
  }

  public static boolean hasDefinition(String id) {
    return BUG_NETS.containsKey(id);
  }
}
