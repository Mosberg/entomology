package dk.mosberg.entomology.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;

/**
 * Mosquito entity - a blood-feeding flying insect.
 */
public class MosquitoEntity extends PathAwareEntity {

  public MosquitoEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
    super(entityType, world);
    this.moveControl = new FlightMoveControl(this, 20, true);
  }

  public static DefaultAttributeContainer.Builder createMosquitoAttributes() {
    return PathAwareEntity.createMobAttributes()
        .add(EntityAttributes.MAX_HEALTH, 1.0)
        .add(EntityAttributes.FLYING_SPEED, 0.3)
        .add(EntityAttributes.MOVEMENT_SPEED, 0.18);
  }

  @Override
  protected EntityNavigation createNavigation(World world) {
    BirdNavigation birdNavigation = new BirdNavigation(this, world);
    birdNavigation.setCanSwim(false);
    return birdNavigation;
  }

  @Override
  protected void initGoals() {
    this.goalSelector.add(0, new SwimGoal(this));
    this.goalSelector.add(1, new EscapeDangerGoal(this, 1.7));
    this.goalSelector.add(2, new WanderAroundFarGoal(this, 0.8));
    this.goalSelector.add(3, new LookAroundGoal(this));
  }

  @Override
  public boolean canAvoidTraps() {
    return true;
  }
}
