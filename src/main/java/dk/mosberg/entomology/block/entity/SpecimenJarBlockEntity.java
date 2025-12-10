package dk.mosberg.entomology.block.entity;

import dk.mosberg.entomology.EntomologyMod;
import dk.mosberg.entomology.data.DataDrivenRegistry;
import dk.mosberg.entomology.data.SpecimenDefinition;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

/**
 * Block entity for specimen jar that stores captured insect data.
 */
public class SpecimenJarBlockEntity extends BlockEntity {
  private String specimenId;

  public SpecimenJarBlockEntity(BlockPos pos, BlockState state) {
    super(EntomologyMod.specimenJarBe, pos, state);
  }

  public void setSpecimenId(String id) {
    this.specimenId = id;
    markDirty();
  }

  public String getSpecimenId() {
    return specimenId;
  }

  public SpecimenDefinition getSpecimen() {
    return specimenId != null ? DataDrivenRegistry.getSpecimen(specimenId) : null;
  }

  @Override
  protected void readData(ReadView view) {
    super.readData(view);
    this.specimenId = view.getString("SpecimenId", null);
  }

  @Override
  protected void writeData(WriteView view) {
    super.writeData(view);
    if (specimenId != null) {
      view.putString("SpecimenId", specimenId);
    }
  }

  @Override
  public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
    return createNbt(registries);
  }
}
