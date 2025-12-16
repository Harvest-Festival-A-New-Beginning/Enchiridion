package joshie.enchiridion.network;

import joshie.enchiridion.lib.EInfo;
import joshie.enchiridion.network.packet.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;

/**
 * TODO: Networking system changed completely in 1.20.4/NeoForge
 *
 * The old SimpleChannel system was replaced with a payload-based system.
 * To fix this properly:
 *
 * 1. Each packet class needs to implement CustomPacketPayload
 * 2. Register using PayloadRegistrar during the RegisterPayloadHandlerEvent
 * 3. Use PlayPayloadContext instead of NetworkEvent.Context
 * 4. Sending packets uses PacketDistributor instead of CHANNEL.sendTo()
 *
 * For now, this is stubbed out to allow compilation.
 * Networking features will not work until this is properly implemented.
 */
public class PacketHandler {
    // TODO: Replace with payload-based registration
    // public static final SimpleChannel CHANNEL = ...

    public static void registerPackets() {
        // TODO: Implement payload-based packet registration
        // Example:
        // registrar.play(PACKET_ID, PacketClass.class, PacketClass::write, PacketClass::new, PacketClass::handle);

        System.err.println("WARNING: Packet registration is stubbed out - networking will not work!");
    }

    public static void sendToClient(Object packet, ServerPlayer playerServer) {
        // TODO: Use PacketDistributor.PLAYER.with(() -> playerServer).send(payload);
        System.err.println("WARNING: sendToClient is stubbed out");
    }

    public static void sendToServer(Object packet) {
        // TODO: Use PacketDistributor.SERVER.noArg().send(payload);
        System.err.println("WARNING: sendToServer is stubbed out");
    }

    public static void sendToEveryone(Object packet, ServerPlayer playerServer) {
        // TODO: Use PacketDistributor.ALL.noArg().send(payload);
        System.err.println("WARNING: sendToEveryone is stubbed out");
    }
}