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
 * Basic insect model for ground-dwelling insects with body, head, and legs.
 * Compatible with Minecraft 1.21+ EntityRenderState system.
 */
public class InsectEntityModel extends EntityModel<LivingEntityRenderState> {
  @SuppressWarnings("unused")
  private final ModelPart body;
  @SuppressWarnings("unused")
  private final ModelPart head;
  private final ModelPart legFrontLeft;
  private final ModelPart legFrontRight;
  private final ModelPart legMiddleLeft;
  private final ModelPart legMiddleRight;
  private final ModelPart legBackLeft;
  private final ModelPart legBackRight;

  public InsectEntityModel(ModelPart root) {
    super(root);
    this.body = root.getChild("body");
    this.head = root.getChild("head");
    this.legFrontLeft = root.getChild("leg_front_left");
    this.legFrontRight = root.getChild("leg_front_right");
    this.legMiddleLeft = root.getChild("leg_middle_left");
    this.legMiddleRight = root.getChild("leg_middle_right");
    this.legBackLeft = root.getChild("leg_back_left");
    this.legBackRight = root.getChild("leg_back_right");
  }

  public static TexturedModelData getTexturedModelData() {
    ModelData modelData = new ModelData();
    ModelPartData modelPartData = modelData.getRoot();

    // Body - elongated box
    modelPartData.addChild("body",
        ModelPartBuilder.create()
            .uv(0, 0)
            .cuboid(-2.0F, -2.0F, -3.0F, 4.0F, 3.0F, 6.0F, new Dilation(0.0F)),
        ModelTransform.of(0.0F, 20.0F, 0.0F, 0.0F, 0.0F, 0.0F));

    // Head - smaller box in front
    modelPartData.addChild("head",
        ModelPartBuilder.create()
            .uv(0, 9)
            .cuboid(-1.5F, -1.5F, -1.5F, 3.0F, 2.0F, 2.0F, new Dilation(0.0F)),
        ModelTransform.of(0.0F, 19.5F, -3.0F, 0.0F, 0.0F, 0.0F));

    // Legs - thin boxes
    modelPartData.addChild("leg_front_left",
        ModelPartBuilder.create()
            .uv(16, 0)
            .cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)),
        ModelTransform.of(2.0F, 21.0F, -2.0F, 0.0F, 0.0F, 0.0F));

    modelPartData.addChild("leg_front_right",
        ModelPartBuilder.create()
            .uv(16, 0)
            .cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)),
        ModelTransform.of(-2.0F, 21.0F, -2.0F, 0.0F, 0.0F, 0.0F));

    modelPartData.addChild("leg_middle_left",
        ModelPartBuilder.create()
            .uv(16, 0)
            .cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)),
        ModelTransform.of(2.0F, 21.0F, 0.0F, 0.0F, 0.0F, 0.0F));

    modelPartData.addChild("leg_middle_right",
        ModelPartBuilder.create()
            .uv(16, 0)
            .cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)),
        ModelTransform.of(-2.0F, 21.0F, 0.0F, 0.0F, 0.0F, 0.0F));

    modelPartData.addChild("leg_back_left",
        ModelPartBuilder.create()
            .uv(16, 0)
            .cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)),
        ModelTransform.of(2.0F, 21.0F, 2.0F, 0.0F, 0.0F, 0.0F));

    modelPartData.addChild("leg_back_right",
        ModelPartBuilder.create()
            .uv(16, 0)
            .cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)),
        ModelTransform.of(-2.0F, 21.0F, 2.0F, 0.0F, 0.0F, 0.0F));

    return TexturedModelData.of(modelData, 32, 32);
  }

  @Override
  public void setAngles(LivingEntityRenderState state) {
    super.setAngles(state);

    // Simple walking animation using age for animation progress
    float animationProgress = state.age * 0.6662F;
    float animationSpeed = 1.4F;

    this.legFrontLeft.pitch = (float) (Math.cos(animationProgress) * animationSpeed);
    this.legFrontRight.pitch = (float) (Math.cos(animationProgress + Math.PI) * animationSpeed);
    this.legMiddleLeft.pitch = (float) (Math.cos(animationProgress + Math.PI) * animationSpeed);
    this.legMiddleRight.pitch = (float) (Math.cos(animationProgress) * animationSpeed);
    this.legBackLeft.pitch = (float) (Math.cos(animationProgress) * animationSpeed);
    this.legBackRight.pitch = (float) (Math.cos(animationProgress + Math.PI) * animationSpeed);
  }
}
