package dk.mosberg.entomology.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

/**
 * Screen handler for research station block entity.
 * Provides inventory management for specimen jar + field guide inputs.
 */
public class ResearchStationScreenHandler extends ScreenHandler {
  public static final int SLOT_JAR = 0;
  public static final int SLOT_GUIDE = 1;
  public static ScreenHandlerType<ResearchStationScreenHandler> screenHandlerType;

  private final Inventory inventory;

  public ResearchStationScreenHandler(int syncId, PlayerInventory playerInventory) {
    this(syncId, playerInventory, new SimpleInventory(2));
  }

  public ResearchStationScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
    super(screenHandlerType, syncId);
    this.inventory = inventory;
    checkSize(inventory, 2);
    inventory.onOpen(playerInventory.player);

    // Research station slots
    this.addSlot(new Slot(inventory, SLOT_JAR, 56, 35)); // Specimen jar slot
    this.addSlot(new Slot(inventory, SLOT_GUIDE, 102, 35)); // Field guide slot

    // Player inventory
    int playerInvX = 8;
    int playerInvY = 84;

    // Player main inventory
    for (int row = 0; row < 3; ++row) {
      for (int col = 0; col < 9; ++col) {
        this.addSlot(new Slot(playerInventory, col + row * 9 + 9,
            playerInvX + col * 18, playerInvY + row * 18));
      }
    }

    // Player hotbar
    for (int col = 0; col < 9; ++col) {
      this.addSlot(new Slot(playerInventory, col,
          playerInvX + col * 18, playerInvY + 58));
    }
  }

  @Override
  public ItemStack quickMove(PlayerEntity player, int slot) {
    ItemStack newStack = ItemStack.EMPTY;
    Slot clickedSlot = this.slots.get(slot);

    if (clickedSlot.hasStack()) {
      ItemStack originalStack = clickedSlot.getStack();
      newStack = originalStack.copy();

      if (slot < 2) {
        // From research station to player inventory
        if (!this.insertItem(originalStack, 2, this.slots.size(), true)) {
          return ItemStack.EMPTY;
        }
      } else {
        // From player inventory to research station
        if (!this.insertItem(originalStack, 0, 2, false)) {
          return ItemStack.EMPTY;
        }
      }

      if (originalStack.isEmpty()) {
        clickedSlot.setStack(ItemStack.EMPTY);
      } else {
        clickedSlot.markDirty();
      }
    }

    return newStack;
  }

  @Override
  public boolean canUse(PlayerEntity player) {
    return this.inventory.canPlayerUse(player);
  }

  @Override
  public void onClosed(PlayerEntity player) {
    super.onClosed(player);
    this.inventory.onClose(player);
  }

  public Inventory getInventory() {
    return inventory;
  }
}
