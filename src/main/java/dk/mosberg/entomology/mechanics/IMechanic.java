package dk.mosberg.entomology.mechanics;

import com.google.gson.JsonObject;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

/**
 * Interface for gameplay mechanics that can be applied to specimens.
 * Mechanics define behavior such as breeding, mutations, environmental effects,
 * etc.
 *
 * Execution Order:
 * Mechanics are executed in priority order (higher priority first).
 * Use priority values between 0-1000, with 500 as default.
 *
 * Thread Safety:
 * Mechanics may be invoked from multiple threads. Implementations must be
 * thread-safe.
 */
public interface IMechanic {

  /**
   * Gets the unique identifier for this mechanic.
   *
   * @return mechanic identifier
   */
  Identifier getId();

  /**
   * Gets the execution priority.
   * Higher values execute first. Default: 500
   *
   * @return priority value (0-1000)
   */
  default int getPriority() {
    return 500;
  }

  /**
   * Checks if this mechanic applies to the given specimen.
   *
   * @param specimenId the specimen identifier
   * @param context    execution context with world, entity, etc.
   * @return true if this mechanic should be applied
   */
  boolean appliesTo(String specimenId, MechanicContext context);

  /**
   * Executes the mechanic logic.
   *
   * @param specimenId the specimen identifier
   * @param context    execution context
   * @return result of the mechanic execution
   */
  MechanicResult execute(String specimenId, MechanicContext context);

  /**
   * Loads mechanic configuration from JSON.
   * Called during data reload.
   *
   * @param config the JSON configuration
   */
  void configure(JsonObject config);

  /**
   * Context passed to mechanics during execution.
   */
  class MechanicContext {
    private final World world;
    private final LivingEntity entity;
    private final Object data;

    public MechanicContext(World world, LivingEntity entity, Object data) {
      this.world = world;
      this.entity = entity;
      this.data = data;
    }

    public World getWorld() {
      return world;
    }

    public LivingEntity getEntity() {
      return entity;
    }

    public Object getData() {
      return data;
    }
  }

  /**
   * Result of mechanic execution.
   */
  class MechanicResult {
    private final boolean success;
    private final String message;
    private final Object resultData;

    private MechanicResult(boolean success, String message, Object resultData) {
      this.success = success;
      this.message = message;
      this.resultData = resultData;
    }

    public static MechanicResult success() {
      return new MechanicResult(true, null, null);
    }

    public static MechanicResult success(Object data) {
      return new MechanicResult(true, null, data);
    }

    public static MechanicResult failure(String message) {
      return new MechanicResult(false, message, null);
    }

    public boolean isSuccess() {
      return success;
    }

    public String getMessage() {
      return message;
    }

    public Object getResultData() {
      return resultData;
    }
  }
}
