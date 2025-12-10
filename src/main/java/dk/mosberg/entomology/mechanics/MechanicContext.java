package dk.mosberg.entomology.mechanics;

import dk.mosberg.entomology.api.mechanics.IMechanicContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of IMechanicContext.
 */
public class MechanicContext implements IMechanicContext {
  private final World world;
  private final BlockPos position;
  private final PlayerEntity player;
  private final Entity entity;
  private final Identifier specimenId;
  private final Map<String, Object> data;
  private final ContextType type;
  private final long gameTime;

  private MechanicContext(ContextBuilder builder) {
    this.world = builder.world;
    this.position = builder.position;
    this.player = builder.player;
    this.entity = builder.entity;
    this.specimenId = builder.specimenId;
    this.data = Collections.unmodifiableMap(new HashMap<>(builder.data));
    this.type = builder.type;
    this.gameTime = builder.world != null ? builder.world.getTime() : 0;
  }

  @Override
  public World getWorld() {
    return world;
  }

  @Override
  public Optional<BlockPos> getPosition() {
    return Optional.ofNullable(position);
  }

  @Override
  public Optional<PlayerEntity> getPlayer() {
    return Optional.ofNullable(player);
  }

  @Override
  public Optional<Entity> getEntity() {
    return Optional.ofNullable(entity);
  }

  @Override
  public Optional<Identifier> getSpecimenId() {
    return Optional.ofNullable(specimenId);
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
  public ContextType getType() {
    return type;
  }

  @Override
  public long getGameTime() {
    return gameTime;
  }

  /**
   * Creates a new context builder.
   */
  public static ContextBuilder builder() {
    return new ContextBuilder();
  }

  /**
   * Builder implementation.
   */
  public static class ContextBuilder implements IMechanicContext.Builder {
    private World world;
    private BlockPos position;
    private PlayerEntity player;
    private Entity entity;
    private Identifier specimenId;
    private final Map<String, Object> data = new HashMap<>();
    private ContextType type = ContextType.CUSTOM;

    @Override
    public Builder world(World world) {
      this.world = world;
      return this;
    }

    @Override
    public Builder position(BlockPos pos) {
      this.position = pos;
      return this;
    }

    @Override
    public Builder player(PlayerEntity player) {
      this.player = player;
      return this;
    }

    @Override
    public Builder entity(Entity entity) {
      this.entity = entity;
      return this;
    }

    @Override
    public Builder specimenId(Identifier id) {
      this.specimenId = id;
      return this;
    }

    @Override
    public Builder data(String key, Object value) {
      this.data.put(key, value);
      return this;
    }

    @Override
    public Builder type(ContextType type) {
      this.type = type;
      return this;
    }

    @Override
    public IMechanicContext build() {
      if (world == null) {
        throw new IllegalStateException("World is required for context");
      }
      return new MechanicContext(this);
    }
  }
}
