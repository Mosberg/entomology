package dk.mosberg.entomology.block.entity;

import dk.mosberg.entomology.EntomologyMod;
import dk.mosberg.entomology.data.DataDrivenRegistry;
import dk.mosberg.entomology.data.ResearchEntryDefinition;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

/**
 * Block entity for research station that processes specimens into research
 * entries.
 */
public class ResearchStationBlockEntity extends BlockEntity {
  private final SimpleInventory inventory = new SimpleInventory(2); // 0: jar, 1: guide

  public ResearchStationBlockEntity(BlockPos pos, BlockState state) {
    super(EntomologyMod.researchStationBe, pos, state);
  }

  public SimpleInventory getInventory() {
    return inventory;
  }

  public Optional<ResearchEntryDefinition> performResearch() {
    ItemStack jar = inventory.getStack(0);
    ItemStack guide = inventory.getStack(1);
    String specimenId = dk.mosberg.entomology.item.SpecimenJarItem.getSpecimenId(jar);
    if (specimenId == null || guide.isEmpty()) {
      return Optional.empty();
    }

    for (ResearchEntryDefinition entry : DataDrivenRegistry.getResearchEntries()) {
      if (entry.specimenId().equals(specimenId)) {
        inventory.setStack(0, ItemStack.EMPTY); // consume jar
        // guide is not consumed; you can change that if desired
        markDirty();
        return Optional.of(entry);
      }
    }

    return Optional.empty();
  }

  @Override
  protected void readData(ReadView view) {
    super.readData(view);
    // Load inventory slots from NBT
    for (int i = 0; i < inventory.size(); i++) {
      String slotKey = "Slot" + i;
      if (view.contains(slotKey)) {
        // Note: ItemStack deserialization requires registry wrapper in 1.21+
        // For now, store as string or wait for proper API
        // This is a simplified approach - full implementation needs RegistryWrapper
        // TODO: Implement full deserialization when RegistryWrapper is available
        continue; // Placeholder until proper deserialization is implemented
      }
    }
  }

  @Override
  protected void writeData(WriteView view) {
    super.writeData(view);
    // Save inventory slots to NBT
    for (int i = 0; i < inventory.size(); i++) {
      ItemStack stack = inventory.getStack(i);
      if (!stack.isEmpty()) {
        // Note: ItemStack serialization requires registry wrapper in 1.21+
        // For now this persists basic data - full implementation needs RegistryWrapper
        view.putString("Slot" + i + "_Item", stack.getItem().toString());
        view.putInt("Slot" + i + "_Count", stack.getCount());
      }
    }
  }

  @Override
  public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
    return createNbt(registries);
  }
}
