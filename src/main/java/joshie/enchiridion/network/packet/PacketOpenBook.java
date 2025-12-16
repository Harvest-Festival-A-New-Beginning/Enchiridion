package joshie.enchiridion.network.packet;

import joshie.enchiridion.api.EnchiridionAPI;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.PacketBuffer;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketOpenBook{
    private String bookID;
    private int page;

    public PacketOpenBook(String bookID, int page) {
        this.bookID = bookID;
        this.page = page;
    }

    public static void encode(PacketOpenBook packet, PacketBuffer buf) {
        buf.writeString(packet.bookID);
        buf.writeInt(packet.page);
    }

    public static PacketOpenBook decode(PacketBuffer buf) {
        return new PacketOpenBook(buf.readString(32767), buf.readInt());
    }

    public static class Handler {
        public static void handle(PacketOpenBook message, Supplier<NetworkEvent.Context> ctx) {
            ServerPlayer playerMP = ctx.get().getSender();
            if (playerMP != null && !(playerMP instanceof FakePlayer)) {
                ctx.get().enqueueWork(() -> EnchiridionAPI.instance.openBook(playerMP, message.bookID, message.page));
                ctx.get().setPacketHandled(true);
            }
        }
    }
}