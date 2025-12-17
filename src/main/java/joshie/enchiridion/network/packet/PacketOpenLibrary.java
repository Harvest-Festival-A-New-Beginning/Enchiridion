package joshie.enchiridion.network.packet;

import joshie.enchiridion.lib.EGuis;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.common.util.FakePlayer;

public class PacketOpenLibrary {

    public PacketOpenLibrary() {
    }

    public static void encode(PacketOpenLibrary packet, FriendlyByteBuf buf) {
    }

    public static PacketOpenLibrary decode(FriendlyByteBuf buf) {
        return new PacketOpenLibrary();
    }

    public static void handle(PacketOpenLibrary message, ServerPlayer playerMP) {
        if (playerMP != null && !(playerMP instanceof FakePlayer)) {
            playerMP.openMenu(EGuis.getLibraryProvider(), buf -> buf.writeInt(playerMP.getUsedItemHand().ordinal()));
        }
    }
}