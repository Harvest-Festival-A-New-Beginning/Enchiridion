package joshie.enchiridion.network.packet;

import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.library.LibraryInventory;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public class PacketSyncLibraryContents {
    private CompoundTag tag;

    public PacketSyncLibraryContents(LibraryInventory inventory) {
        tag = new CompoundTag();
        inventory.writeToNBT(tag);
    }

    public static void encode(PacketSyncLibraryContents packet, FriendlyByteBuf buf) {
        buf.writeNbt(packet.tag);
    }

    public static PacketSyncLibraryContents decode(FriendlyByteBuf buf) {
        PacketSyncLibraryContents packet = new PacketSyncLibraryContents(new LibraryInventory());
        packet.tag = buf.readNbt();
        return packet;
    }

    private void sync() {
        LibraryInventory library = EnchiridionAPI.library.getClientLibraryContents();
        library.readFromNBT(tag);
    }

    // TODO: NetworkEvent.Context removed in 1.20.4 - need to rewrite for CustomPacketPayload
    /*
    public static class Handler {
        public static void handle(PacketSyncLibraryContents message, Supplier<NetworkEvent.Context> ctx) {
            LocalPlayer player = EnchiridionAPI.player.getPlayer();
            if (player != null) {
                ctx.get().enqueueWork(message::sync);
                ctx.get().setPacketHandled(true);
            }
        }
    }
    */
}
