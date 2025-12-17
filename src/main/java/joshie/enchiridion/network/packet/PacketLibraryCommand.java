package joshie.enchiridion.network.packet;

import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.library.LibraryHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.common.util.FakePlayer;

import java.util.function.Supplier;

public class PacketLibraryCommand {
    private Action action;

    public PacketLibraryCommand(Action action) {
        this.action = action;
    }

    public static void encode(PacketLibraryCommand packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.action.ordinal());
    }

    public static PacketLibraryCommand decode(FriendlyByteBuf buf) {
        return new PacketLibraryCommand(Action.values()[buf.readInt()]);
    }

    // TODO: NetworkEvent.Context removed in 1.20.4 - need to rewrite for CustomPacketPayload
    /*
    public static class Handler {
        public static void handle(PacketLibraryCommand message, Supplier<NetworkEvent.Context> ctx) {
            ServerPlayer playerMP = ctx.get().getSender();
            if (playerMP != null && !(playerMP instanceof FakePlayer)) {
                ctx.get().enqueueWork(() -> {
                    if (message.action == Action.CLEAR_INVENTORY) EnchiridionAPI.library.getLibraryInventory(playerMP).clear();
                    else if (message.action == Action.RESET_INVENTORY) EnchiridionAPI.library.getLibraryInventory(playerMP).reset();
                });
                ctx.get().setPacketHandled(true);
            }
        }
    }
    */

    public enum Action {
        CLEAR_INVENTORY, RESET_INVENTORY
    }
}
