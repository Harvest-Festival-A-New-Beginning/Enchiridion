package joshie.enchiridion.network.packet;

import joshie.enchiridion.library.LibraryHelper;
import joshie.enchiridion.library.LibraryInventory;
import joshie.enchiridion.library.ModSupport;
import joshie.enchiridion.network.PacketHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.common.util.FakePlayer;


import java.util.function.Supplier;

import static joshie.enchiridion.network.core.PacketPart.REQUEST_SIZE;

public class PacketLibraryCommand {
    private String command;

    public PacketLibraryCommand(String string) {
        command = string;
    }

    public static void encode(PacketLibraryCommand packet, FriendlyByteBuf buf) {
        buf.writeUtf(packet.command);
    }

    public static PacketLibraryCommand decode(FriendlyByteBuf buf) {
        return new PacketLibraryCommand(buf.readUtf(32767));
    }

    public static class Handler {
        public static void handle(PacketLibraryCommand message, Supplier<NetworkEvent.Context> ctx) {
            ServerPlayer playerMP = ctx.get().getSender();
            if (playerMP != null && !(playerMP instanceof FakePlayer)) {
                //Refresh command resets the modded books allowed
                //Reset command resets whether players have received books or not
                //Clear command clears the inventory of all the players libraries
                switch (message.command) {
                    case "refresh":
                        if (!playerMP.level().isClientSide) {
                            ModSupport.reset(); //Reset the modded data, then tell clients to reset it and request data
                            PacketHandler.sendToEveryone(new PacketLibraryCommand("refresh"), playerMP);
                        } else {
                            ModSupport.reset(); //Reset the modded data, then request the info from the server
                            PacketHandler.sendToServer(new PacketSyncLibraryAllowed(REQUEST_SIZE));
                        }
                        break;
                    case "reset":
                        if (!playerMP.level().isClientSide) {
                            LibraryHelper.getAllInventories().forEach(LibraryInventory::reset);
                        }
                        break;
                    case "clear":
                        if (!playerMP.level().isClientSide) {
                            LibraryHelper.getAllInventories().forEach(LibraryInventory::clear);
                        }
                        break;
                }
                ctx.get().setPacketHandled(true);
            }
        }
    }
}