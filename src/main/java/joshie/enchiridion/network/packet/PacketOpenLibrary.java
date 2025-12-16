package joshie.enchiridion.network.packet;

import joshie.enchiridion.lib.EGuis;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.PacketBuffer;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.fml.network.NetworkEvent;
import net.neoforged.fml.network.NetworkHooks;

import java.util.function.Supplier;

public class PacketOpenLibrary {

    public PacketOpenLibrary() {
    }

    public static void encode(PacketOpenLibrary packet, PacketBuffer buf) {
    }

    public static PacketOpenLibrary decode(PacketBuffer buf) {
        return new PacketOpenLibrary();
    }

    public static class Handler {
        public static void handle(PacketOpenLibrary message, Supplier<NetworkEvent.Context> ctx) {
            ServerPlayer playerMP = ctx.get().getSender();
            if (playerMP != null && !(playerMP instanceof FakePlayer)) {
                ctx.get().enqueueWork(() -> NetworkHooks.openScreen(playerMP, EGuis.getLibraryProvider(playerMP.getActiveHand()), buf -> buf.writeInt(playerMP.getActiveHand().ordinal())));
                ctx.get().setPacketHandled(true);
            }
        }
    }
}