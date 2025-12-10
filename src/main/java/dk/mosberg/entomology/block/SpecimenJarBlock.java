package dk.mosberg.entomology.block;

import com.mojang.serialization.MapCodec;
import dk.mosberg.entomology.block.entity.SpecimenJarBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Specimen jar block that displays captured insects.
 */
public class SpecimenJarBlock extends Block implements BlockEntityProvider {
  public static final MapCodec<SpecimenJarBlock> CODEC = createCodec(SpecimenJarBlock::new);

  public SpecimenJarBlock(Settings settings) {
    super(settings);
  }

  @Override
  protected MapCodec<? extends Block> getCodec() {
    return CODEC;
  }

  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new SpecimenJarBlockEntity(pos, state);
  }
}
