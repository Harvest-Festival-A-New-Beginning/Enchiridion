package joshie.enchiridion.network.packet;

import joshie.enchiridion.EClientHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public class PacketOpenBook {
    private String uniqueName;

    public PacketOpenBook(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public static void encode(PacketOpenBook packet, FriendlyByteBuf buf) {
        buf.writeUtf(packet.uniqueName);
    }

    public static PacketOpenBook decode(FriendlyByteBuf buf) {
        return new PacketOpenBook(buf.readUtf());
    }

    // TODO: NetworkEvent.Context removed in 1.20.4 - need to rewrite for CustomPacketPayload
    /*
    public static class Handler {
        public static void handle(PacketOpenBook message, Supplier<NetworkEvent.Context> ctx) {
            LocalPlayer player = EClientHandler.getPlayer();
            if (player != null) {
                ctx.get().enqueueWork(() -> EClientHandler.openBookByName(message.uniqueName));
                ctx.get().setPacketHandled(true);
            }
        }
    }
    */
}
