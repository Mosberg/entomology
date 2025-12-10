package dk.mosberg.entomology.mechanics;

import dk.mosberg.entomology.api.mechanics.IMechanicContext;
import dk.mosberg.entomology.api.mechanics.IMechanicResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of IMechanicResult.
 */
public class MechanicResult implements IMechanicResult {
  private final boolean success;
  private final ResultType type;
  private final Map<String, Object> data;
  private final String errorMessage;
  private final Collection<ISideEffect> sideEffects;
  private final boolean stopPropagation;

  private MechanicResult(ResultBuilder builder) {
    this.success = builder.success;
    this.type = builder.type;
    this.data = Collections.unmodifiableMap(new HashMap<>(builder.data));
    this.errorMessage = builder.errorMessage;
    this.sideEffects = Collections.unmodifiableList(new ArrayList<>(builder.sideEffects));
    this.stopPropagation = builder.stopPropagation;
  }

  @Override
  public boolean isSuccess() {
    return success;
  }

  @Override
  public ResultType getType() {
    return type;
  }

  @Override
  public Optional<Object> getData(String key) {
    return Optional.ofNullable(data.get(key));
  }

  @Override
  public Map<String, Object> getAllData() {
    return data;
  }

  @Override
  public Optional<String> getErrorMessage() {
    return Optional.ofNullable(errorMessage);
  }

  @Override
  public Collection<ISideEffect> getSideEffects() {
    return sideEffects;
  }

  @Override
  public boolean shouldStopPropagation() {
    return stopPropagation;
  }

  /**
   * Builder implementation.
   */
  public static class ResultBuilder implements IMechanicResult.Builder {
    private boolean success = false;
    private ResultType type = ResultType.SKIPPED;
    private final Map<String, Object> data = new HashMap<>();
    private String errorMessage = null;
    private final List<ISideEffect> sideEffects = new ArrayList<>();
    private boolean stopPropagation = false;

    @Override
    public Builder success() {
      this.success = true;
      this.type = ResultType.SUCCESS;
      return this;
    }

    @Override
    public Builder failure(String message) {
      this.success = false;
      this.type = ResultType.FAILURE;
      this.errorMessage = message;
      return this;
    }

    @Override
    public Builder skip() {
      this.success = true;
      this.type = ResultType.SKIPPED;
      return this;
    }

    @Override
    public Builder partial() {
      this.success = true;
      this.type = ResultType.PARTIAL;
      return this;
    }

    @Override
    public Builder data(String key, Object value) {
      this.data.put(key, value);
      return this;
    }

    @Override
    public Builder sideEffect(ISideEffect effect) {
      this.sideEffects.add(effect);
      return this;
    }

    @Override
    public Builder stopPropagation() {
      this.stopPropagation = true;
      return this;
    }

    @Override
    public IMechanicResult build() {
      return new MechanicResult(this);
    }
  }

  /**
   * Simple side effect implementation.
   */
  public static class SimpleSideEffect implements ISideEffect {
    private final String type;
    private final Map<String, Object> data;

    public SimpleSideEffect(String type, Map<String, Object> data) {
      this.type = type;
      this.data = Collections.unmodifiableMap(new HashMap<>(data));
    }

    @Override
    public String getType() {
      return type;
    }

    @Override
    public Map<String, Object> getData() {
      return data;
    }

    @Override
    public void apply(IMechanicContext context) {
      // Default implementation - override for specific side effects
    }
  }
}
