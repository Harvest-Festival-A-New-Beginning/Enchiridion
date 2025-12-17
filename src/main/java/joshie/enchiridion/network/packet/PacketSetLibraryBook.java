package joshie.enchiridion.network.packet;

import joshie.enchiridion.api.EnchiridionAPI;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.common.util.FakePlayer;


import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class PacketSetLibraryBook {
    private ItemStack stack;
    private int slot;

    public PacketSetLibraryBook(@Nonnull ItemStack stack, int slot) {
        this.stack = stack;
        this.slot = slot;
    }

    public static void encode(PacketSetLibraryBook packet, FriendlyByteBuf buf) {
        ItemStack.STREAM_CODEC.encode(buf, packet.stack);
        buf.writeInt(packet.slot);
    }

    public static PacketSetLibraryBook decode(FriendlyByteBuf buf) {
        return new PacketSetLibraryBook(ItemStack.STREAM_CODEC.decode(buf), buf.readInt());
    }

    public static class Handler {
        public static void handle(PacketSetLibraryBook message, Supplier<NetworkEvent.Context> ctx) {
            ServerPlayer playerMP = ctx.get().getSender();
            if (playerMP != null && !(playerMP instanceof FakePlayer)) {
                ctx.get().enqueueWork(() -> EnchiridionAPI.library.getLibraryInventory(playerMP).setItem(message.slot, message.stack));
                ctx.get().setPacketHandled(true);
            }
        }
    }
}