package joshie.enchiridion.network.core;

import net.minecraft.server.level.ServerPlayer;

public interface IPacketArray {
    /**
     * Client should do nothing or send a packet to request data
     **/
    default void receivedHashcode(ServerPlayer player) {
    }

    /**
     * Server should now send the length
     **/
    default void receivedLengthRequest(ServerPlayer player) {
    }

    /**
     * Client should now send a received size packet
     **/
    default void receivedStringLength(ServerPlayer player) {
    }

    /**
     * Server should now send the data for the string
     **/
    default void receivedDataRequest(ServerPlayer player) {
    }

    /**
     * Client should try to build a list of the data after received all the data
     **/
    default void receivedData(ServerPlayer player) {
    }
}