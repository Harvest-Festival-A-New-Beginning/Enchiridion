package joshie.enchiridion.network.packet;

import joshie.enchiridion.api.EnchiridionAPI;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class PacketSetLibraryBook {
    private ItemStack stack;
    private int slot;

    public PacketSetLibraryBook(@Nonnull ItemStack stack, int slot) {
        this.stack = stack;
        this.slot = slot;
    }

    public static void encode(PacketSetLibraryBook packet, PacketBuffer buf) {
        buf.writeItemStack(packet.stack);
        buf.writeInt(packet.slot);
    }

    public static PacketSetLibraryBook decode(PacketBuffer buf) {
        return new PacketSetLibraryBook(buf.readItemStack(), buf.readInt());
    }

    public static class Handler {
        public static void handle(PacketSetLibraryBook message, Supplier<NetworkEvent.Context> ctx) {
            ServerPlayer playerMP = ctx.get().getSender();
            if (playerMP != null && !(playerMP instanceof FakePlayer)) {
                ctx.get().enqueueWork(() -> EnchiridionAPI.library.getLibraryInventory(playerMP).setInventorySlotContents(message.slot, message.stack));
                ctx.get().setPacketHandled(true);
            }
        }
    }
}