package dk.mosberg.entomology.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;

/**
 * Cicada entity - a loud singing insect.
 */
public class CicadaEntity extends PathAwareEntity {

  public CicadaEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
    super(entityType, world);
  }

  public static DefaultAttributeContainer.Builder createCicadaAttributes() {
    return PathAwareEntity.createMobAttributes()
        .add(EntityAttributes.MAX_HEALTH, 3.0)
        .add(EntityAttributes.MOVEMENT_SPEED, 0.18);
  }

  @Override
  protected void initGoals() {
    this.goalSelector.add(0, new SwimGoal(this));
    this.goalSelector.add(1, new EscapeDangerGoal(this, 1.3));
    this.goalSelector.add(2, new WanderAroundFarGoal(this, 0.7));
    this.goalSelector.add(3, new LookAroundGoal(this));
  }
}
