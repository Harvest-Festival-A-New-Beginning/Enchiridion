package joshie.enchiridion.network.packet;

import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.api.book.IBookHandler;
import joshie.enchiridion.library.LibraryHelper;
import joshie.enchiridion.library.LibraryInventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketHandleBook {
    private int slot;
    private boolean isShiftPressed;
    private InteractionHand hand;

    public PacketHandleBook(int slot, InteractionHand hand, boolean isShiftPressed) {
        this.slot = slot;
        this.hand = hand;
        this.isShiftPressed = isShiftPressed;
    }

    public static void encode(PacketHandleBook packet, PacketBuffer buf) {
        buf.writeInt(packet.slot);
        buf.writeInt(packet.hand.ordinal());
        buf.writeBoolean(packet.isShiftPressed);
    }

    public static PacketHandleBook decode(PacketBuffer buf) {
        return new PacketHandleBook(buf.readInt(), InteractionHand.values()[buf.readInt()], buf.readBoolean());
    }

    public static class Handler {
        public static void handle(PacketHandleBook message, Supplier<NetworkEvent.Context> ctx) {
            ServerPlayer playerMP = ctx.get().getSender();
            if (playerMP != null && !(playerMP instanceof FakePlayer)) {
                ItemStack stack = EnchiridionAPI.library.getLibraryInventory(playerMP).getStackInSlot(message.slot);
                if (!stack.isEmpty()) {
                    IBookHandler handler = EnchiridionAPI.library.getBookHandlerForStack(stack);
                    if (handler != null) {
                        handler.handle(stack, playerMP, message.hand, message.slot, message.isShiftPressed);
                    }
                    LibraryInventory libraryServer = LibraryHelper.getServerLibraryContents(playerMP);
                    libraryServer.setCurrentBook(message.slot);
                    ctx.get().setPacketHandled(true);
                }
            }
        }
    }
}