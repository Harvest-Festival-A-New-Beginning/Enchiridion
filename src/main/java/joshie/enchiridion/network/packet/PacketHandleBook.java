package joshie.enchiridion.network.packet;

import joshie.enchiridion.api.EnchiridionAPI;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.common.util.FakePlayer;

import java.util.function.Supplier;

public class PacketHandleBook {
    private InteractionHand hand;
    private int slot;
    private boolean isShift;

    public PacketHandleBook(InteractionHand hand, int slot, boolean isShift) {
        this.hand = hand;
        this.slot = slot;
        this.isShift = isShift;
    }

    public static void encode(PacketHandleBook packet, FriendlyByteBuf buf) {
        buf.writeBoolean(packet.hand == InteractionHand.MAIN_HAND);
        buf.writeInt(packet.slot);
        buf.writeBoolean(packet.isShift);
    }

    public static PacketHandleBook decode(FriendlyByteBuf buf) {
        return new PacketHandleBook(buf.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, buf.readInt(), buf.readBoolean());
    }

    // TODO: NetworkEvent.Context removed in 1.20.4 - need to rewrite for CustomPacketPayload
    /*
    public static class Handler {
        public static void handle(PacketHandleBook message, Supplier<NetworkEvent.Context> ctx) {
            ServerPlayer playerMP = ctx.get().getSender();
            if (playerMP != null && !(playerMP instanceof FakePlayer)) {
                ctx.get().enqueueWork(() -> EnchiridionAPI.library.handleBook(playerMP, message.hand, message.slot, message.isShift));
                ctx.get().setPacketHandled(true);
            }
        }
    }
    */
}
