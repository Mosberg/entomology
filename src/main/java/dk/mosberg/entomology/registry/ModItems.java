package dk.mosberg.entomology.registry;

import dk.mosberg.entomology.EntomologyMod;
import dk.mosberg.entomology.item.BugNetItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
  public static final Item BASIC_BUG_NET = registerBugNet("basic_bug_net", 128, false);
  public static final Item IRON_BUG_NET = registerBugNet("iron_bug_net", 256, false);
  public static final Item GOLDEN_BUG_NET = registerBugNet("golden_bug_net", 64, false);
  public static final Item DIAMOND_BUG_NET = registerBugNet("diamond_bug_net", 512, false);
  public static final Item NETHERITE_BUG_NET = registerBugNet("netherite_bug_net", 768, true);

  private static Item registerBugNet(String id, int maxDamage, boolean fireproof) {
    Item.Settings settings = new Item.Settings()
        .maxDamage(maxDamage)
        .maxCount(1);

    if (fireproof) {
      settings.fireproof();
    }

    return Registry.register(
        Registries.ITEM,
        Identifier.of(EntomologyMod.MODID, id),
        new BugNetItem(settings, id));
  }

  public static void register() {
    EntomologyMod.LOGGER.info("Registering items for " + EntomologyMod.MODID);
  }
}
