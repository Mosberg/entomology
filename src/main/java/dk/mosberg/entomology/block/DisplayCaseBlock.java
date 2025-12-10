package dk.mosberg.entomology.block;

import dk.mosberg.entomology.block.entity.DisplayCaseBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Display case block for showcasing captured specimens.
 * Similar to item frames but specifically for entomology specimens.
 */
public class DisplayCaseBlock extends Block implements BlockEntityProvider {

  public DisplayCaseBlock(Settings settings) {
    super(settings);
  }

  @Override
  @Nullable
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new DisplayCaseBlockEntity(pos, state);
  }

  @Override
  protected ActionResult onUseWithItem(net.minecraft.item.ItemStack stack, BlockState state,
      World world, BlockPos pos, PlayerEntity player,
      net.minecraft.util.Hand hand, BlockHitResult hit) {
    if (world.isClient()) {
      return ActionResult.SUCCESS;
    }

    BlockEntity blockEntity = world.getBlockEntity(pos);
    if (blockEntity instanceof DisplayCaseBlockEntity displayCase) {
      // Handle specimen placement/removal
      if (stack.isEmpty()) {
        // Remove specimen
        displayCase.removeSpecimen();
      } else {
        // Place specimen
        displayCase.setSpecimen(stack.copy());
        if (!player.isCreative()) {
          stack.decrement(1);
        }
      }
      return ActionResult.SUCCESS;
    }

    return ActionResult.PASS;
  }

  @Override
  protected void onStateReplaced(BlockState state, net.minecraft.server.world.ServerWorld world,
      BlockPos pos, boolean moved) {
    BlockEntity blockEntity = world.getBlockEntity(pos);
    if (blockEntity instanceof DisplayCaseBlockEntity displayCase) {
      displayCase.dropContents(world, pos);
    }
    super.onStateReplaced(state, world, pos, moved);
  }
}
