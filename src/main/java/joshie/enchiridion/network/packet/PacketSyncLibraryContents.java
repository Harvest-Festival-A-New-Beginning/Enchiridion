package joshie.enchiridion.network.packet;

import joshie.enchiridion.library.LibraryHelper;
import joshie.enchiridion.library.LibraryInventory;
import joshie.enchiridion.network.core.PacketNBT;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.NonNullList;


import java.util.function.Supplier;

public class PacketSyncLibraryContents extends PacketNBT {
    private int currentBook;

    public PacketSyncLibraryContents(int currentBook) {
        this.currentBook = currentBook;
    }

    public PacketSyncLibraryContents(LibraryInventory contents) {
        super(contents.getInventory());
        this.currentBook = contents.getCurrentBook();
    }

    public static void encode(PacketSyncLibraryContents packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.currentBook);
        toBytes(packet, buf);
    }

    public static PacketSyncLibraryContents decode(FriendlyByteBuf buf) {
        PacketSyncLibraryContents packet = new PacketSyncLibraryContents(buf.readInt());
        fromBytes(packet, buf);
        return packet;
    }

    public static class Handler {
        public static void handle(PacketSyncLibraryContents message, Supplier<NetworkEvent.Context> ctx) {
            //Reload the info in from the packet that was sent
            NonNullList<ItemStack> inventory = NonNullList.withSize(LibraryInventory.MAX, ItemStack.EMPTY);
            ListTag tagList = message.nbt.getList("Inventory", 10);
            for (int i = 0; i < tagList.size(); i++) {
                CompoundTag tag = tagList.getCompound(i);
                byte slot = tag.getByte("Slot");
                if (slot > inventory.size() || slot < 0) continue;
                if (tag.getBoolean("NULLItemStack")) {
                    inventory.set(slot, ItemStack.EMPTY);
                } else if (slot >= 0 && slot < inventory.size()) {
                    inventory.set(slot, ItemStack.of(tag));
                }
            }

            //Set the client library
            LibraryHelper.getClientLibraryContents().setCurrentBook(message.currentBook);
            for (int i = 0; i < inventory.size(); i++) {
                LibraryHelper.getClientLibraryContents().setItem(i, inventory.get(i));
            }
            ctx.get().setPacketHandled(true);
        }
    }
}