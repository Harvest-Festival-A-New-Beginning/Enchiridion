package joshie.enchiridion.library.handlers;

import joshie.enchiridion.EClientHandler;
import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.api.book.IBookHandler;
import joshie.enchiridion.network.PacketHandler;
import joshie.enchiridion.network.packet.PacketSetLibraryBook;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.InteractionHand;

import javax.annotation.Nonnull;

public class WritableBookHandler implements IBookHandler {
    @Override
    public String getName() {
        return "writeable";
    }

    @Override
    public void handle(@Nonnull ItemStack stack, Player player, InteractionHand hand, int slotID, boolean isShiftPressed) {
        if (player.level().isClientSide) {
            EClientHandler.openWriteableBook(player, slotID, hand);
        }
    }

    //Our own version for the writeable so that we send packets to the library instead of the hand
    public static class GuiScreenWritable extends BookEditScreen {
        private int slot;

        public GuiScreenWritable(ServerPlayer player, int slot, InteractionHand hand) {
            super(player, EnchiridionAPI.library.getLibraryInventory(player).getStackInSlot(slot), hand);
            this.slot = slot;
        }

        //Overwrite mc behaviour and send a custom packet instead
        @Override
        public void saveChanges(boolean publish) {
            if (this.isModified) {
                this.updateLocalCopy();
                ListTag nbtList = new ListTag();
                this.pages.stream().map(StringTag::valueOf).forEach(nbtList::add);
                if (!this.pages.isEmpty()) {
                    this.book.addTagElement("pages", nbtList);
                }

                if (publish) {
                    this.book.addTagElement("author", StringTag.valueOf(this.owner.getGameProfile().getName()));
                    this.book.addTagElement("title", StringTag.valueOf(this.title.trim()));
                }

                //Set the book in the library
                EnchiridionAPI.library.getLibraryInventory(this.owner).setInventorySlotContents(slot, this.book);
                PacketHandler.sendToServer(new PacketSetLibraryBook(this.book, slot));
            }
        }
    }
}