package joshie.enchiridion.gui.library;

import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.lib.EGuis;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;

import javax.annotation.Nonnull;

public class ContainerLibrary extends AbstractContainerMenu {
    public Container library;

    public ContainerLibrary(int windowID, Inventory playerInventory, Container library, InteractionHand hand) {
        super(EGuis.LIBRARY_CONTAINER, windowID);
        this.library = library;

        //Left hand side slots
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 3; j++) {
                addSlot(new SlotBook(library, hand, j + (i * 3), -51 + (j * 18), 22 + (i * 23)));
            }
        }

        //Right hand side slots
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 3; j++) {
                addSlot(new SlotBook(library, hand, 15 + j + (i * 3), 175 + (j * 18), 22 + (i * 23)));
            }
        }

        //Centre Slots
        for (int i = 0; i < 5; i++) {
            int y = i == 0 ? 1 : 0;
            for (int j = 0; j < 7; j++) {
                addSlot(new SlotBook(library, hand, 30 + j + (i * 7), 26 + (j * 18), -1 + (i * 23) - y));
            }
        }

        // addSlotToContainer(new SlotBook(library, 0, 8, 15)); //Add one book slot
        bindPlayerInventory(playerInventory, 30);
    }

    protected void bindPlayerInventory(Inventory inventory, int yOffset) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + yOffset));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(inventory, i, 8 + i * 18, 142 + yOffset));
        }
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return true;
    }

    @Override
    @Nonnull
    public ItemStack quickMoveStack(Player player, int slotID) {
        int size = library.getContainerSize();
        int low = size + 27;
        int high = low + 9;
        ItemStack returnStack = ItemStack.EMPTY;
        Slot slot = slots.get(slotID);

        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            returnStack = stack.copy();

            if (slotID < size) {
                if (!moveItemStackTo(stack, size, high, true)) return ItemStack.EMPTY;
                slot.setChanged();
            } else if (slotID >= size) {
                if (EnchiridionAPI.library.getBookHandlerForStack(stack) != null) {
                    if (!moveItemStackTo(stack, 0, 65, false)) return ItemStack.EMPTY; //Slots 0-64 for Books
                } else if (slotID >= size && slotID < low) {
                    if (!moveItemStackTo(stack, low, high, false)) return ItemStack.EMPTY;
                } else if (slotID >= low && slotID < high && !moveItemStackTo(stack, high, low, false))
                    return ItemStack.EMPTY;
            } else if (!moveItemStackTo(stack, size, high, false)) return ItemStack.EMPTY;

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == returnStack.getCount()) return ItemStack.EMPTY;

            slot.onTake(player, stack);
        }
        return returnStack;
    }

    @Override
    @Nonnull
    public void clicked(int slotID, int mouseButton, ClickType type, Player player) {
        Slot slot = slotID < 0 || slotID > slots.size() ? null : slots.get(slotID);
        if (!(mouseButton == 1 && slot instanceof SlotBook && ((SlotBook) slot).handle(player, mouseButton, slot).isEmpty())) {
            super.clicked(slotID, mouseButton, type, player);
        }
    }
}