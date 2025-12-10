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
 * Beetle entity - a hardy ground insect.
 */
public class BeetleEntity extends PathAwareEntity {

  public BeetleEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
    super(entityType, world);
  }

  public static DefaultAttributeContainer.Builder createBeetleAttributes() {
    return PathAwareEntity.createMobAttributes()
        .add(EntityAttributes.MAX_HEALTH, 4.0)
        .add(EntityAttributes.MOVEMENT_SPEED, 0.15);
  }

  @Override
  protected void initGoals() {
    this.goalSelector.add(0, new SwimGoal(this));
    this.goalSelector.add(1, new EscapeDangerGoal(this, 1.2));
    this.goalSelector.add(2, new WanderAroundFarGoal(this, 0.6));
    this.goalSelector.add(3, new LookAroundGoal(this));
  }
}
