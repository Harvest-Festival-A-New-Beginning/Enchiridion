package joshie.enchiridion.network.packet;

import joshie.enchiridion.library.ModSupport;
import joshie.enchiridion.network.PacketHandler;
import joshie.enchiridion.network.core.PacketPart;
import joshie.enchiridion.network.core.PacketSyncStringArray;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;

import static joshie.enchiridion.network.core.PacketPart.*;

/**
 * Packet for syncing library allowed books.
 * NOTE: With the move to ReloadableRegistry, ModdedBook entries are now automatically
 * synced via Minecraft's data pack system. This packet is kept for backward compatibility
 * and triggers a registry reload on the client.
 */
public class PacketSyncLibraryAllowed extends PacketSyncStringArray {

    public PacketSyncLibraryAllowed() {
    }

    public PacketSyncLibraryAllowed(PacketPart part) {
        super(part);
    }

    public PacketSyncLibraryAllowed(PacketPart part, String text, int index) {
        super(part, text, index);
    }

    public static void encode(PacketSyncLibraryAllowed packet, FriendlyByteBuf buf) {
        toBytes(packet, buf);
    }

    public static PacketSyncLibraryAllowed decode(FriendlyByteBuf buf) {
        PacketSyncLibraryAllowed libraryAllowed = new PacketSyncLibraryAllowed();
        fromBytes(libraryAllowed, buf);
        return libraryAllowed;
    }

    @Override
    public void receivedHashcode(ServerPlayer player) {
        // Simplified - just reload from registry
        ModSupport.reset();
        ModSupport.loadFromRegistry();
    }

    @Override
    public void receivedLengthRequest(ServerPlayer player) {
        // No longer needed - data packs handle sync
    }

    @Override
    public void receivedStringLength(ServerPlayer player) {
        // No longer needed - data packs handle sync
    }

    @Override
    public void receivedDataRequest(ServerPlayer player) {
        // No longer needed - data packs handle sync
    }

    @Override
    public void receivedData(ServerPlayer player) {
        // Simplified - just reload from registry
        ModSupport.reset();
        ModSupport.loadFromRegistry();
    }
}