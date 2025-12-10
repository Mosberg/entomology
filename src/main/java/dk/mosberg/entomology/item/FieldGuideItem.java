package dk.mosberg.entomology.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * Field guide book that displays researched insect data.
 * Opens a client-side GUI when used.
 */
public class FieldGuideItem extends Item {
  public FieldGuideItem(Settings settings) {
    super(settings);
  }

  @Override
  public ActionResult use(World world, PlayerEntity user, Hand hand) {
    if (world.isClient()) {
      openFieldGuideScreen();
    }

    return ActionResult.SUCCESS;
  }

  @Environment(EnvType.CLIENT)
  private void openFieldGuideScreen() {
    try {
      Class<?> proxyClass = Class.forName("dk.mosberg.entomology.client.ClientProxy");
      java.lang.reflect.Method method = proxyClass.getMethod("openFieldGuide");
      method.invoke(null);
    } catch (Exception e) {
      // Ignore - client classes not available
    }
  }
}
