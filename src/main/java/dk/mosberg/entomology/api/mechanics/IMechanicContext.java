package dk.mosberg.entomology.api.mechanics;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Optional;

/**
 * Context provided to mechanics during execution.
 * Contains all relevant data needed for mechanic decisions.
 */
public interface IMechanicContext {
  /**
   * Gets the world instance.
   *
   * @return world
   */
  World getWorld();

  /**
   * Gets the position context.
   *
   * @return position if applicable
   */
  Optional<BlockPos> getPosition();

  /**
   * Gets the player context.
   *
   * @return player if applicable
   */
  Optional<PlayerEntity> getPlayer();

  /**
   * Gets the primary entity context.
   *
   * @return entity if applicable
   */
  Optional<Entity> getEntity();

  /**
   * Gets the specimen ID being processed.
   *
   * @return specimen identifier if applicable
   */
  Optional<Identifier> getSpecimenId();

  /**
   * Gets custom context data.
   *
   * @param key data key
   * @return value if present
   */
  Optional<Object> getData(String key);

  /**
   * Gets all custom context data.
   *
   * @return immutable map of all context data
   */
  Map<String, Object> getAllData();

  /**
   * Gets the context type.
   *
   * @return context type
   */
  ContextType getType();

  /**
   * Gets the game time (in ticks).
   *
   * @return current game time
   */
  long getGameTime();

  /**
   * Context types.
   */
  enum ContextType {
    /** Breeding operation */
    BREEDING,
    /** Spawning/capturing */
    SPAWN,
    /** Environmental check */
    ENVIRONMENTAL,
    /** Player interaction */
    INTERACTION,
    /** Research operation */
    RESEARCH,
    /** Combat/behavior */
    BEHAVIOR,
    /** Custom context */
    CUSTOM
  }

  /**
   * Builder for creating context instances.
   */
  interface Builder {
    Builder world(World world);

    Builder position(BlockPos pos);

    Builder player(PlayerEntity player);

    Builder entity(Entity entity);

    Builder specimenId(Identifier id);

    Builder data(String key, Object value);

    Builder type(ContextType type);

    IMechanicContext build();
  }
}
