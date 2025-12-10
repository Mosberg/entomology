package dk.mosberg.entomology.registry.advanced;

import dk.mosberg.entomology.EntomologyMod;
import dk.mosberg.entomology.api.core.IDataProvider;
import dk.mosberg.entomology.api.core.ILifecycleAware;
import dk.mosberg.entomology.api.mechanics.IAdvancedMechanic;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Advanced component registry with dependency injection and lifecycle
 * management.
 * Thread-safe and supports dynamic registration/unregistration.
 */
public class ComponentRegistry {
  private static ComponentRegistry instance;

  private final Map<Identifier, ComponentEntry<?>> components = new ConcurrentHashMap<>();
  private final Map<Class<?>, List<Identifier>> typeIndex = new ConcurrentHashMap<>();
  private final Map<Identifier, Set<Identifier>> dependencies = new ConcurrentHashMap<>();
  private final Set<Identifier> initializedComponents = ConcurrentHashMap.newKeySet();

  private ComponentRegistry() {
  }

  public static synchronized ComponentRegistry getInstance() {
    if (instance == null) {
      instance = new ComponentRegistry();
    }
    return instance;
  }

  /**
   * Registers a component with dependencies.
   */
  public <T> void register(Identifier id, Class<T> type, Supplier<T> factory,
      Identifier... deps) {
    if (components.containsKey(id)) {
      throw new IllegalArgumentException("Component already registered: " + id);
    }

    ComponentEntry<T> entry = new ComponentEntry<>(id, type, factory,
        deps.length > 0 ? Set.of(deps) : Collections.emptySet());
    components.put(id, entry);

    typeIndex.computeIfAbsent(type, k -> new ArrayList<>()).add(id);
    if (deps.length > 0) {
      dependencies.put(id, Set.of(deps));
    }

    EntomologyMod.LOGGER.debug("Registered component: {} ({})", id, type.getSimpleName());
  }

  /**
   * Registers a singleton component instance.
   */
  @SuppressWarnings("unchecked")
  public <T> void registerSingleton(Identifier id, T instance, Identifier... deps) {
    register(id, (Class<T>) instance.getClass(), () -> instance, deps);
    // Pre-instantiate
    get(id, instance.getClass());
  }

  /**
   * Gets a component by ID and type.
   */
  public <T> Optional<T> get(Identifier id, Class<T> type) {
    ComponentEntry<?> entry = components.get(id);
    if (entry == null || !type.isAssignableFrom(entry.type)) {
      return Optional.empty();
    }

    return Optional.of(type.cast(entry.getInstance()));
  }

  /**
   * Gets all components of a specific type.
   */
  public <T> List<T> getAll(Class<T> type) {
    List<Identifier> ids = typeIndex.get(type);
    if (ids == null) {
      return Collections.emptyList();
    }

    return ids.stream()
        .map(id -> get(id, type))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
  }

  /**
   * Gets all mechanics, sorted by priority.
   */
  public List<IAdvancedMechanic> getAllMechanics() {
    return getAll(IAdvancedMechanic.class).stream()
        .sorted(Comparator.comparingInt(IAdvancedMechanic::getPriority).reversed())
        .collect(Collectors.toList());
  }

  /**
   * Gets all data providers.
   */
  @SuppressWarnings("unchecked")
  public List<IDataProvider<?>> getAllDataProviders() {
    return (List<IDataProvider<?>>) (List<?>) getAll(IDataProvider.class);
  }

  /**
   * Initializes all components in dependency order.
   */
  public void initializeAll() {
    EntomologyMod.LOGGER.info("Initializing all components...");

    List<Identifier> order = resolveDependencyOrder();
    int initialized = 0;

    for (Identifier id : order) {
      if (initializeComponent(id)) {
        initialized++;
      }
    }

    EntomologyMod.LOGGER.info("Initialized {} components", initialized);
  }

  /**
   * Initializes a specific component.
   */
  public boolean initializeComponent(Identifier id) {
    if (initializedComponents.contains(id)) {
      return false;
    }

    ComponentEntry<?> entry = components.get(id);
    if (entry == null) {
      return false;
    }

    // Initialize dependencies first
    for (Identifier dep : entry.dependencies) {
      if (!initializedComponents.contains(dep)) {
        initializeComponent(dep);
      }
    }

    // Get instance (triggers factory)
    Object instance = entry.getInstance();

    // Initialize if lifecycle aware
    if (instance instanceof ILifecycleAware aware) {
      try {
        aware.onInitialize();
        aware.onEnable();
        EntomologyMod.LOGGER.debug("Initialized lifecycle component: {}", id);
      } catch (Exception e) {
        EntomologyMod.LOGGER.error("Failed to initialize component: {}", id, e);
        return false;
      }
    }

    initializedComponents.add(id);
    return true;
  }

  /**
   * Shuts down all components.
   */
  public void shutdownAll() {
    EntomologyMod.LOGGER.info("Shutting down all components...");

    // Shutdown in reverse order
    List<Identifier> order = resolveDependencyOrder();
    Collections.reverse(order);

    for (Identifier id : order) {
      shutdownComponent(id);
    }

    components.clear();
    typeIndex.clear();
    dependencies.clear();
    initializedComponents.clear();
  }

  /**
   * Shuts down a specific component.
   */
  public void shutdownComponent(Identifier id) {
    ComponentEntry<?> entry = components.get(id);
    if (entry == null || !entry.isInstantiated()) {
      return;
    }

    Object instance = entry.getInstance();
    if (instance instanceof ILifecycleAware aware) {
      try {
        aware.onDisable();
        aware.onShutdown();
        EntomologyMod.LOGGER.debug("Shutdown component: {}", id);
      } catch (Exception e) {
        EntomologyMod.LOGGER.error("Failed to shutdown component: {}", id, e);
      }
    }

    initializedComponents.remove(id);
  }

  /**
   * Resolves dependency order using topological sort.
   */
  private List<Identifier> resolveDependencyOrder() {
    List<Identifier> result = new ArrayList<>();
    Set<Identifier> visited = new HashSet<>();
    Set<Identifier> temp = new HashSet<>();

    for (Identifier id : components.keySet()) {
      if (!visited.contains(id)) {
        if (!topologicalSort(id, visited, temp, result)) {
          throw new IllegalStateException("Circular dependency detected involving: " + id);
        }
      }
    }

    return result;
  }

  /**
   * Topological sort helper.
   */
  private boolean topologicalSort(Identifier id, Set<Identifier> visited,
      Set<Identifier> temp, List<Identifier> result) {
    if (temp.contains(id)) {
      return false; // Circular dependency
    }
    if (visited.contains(id)) {
      return true;
    }

    temp.add(id);

    Set<Identifier> deps = dependencies.getOrDefault(id, Collections.emptySet());
    for (Identifier dep : deps) {
      if (!topologicalSort(dep, visited, temp, result)) {
        return false;
      }
    }

    temp.remove(id);
    visited.add(id);
    result.add(id);

    return true;
  }

  /**
   * Component entry with lazy instantiation.
   */
  private static class ComponentEntry<T> {
    final Identifier id;
    final Class<T> type;
    final Supplier<T> factory;
    final Set<Identifier> dependencies;
    private volatile T instance;

    ComponentEntry(Identifier id, Class<T> type, Supplier<T> factory, Set<Identifier> dependencies) {
      this.id = id;
      this.type = type;
      this.factory = factory;
      this.dependencies = dependencies;
    }

    T getInstance() {
      if (instance == null) {
        synchronized (this) {
          if (instance == null) {
            instance = factory.get();
            if (instance == null) {
              throw new IllegalStateException("Factory returned null for: " + id);
            }
          }
        }
      }
      return instance;
    }

    boolean isInstantiated() {
      return instance != null;
    }
  }

  /**
   * Clears the registry (for testing).
   */
  public void clear() {
    shutdownAll();
  }
}
