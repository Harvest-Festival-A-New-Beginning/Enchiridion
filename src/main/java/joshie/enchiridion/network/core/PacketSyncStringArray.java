package joshie.enchiridion.network.core;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.common.util.FakePlayer;

import java.util.function.Supplier;

import static joshie.enchiridion.network.core.PacketPart.*;

public class PacketSyncStringArray extends PacketSyncByteArray {
    public PacketSyncStringArray() {
    }

    public PacketSyncStringArray(PacketPart part) {
        super(part);
    }

    public PacketSyncStringArray(PacketPart part, String[] strings) {
        super(part, getStringBytesFromArray(strings));
    }

    public static void encode(PacketSyncStringArray packet, FriendlyByteBuf buf) {
        toBytes(packet, buf);
    }

    public static PacketSyncStringArray decode(FriendlyByteBuf buf) {
        PacketSyncStringArray packet = new PacketSyncStringArray();
        fromBytes(packet, buf);
        return packet;
    }

    private static byte[] getStringBytesFromArray(String[] array) {
        String combined = String.join("\n", array);
        return combined.getBytes();
    }

    private String[] getStringArrayFromBytes() {
        return new String(bites).split("\n");
    }

    // TODO: NetworkEvent.Context removed in 1.20.4 - need to rewrite for CustomPacketPayload
    /*
    public static class Handler {
        public static void handle(PacketSyncStringArray message, Supplier<NetworkEvent.Context> ctx) {
            ServerPlayer playerMP = ctx.get().getSender();
            if (playerMP != null && !(playerMP instanceof FakePlayer)) {
                if (message.part == SEND_HASH) message.receivedHashcode(playerMP);
                else if (message.part == REQUEST_SIZE) message.receivedLengthRequest(playerMP);
                else if (message.part == SEND_SIZE) message.receivedStringLength(playerMP);
                else if (message.part == REQUEST_DATA) message.receivedDataRequest(playerMP);
                else if (message.part == SEND_DATA) message.receivedData(playerMP);
                ctx.get().setPacketHandled(true);
            }
        }
    }
    */
}
