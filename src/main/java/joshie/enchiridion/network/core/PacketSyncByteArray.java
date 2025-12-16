package joshie.enchiridion.network.core;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.function.Supplier;

import static joshie.enchiridion.network.core.PacketPart.*;

public class PacketSyncByteArray implements IPacketArray {
    protected PacketPart part;
    protected byte[] bites;

    public PacketSyncByteArray() {
    }

    public PacketSyncByteArray(PacketPart part) {
        this.part = part;
    }

    public PacketSyncByteArray(PacketPart part, byte[] bites) {
        this.part = part;
        this.bites = bites;
    }

    protected static void toBytes(PacketSyncByteArray packet, FriendlyByteBuf buf) {
        buf.writeByte(packet.part.ordinal());
        if (packet.part.sends()) {
            if (packet.bites != null && packet.bites.length > 0) {
                buf.writeInt(packet.bites.length);
                for (byte b : packet.bites) {
                    buf.writeByte(b);
                }
            } else buf.writeInt(0);
        }
    }

    protected static void fromBytes(PacketSyncByteArray packet, FriendlyByteBuf buf) {
        packet.part = PacketPart.values()[buf.readByte()];
        if (packet.part.sends()) {
            int length = buf.readInt();
            if (length != 0) {
                packet.bites = new byte[length];
                for (int i = 0; i < length; i++) {
                    packet.bites[i] = buf.readByte();
                }
            }
        }
    }

    public static class Handler {
        public static void handle(PacketSyncByteArray message, Supplier<NetworkEvent.Context> ctx) {
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
}