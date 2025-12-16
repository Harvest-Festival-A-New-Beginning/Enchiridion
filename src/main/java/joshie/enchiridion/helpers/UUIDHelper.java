package joshie.enchiridion.helpers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.server.ServerLifecycleHooks;

import java.util.UUID;

public class UUIDHelper {
    public static UUID getPlayerUUID(Player player) {
        return Player.getUUID(player.getGameProfile());
    }

    /**
     * Gets the player from the uuid
     **/
    public static ServerPlayer getPlayerFromUUID(UUID uuid) {
        //Loops through every single player
        for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            if (getPlayerUUID(player).equals(uuid)) {
                return player;
            }
        }
        return null;
    }
}