package dk.mosberg.entomology.api.core;

/**
 * Interface for components that need lifecycle management.
 * Provides hooks for initialization, cleanup, and state management.
 */
public interface ILifecycleAware {
  /**
   * Called during initialization phase.
   * Use for setting up resources, registering listeners, etc.
   */
  void onInitialize();

  /**
   * Called when the component should be enabled/activated.
   * Use for starting background tasks, enabling features, etc.
   */
  default void onEnable() {
  }

  /**
   * Called when the component should be disabled/deactivated.
   * Use for pausing features, stopping tasks, etc.
   */
  default void onDisable() {
  }

  /**
   * Called during cleanup/shutdown phase.
   * Use for releasing resources, saving state, etc.
   */
  void onShutdown();

  /**
   * Gets the current lifecycle state.
   *
   * @return current state
   */
  LifecycleState getState();

  /**
   * Lifecycle states.
   */
  enum LifecycleState {
    /** Not yet initialized */
    UNINITIALIZED,
    /** Initialized but not enabled */
    INITIALIZED,
    /** Enabled and active */
    ENABLED,
    /** Disabled but can be re-enabled */
    DISABLED,
    /** Shut down and cannot be reused */
    SHUTDOWN
  }
}
