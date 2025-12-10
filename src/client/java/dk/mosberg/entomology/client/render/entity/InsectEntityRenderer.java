package dk.mosberg.entomology.client.render.entity;

import dk.mosberg.entomology.client.model.InsectEntityModel;
import dk.mosberg.entomology.client.render.entity.state.BeetleEntityRenderState;
import dk.mosberg.entomology.client.render.entity.state.CicadaEntityRenderState;
import dk.mosberg.entomology.entity.BeetleEntity;
import dk.mosberg.entomology.entity.CicadaEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.Identifier;

/**
 * Generic renderer for ground-dwelling insects.
 * Used for beetles, cicadas, etc.
 * Uses custom render states for Minecraft 1.21+ compatibility.
 */
public class InsectEntityRenderer<T extends PathAwareEntity>
    extends MobEntityRenderer<T, LivingEntityRenderState, InsectEntityModel> {
  private static final Identifier BEETLE_TEXTURE = Identifier.of("entomology", "textures/entity/beetle.png");
  private static final Identifier CICADA_TEXTURE = Identifier.of("entomology", "textures/entity/cicada.png");
  private static final Identifier GENERIC_TEXTURE = Identifier.of("entomology", "textures/entity/insect.png");

  private final Identifier texture;
  private final Class<?> entityClass;

  public InsectEntityRenderer(EntityRendererFactory.Context context, Class<?> entityClass) {
    super(context, new InsectEntityModel(context.getPart(EntomologyModelLayers.INSECT)), 0.3f);
    this.entityClass = entityClass;

    // Select texture based on entity type
    if (entityClass == BeetleEntity.class) {
      this.texture = BEETLE_TEXTURE;
    } else if (entityClass == CicadaEntity.class) {
      this.texture = CICADA_TEXTURE;
    } else {
      this.texture = GENERIC_TEXTURE;
    }
  }

  @Override
  public Identifier getTexture(LivingEntityRenderState state) {
    return texture;
  }

  @Override
  public LivingEntityRenderState createRenderState() {
    // Return appropriate custom render state based on entity type
    if (entityClass == BeetleEntity.class) {
      return new BeetleEntityRenderState();
    } else if (entityClass == CicadaEntity.class) {
      return new CicadaEntityRenderState();
    }
    return new LivingEntityRenderState();
  }
}
