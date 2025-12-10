package dk.mosberg.entomology.client.render.entity;

import dk.mosberg.entomology.client.model.FlyingInsectEntityModel;
import dk.mosberg.entomology.client.render.entity.state.ButterflyEntityRenderState;
import dk.mosberg.entomology.client.render.entity.state.DamselflyEntityRenderState;
import dk.mosberg.entomology.client.render.entity.state.FireflyEntityRenderState;
import dk.mosberg.entomology.client.render.entity.state.FlyEntityRenderState;
import dk.mosberg.entomology.client.render.entity.state.MonarchButterflyEntityRenderState;
import dk.mosberg.entomology.client.render.entity.state.MosquitoEntityRenderState;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.Identifier;

/**
 * Generic renderer for flying insects.
 * Used for butterflies, fireflies, flies, mosquitoes, etc.
 * Uses custom render states for Minecraft 1.21+ compatibility.
 */
public class FlyingInsectEntityRenderer<T extends PathAwareEntity>
    extends MobEntityRenderer<T, LivingEntityRenderState, FlyingInsectEntityModel> {
  private final Identifier texture;
  private final String textureName;

  public FlyingInsectEntityRenderer(EntityRendererFactory.Context context, String textureName) {
    super(context, new FlyingInsectEntityModel(context.getPart(EntomologyModelLayers.FLYING_INSECT)), 0.3f);
    this.textureName = textureName;
    this.texture = Identifier.of("entomology", "textures/entity/" + textureName + ".png");
  }

  @Override
  public Identifier getTexture(LivingEntityRenderState state) {
    return texture;
  }

  @Override
  public LivingEntityRenderState createRenderState() {
    // Return appropriate custom render state based on texture name
    return switch (textureName) {
      case "butterfly" -> new ButterflyEntityRenderState();
      case "monarch_butterfly" -> new MonarchButterflyEntityRenderState();
      case "damselfly" -> new DamselflyEntityRenderState();
      case "firefly" -> new FireflyEntityRenderState();
      case "fly" -> new FlyEntityRenderState();
      case "mosquito" -> new MosquitoEntityRenderState();
      default -> new LivingEntityRenderState();
    };
  }
}
