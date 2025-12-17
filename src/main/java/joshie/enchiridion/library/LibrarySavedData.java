package joshie.enchiridion.library;

import joshie.enchiridion.helpers.UUIDHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class LibrarySavedData extends SavedData {
    public static final String DATA_NAME = "Enchiridion-Library";
    private HashMap<UUID, LibraryInventory> players = new HashMap<>();

    public LibrarySavedData() {
        super();
    }

    public Collection<LibraryInventory> getPlayerData() {
        return players.values();
    }

    public LibraryInventory getLibraryContents(Player player) {
        UUID uuid = UUIDHelper.getPlayerUUID(player);
        if (players.containsKey(uuid)) {
            return players.get(uuid);
        } else {
            if (players.containsKey(uuid)) {
                return players.get(uuid);
            } else {
                LibraryInventory data = new LibraryInventory(player);
                players.put(uuid, data);

                setDirty();
                return players.get(uuid);
            }
        }
    }

    /**
     * CAN AND WILL RETURN NULL, IF THE UUID COULD NOT BE FOUND
     **/
    public LibraryInventory getLibraryContents(UUID uuid) {
        if (players.containsKey(uuid)) {
            return players.get(uuid);
        } else {
            ServerPlayer player = UUIDHelper.getPlayerFromUUID(uuid);
            if (player == null) return null;
            else return getLibraryContents(player);
        }
    }

    public static LibrarySavedData load(CompoundTag nbt) {
        LibrarySavedData data = new LibrarySavedData();
        ListTag tag_list_players = nbt.getList("LibraryInventory", 10);
        for (int i = 0; i < tag_list_players.size(); i++) {
            CompoundTag tag = tag_list_players.getCompound(i);
            LibraryInventory inventory = new LibraryInventory();
            boolean success;
            try {
                inventory.readFromNBT(tag);
                success = true;
            } catch (Exception e) {
                success = false;
            }
            //Only add non failed loads
            if (success) {
                data.players.put(inventory.getUUID(), inventory);
            }
        }
        return data;
    }

    @Override
    @Nonnull
    public CompoundTag save(@Nonnull CompoundTag nbt) {
        ListTag tag_list_players = new ListTag();
        players.entrySet().stream().filter(entry -> entry.getKey() != null && entry.getValue() != null).forEach(entry -> {
            CompoundTag tag = new CompoundTag();
            entry.getValue().writeToNBT(tag);
            tag_list_players.add(tag);
        });
        nbt.put("LibraryInventory", tag_list_players);
        return nbt;
    }
}