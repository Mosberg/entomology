package dk.mosberg.entomology.api;

import dk.mosberg.entomology.component.ISpecimenComponent;
import dk.mosberg.entomology.mechanics.IMechanic;
import dk.mosberg.entomology.schema.ISchemaValidator;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the Entomology API.
 * Singleton instance accessible to all mods.
 */
public final class EntomologyAPI implements IEntomologyAPI {
  private static final EntomologyAPI INSTANCE = new EntomologyAPI();
  private static final String API_VERSION = "1.0.0";

  private final Map<Identifier, IMechanic> mechanics = new ConcurrentHashMap<>();
  private final Map<Identifier, ISpecimenComponent.Factory> componentFactories = new ConcurrentHashMap<>();
  private final Map<String, ISchemaValidator> validators = new ConcurrentHashMap<>();
  private final List<Runnable> reloadListeners = Collections.synchronizedList(new ArrayList<>());

  private EntomologyAPI() {
  }

  public static EntomologyAPI getInstance() {
    return INSTANCE;
  }

  @Override
  public String getApiVersion() {
    return API_VERSION;
  }

  @Override
  public void registerMechanic(IMechanic mechanic) {
    if (mechanic == null) {
      throw new IllegalArgumentException("Mechanic cannot be null");
    }

    Identifier id = mechanic.getId();
    if (mechanics.containsKey(id)) {
      throw new IllegalArgumentException("Mechanic already registered: " + id);
    }

    mechanics.put(id, mechanic);
  }

  @Override
  public boolean unregisterMechanic(Identifier id) {
    return mechanics.remove(id) != null;
  }

  @Override
  public Collection<IMechanic> getMechanics() {
    List<IMechanic> sorted = new ArrayList<>(mechanics.values());
    sorted.sort(Comparator.comparingInt(IMechanic::getPriority).reversed());
    return Collections.unmodifiableList(sorted);
  }

  @Override
  public Optional<IMechanic> getMechanic(Identifier id) {
    return Optional.ofNullable(mechanics.get(id));
  }

  @Override
  public void registerSpecimenComponent(Identifier componentType, ISpecimenComponent.Factory factory) {
    if (componentType == null || factory == null) {
      throw new IllegalArgumentException("Component type and factory cannot be null");
    }

    if (componentFactories.containsKey(componentType)) {
      throw new IllegalArgumentException("Component type already registered: " + componentType);
    }

    componentFactories.put(componentType, factory);
  }

  @Override
  public Optional<ISpecimenComponent.Factory> getSpecimenComponentFactory(Identifier componentType) {
    return Optional.ofNullable(componentFactories.get(componentType));
  }

  @Override
  public void registerSchemaValidator(String schemaPath, ISchemaValidator validator) {
    if (schemaPath == null || validator == null) {
      throw new IllegalArgumentException("Schema path and validator cannot be null");
    }
    validators.put(schemaPath, validator);
  }

  @Override
  public ValidationResult validateSchema(String schemaPath, com.google.gson.JsonObject json) {
    ISchemaValidator validator = validators.get(schemaPath);
    if (validator == null) {
      return new SimpleValidationResult(true, Collections.emptyList(), Collections.emptyList());
    }
    return validator.validate(json);
  }

  @Override
  public CompletableFuture<Void> reloadData() {
    return CompletableFuture.runAsync(() -> {
      for (Runnable listener : reloadListeners) {
        try {
          listener.run();
        } catch (Exception e) {
          // Log but don't propagate
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Adds a reload listener that will be notified when data is reloaded.
   * Internal use only.
   */
  public void addReloadListener(Runnable listener) {
    reloadListeners.add(listener);
  }

  private static class SimpleValidationResult implements ValidationResult {
    private final boolean valid;
    private final List<String> errors;
    private final List<String> warnings;

    SimpleValidationResult(boolean valid, List<String> errors, List<String> warnings) {
      this.valid = valid;
      this.errors = Collections.unmodifiableList(new ArrayList<>(errors));
      this.warnings = Collections.unmodifiableList(new ArrayList<>(warnings));
    }

    @Override
    public boolean isValid() {
      return valid;
    }

    @Override
    public Collection<String> getErrors() {
      return errors;
    }

    @Override
    public Collection<String> getWarnings() {
      return warnings;
    }
  }
}
