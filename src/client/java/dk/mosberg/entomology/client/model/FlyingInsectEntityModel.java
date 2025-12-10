package dk.mosberg.entomology.client.model;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;

/**
 * Flying insect model with body, head, wings, and legs.
 * Used for butterflies, dragonflies, fireflies, etc.
 * Compatible with Minecraft 1.21+ EntityRenderState system.
 */
public class FlyingInsectEntityModel extends EntityModel<LivingEntityRenderState> {
  @SuppressWarnings("unused")
  private final ModelPart body;
  @SuppressWarnings("unused")
  private final ModelPart head;
  private final ModelPart wingLeft;
  private final ModelPart wingRight;
  @SuppressWarnings("unused")
  private final ModelPart legLeft;
  @SuppressWarnings("unused")
  private final ModelPart legRight;

  public FlyingInsectEntityModel(ModelPart root) {
    super(root);
    this.body = root.getChild("body");
    this.head = root.getChild("head");
    this.wingLeft = root.getChild("wing_left");
    this.wingRight = root.getChild("wing_right");
    this.legLeft = root.getChild("leg_left");
    this.legRight = root.getChild("leg_right");
  }

  public static TexturedModelData getTexturedModelData() {
    ModelData modelData = new ModelData();
    ModelPartData modelPartData = modelData.getRoot();

    // Body - thin elongated box
    modelPartData.addChild("body",
        ModelPartBuilder.create()
            .uv(0, 0)
            .cuboid(-1.0F, -1.0F, -2.0F, 2.0F, 2.0F, 4.0F, new Dilation(0.0F)),
        ModelTransform.of(0.0F, 21.0F, 0.0F, 0.0F, 0.0F, 0.0F));

    // Head - small box
    modelPartData.addChild("head",
        ModelPartBuilder.create()
            .uv(0, 6)
            .cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)),
        ModelTransform.of(0.0F, 20.5F, -2.0F, 0.0F, 0.0F, 0.0F));

    // Wings - flat panels
    modelPartData.addChild("wing_left",
        ModelPartBuilder.create()
            .uv(8, 0)
            .cuboid(0.0F, 0.0F, -3.0F, 6.0F, 0.0F, 6.0F, new Dilation(0.0F)),
        ModelTransform.of(1.0F, 20.0F, 0.0F, 0.0F, 0.0F, 0.0F));

    modelPartData.addChild("wing_right",
        ModelPartBuilder.create()
            .uv(8, 6)
            .cuboid(-6.0F, 0.0F, -3.0F, 6.0F, 0.0F, 6.0F, new Dilation(0.0F)),
        ModelTransform.of(-1.0F, 20.0F, 0.0F, 0.0F, 0.0F, 0.0F));

    // Legs - small thin boxes
    modelPartData.addChild("leg_left",
        ModelPartBuilder.create()
            .uv(0, 10)
            .cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
        ModelTransform.of(1.0F, 22.0F, 0.0F, 0.0F, 0.0F, 0.0F));

    modelPartData.addChild("leg_right",
        ModelPartBuilder.create()
            .uv(0, 10)
            .cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
        ModelTransform.of(-1.0F, 22.0F, 0.0F, 0.0F, 0.0F, 0.0F));

    return TexturedModelData.of(modelData, 32, 32);
  }

  @Override
  public void setAngles(LivingEntityRenderState state) {
    super.setAngles(state);

    // Wing flapping animation
    float animationProgress = state.age;
    float wingFlap = (float) Math.sin(animationProgress * 0.5F) * 0.4F;
    this.wingLeft.roll = wingFlap;
    this.wingRight.roll = -wingFlap;
  }
}
