package dk.mosberg.entomology.item;

import dk.mosberg.entomology.data.DataDrivenRegistry;
import dk.mosberg.entomology.data.BlockDefinition;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

/**
 * Specimen jar item that stores captured insects.
 */
public class SpecimenJarItem extends BlockItem {
  private static final String SPECIMEN_KEY = "SpecimenId";

  public SpecimenJarItem(Settings settings) {
    super(dk.mosberg.entomology.EntomologyMod.specimenJarBlock, settings);
  }

  public static void setSpecimenId(ItemStack stack, String id) {
    NbtCompound nbt = new NbtCompound();
    nbt.putString(SPECIMEN_KEY, id);
    stack.set(net.minecraft.component.DataComponentTypes.CUSTOM_DATA,
        net.minecraft.component.type.NbtComponent.of(nbt));
  }

  public static String getSpecimenId(ItemStack stack) {
    var nbtComponent = stack.getOrDefault(net.minecraft.component.DataComponentTypes.CUSTOM_DATA,
        net.minecraft.component.type.NbtComponent.DEFAULT);
    NbtCompound nbt = nbtComponent.copyNbt();
    if (nbt.contains(SPECIMEN_KEY)) {
      var value = nbt.getString(SPECIMEN_KEY);
      return value.isPresent() ? value.get() : null;
    }
    return null;
  }

  @Override
  public Text getName(ItemStack stack) {
    String specimenId = getSpecimenId(stack);
    if (specimenId != null) {
      var specimen = DataDrivenRegistry.getSpecimen(specimenId);
      if (specimen != null) {
        return Text.translatable(specimen.displayNameKey());
      }
    }
    return super.getName(stack);
  }

  @Override
  public int getMaxCount() {
    // Empty jars can stack up to 16
    // When a specimen is added via NBT, the stack will auto-limit to 1
    return 16;
  }

  public static int getCapacity() {
    BlockDefinition def = DataDrivenRegistry.getBlock("specimen_jar");
    return def != null ? def.capacity() : 1;
  }
}
