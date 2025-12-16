package joshie.enchiridion.gui.library;

import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.api.book.IBookHandler;
import joshie.enchiridion.helpers.MCClientHelper;
import joshie.enchiridion.items.EItems;
import joshie.enchiridion.library.LibraryHelper;
import joshie.enchiridion.network.PacketHandler;
import joshie.enchiridion.network.packet.PacketHandleBook;
import net.minecraft.world.entity.player.Player;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionHand;

import javax.annotation.Nonnull;

public class SlotBook extends Slot {
    private static final ItemStack DUMMY = new ItemStack(Items.BOOK);
    private InteractionHand hand;

    public SlotBook(IInventory inventory, InteractionHand hand, int index, int xPosition, int yPosition) {
        super(inventory, index, xPosition, yPosition);
        this.hand = hand;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        //FORBID LIBRARIES
        return stack.getItem() != EItems.LIBRARY && EnchiridionAPI.library.getBookHandlerForStack(stack) != null;
    }

    @Nonnull
    public ItemStack handle(Player player, int mouseButton, Slot slot) {
        ItemStack stack = slot.getStack();
        IBookHandler handler = EnchiridionAPI.library.getBookHandlerForStack(stack);
        if (handler != null) {
            if (player.level().isClientSide) {
                boolean isShiftPressed = MCClientHelper.isShiftPressed();
                PacketHandler.sendToServer(new PacketHandleBook(slot.slotNumber, hand, isShiftPressed));
                handler.handle(stack, player, hand, slot.slotNumber, isShiftPressed);
                System.out.println("Potato");
                LibraryHelper.getClientLibraryContents().setCurrentBook(slot.slotNumber);
            }
            System.out.println("Empty");

            return ItemStack.EMPTY;
        }
        return DUMMY;
    }
}