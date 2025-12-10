package dk.mosberg.entomology.block;

import com.mojang.serialization.MapCodec;
import dk.mosberg.entomology.block.entity.ResearchStationBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Research station block for identifying specimens.
 */
public class ResearchStationBlock extends Block implements BlockEntityProvider {
  public static final MapCodec<ResearchStationBlock> CODEC = createCodec(ResearchStationBlock::new);

  public ResearchStationBlock(Settings settings) {
    super(settings);
  }

  @Override
  protected MapCodec<? extends Block> getCodec() {
    return CODEC;
  }

  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new ResearchStationBlockEntity(pos, state);
  }

  @Override
  protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player,
      BlockHitResult hit) {
    if (world.isClient()) {
      return ActionResult.SUCCESS;
    }

    BlockEntity blockEntity = world.getBlockEntity(pos);
    if (blockEntity instanceof ResearchStationBlockEntity researchStation) {
      player.openHandledScreen(createScreenHandlerFactory(researchStation));
    }
    return ActionResult.SUCCESS;
  }

  private NamedScreenHandlerFactory createScreenHandlerFactory(ResearchStationBlockEntity entity) {
    return new SimpleNamedScreenHandlerFactory(
        (syncId, playerInventory, player) -> new dk.mosberg.entomology.screen.ResearchStationScreenHandler(syncId,
            playerInventory, entity.getInventory()),
        Text.translatable("container.entomology.research_station"));
  }
}
