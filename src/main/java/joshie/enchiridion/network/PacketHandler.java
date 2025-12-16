package joshie.enchiridion.network;

import joshie.enchiridion.lib.EInfo;
import joshie.enchiridion.network.packet.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.NetworkDirection;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.simple.SimpleChannel;

public class PacketHandler {
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(EInfo.MODID, "channel"))
            .clientAcceptedVersions(v -> true)
            .serverAcceptedVersions(v -> true)
            .networkProtocolVersion(() -> "ENCHIRIDION1")
            .simpleChannel();

    public static void registerPackets() {
        CHANNEL.registerMessage(0, PacketSyncLibraryAllowed.class, PacketSyncLibraryAllowed::encode, PacketSyncLibraryAllowed::decode, PacketSyncLibraryAllowed.Handler::handle);
        CHANNEL.registerMessage(1, PacketLibraryCommand.class, PacketLibraryCommand::encode, PacketLibraryCommand::decode, PacketLibraryCommand.Handler::handle);
        CHANNEL.registerMessage(2, PacketSyncMD5.class, PacketSyncMD5::encode, PacketSyncMD5::decode, PacketSyncMD5.Handler::handle);
        CHANNEL.registerMessage(3, PacketSyncFile.class, PacketSyncFile::encode, PacketSyncFile::decode, PacketSyncFile.Handler::handle);
        CHANNEL.registerMessage(4, PacketOpenBook.class, PacketOpenBook::encode, PacketOpenBook::decode, PacketOpenBook.Handler::handle);
        CHANNEL.registerMessage(5, PacketSyncLibraryContents.class, PacketSyncLibraryContents::encode, PacketSyncLibraryContents::decode, PacketSyncLibraryContents.Handler::handle);
        CHANNEL.registerMessage(6, PacketOpenLibrary.class, PacketOpenLibrary::encode, PacketOpenLibrary::decode, PacketOpenLibrary.Handler::handle);
        CHANNEL.registerMessage(7, PacketHandleBook.class, PacketHandleBook::encode, PacketHandleBook::decode, PacketHandleBook.Handler::handle);
        CHANNEL.registerMessage(8, PacketSetLibraryBook.class, PacketSetLibraryBook::encode, PacketSetLibraryBook::decode, PacketSetLibraryBook.Handler::handle);
    }

    public static void sendToClient(Object packet, ServerPlayer playerServer) {
        CHANNEL.sendTo(packet, playerServer.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToServer(Object packet) {
        CHANNEL.sendToServer(packet);
    }

    public static void sendToEveryone(Object packet, ServerPlayer playerServer) {
        for (Player player : playerServer.level().players()) {
            if (player instanceof ServerPlayer) {
                CHANNEL.sendTo(packet, ((ServerPlayer) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
            }
        }
    }
}