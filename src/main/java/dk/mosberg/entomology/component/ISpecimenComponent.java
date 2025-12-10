package dk.mosberg.entomology.component;

import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

/**
 * Interface for specimen components that can be attached to specimens.
 * Components allow extensible data and behavior without modifying core classes.
 *
 * Implementation Guidelines:
 * - Components should be immutable or thread-safe
 * - Serialization must be deterministic for multiplayer sync
 * - Keep component data lightweight for network efficiency
 */
public interface ISpecimenComponent {

  /**
   * Gets the unique identifier for this component type.
   *
   * @return component type identifier
   */
  Identifier getType();

  /**
   * Serializes component data to NBT for persistence.
   *
   * @return NBT compound containing component data
   */
  NbtCompound toNbt();

  /**
   * Deserializes component data from NBT.
   *
   * @param nbt the NBT compound
   */
  void fromNbt(NbtCompound nbt);

  /**
   * Creates a copy of this component.
   * Required for specimen cloning operations.
   *
   * @return deep copy of this component
   */
  ISpecimenComponent copy();

  /**
   * Called when the component is attached to a specimen.
   * Use for initialization logic.
   */
  default void onAttach() {
  }

  /**
   * Called when the component is removed from a specimen.
   * Use for cleanup logic.
   */
  default void onDetach() {
  }

  /**
   * Factory for creating component instances from JSON configuration.
   */
  @FunctionalInterface
  interface Factory {
    /**
     * Creates a new component instance from JSON data.
     *
     * @param config the JSON configuration
     * @return new component instance
     * @throws IllegalArgumentException if config is invalid
     */
    ISpecimenComponent create(JsonObject config);
  }
}
