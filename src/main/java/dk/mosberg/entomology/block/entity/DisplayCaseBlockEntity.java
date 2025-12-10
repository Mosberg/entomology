package dk.mosberg.entomology.block.entity;

import dk.mosberg.entomology.EntomologyMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Block entity for display case that showcases a specimen.
 */
public class DisplayCaseBlockEntity extends BlockEntity {
  private ItemStack displayedItem = ItemStack.EMPTY;

  public DisplayCaseBlockEntity(BlockPos pos, BlockState state) {
    super(EntomologyMod.displayCaseBe, pos, state);
  }

  public void setSpecimen(ItemStack stack) {
    this.displayedItem = stack.copy();
    markDirty();
  }

  public ItemStack getSpecimen() {
    return displayedItem;
  }

  public void removeSpecimen() {
    this.displayedItem = ItemStack.EMPTY;
    markDirty();
  }

  public void dropContents(World world, BlockPos pos) {
    if (!displayedItem.isEmpty()) {
      ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), displayedItem);
    }
  }

  @Override
  protected void readData(ReadView view) {
    super.readData(view);
    String itemId = view.getString("DisplayedItemId", null);
    int count = view.getInt("DisplayedItemCount", 0);
    if (itemId != null && count > 0) {
      // Simplified loading - just store empty for now
      this.displayedItem = ItemStack.EMPTY;
    }
  }

  @Override
  protected void writeData(WriteView view) {
    super.writeData(view);
    if (!displayedItem.isEmpty()) {
      view.putString("DisplayedItemId", displayedItem.getItem().toString());
      view.putInt("DisplayedItemCount", displayedItem.getCount());
    }
  }

  @Override
  public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
    return createNbt(registries);
  }
}
