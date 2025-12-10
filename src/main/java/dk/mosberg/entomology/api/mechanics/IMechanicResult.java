package dk.mosberg.entomology.api.mechanics;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Result from mechanic execution.
 * Provides success status, data output, and side effects.
 */
public interface IMechanicResult {
  /**
   * Gets whether the mechanic succeeded.
   *
   * @return true if successful
   */
  boolean isSuccess();

  /**
   * Gets the result type.
   *
   * @return result type
   */
  ResultType getType();

  /**
   * Gets result data.
   *
   * @param key data key
   * @return value if present
   */
  Optional<Object> getData(String key);

  /**
   * Gets all result data.
   *
   * @return immutable map of result data
   */
  Map<String, Object> getAllData();

  /**
   * Gets error message if failed.
   *
   * @return error message
   */
  Optional<String> getErrorMessage();

  /**
   * Gets side effects triggered by this mechanic.
   *
   * @return collection of side effects
   */
  Collection<ISideEffect> getSideEffects();

  /**
   * Gets whether this result should stop further mechanic execution.
   *
   * @return true to stop processing
   */
  boolean shouldStopPropagation();

  /**
   * Result types.
   */
  enum ResultType {
    /** Operation succeeded */
    SUCCESS,
    /** Operation failed */
    FAILURE,
    /** Operation skipped/not applicable */
    SKIPPED,
    /** Operation partially succeeded */
    PARTIAL
  }

  /**
   * Side effect interface.
   */
  interface ISideEffect {
    String getType();

    Map<String, Object> getData();

    void apply(IMechanicContext context);
  }

  /**
   * Builder for creating results.
   */
  interface Builder {
    Builder success();

    Builder failure(String message);

    Builder skip();

    Builder partial();

    Builder data(String key, Object value);

    Builder sideEffect(ISideEffect effect);

    Builder stopPropagation();

    IMechanicResult build();
  }

  /**
   * Creates a success result.
   */
  static IMechanicResult success() {
    return builder().success().build();
  }

  /**
   * Creates a failure result.
   */
  static IMechanicResult failure(String message) {
    return builder().failure(message).build();
  }

  /**
   * Creates a skip result.
   */
  static IMechanicResult skip() {
    return builder().skip().build();
  }

  /**
   * Creates a new builder.
   */
  static Builder builder() {
    return new dk.mosberg.entomology.mechanics.MechanicResult.ResultBuilder();
  }
}
