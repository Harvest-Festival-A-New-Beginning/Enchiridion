package joshie.enchiridion.library;

import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nullable;
import java.util.Collection;

public class LibraryProxyServer extends LibraryProxy {
    private LibrarySavedData data;

    public LibraryProxyServer(ServerLevel world) {
        data = world.getDataStorage().computeIfAbsent(
            new SavedData.Factory<>(
                LibrarySavedData::new,
                LibrarySavedData::load
            ),
            LibrarySavedData.DATA_NAME
        );
    }

    @Override
    @Nullable
    public LibraryInventory getLibraryInventory(Player player) {
        return data.getLibraryContents(player);
    }

    public Collection<LibraryInventory> getAllInventories() {
        return data.getPlayerData();
    }

    public void setDirty() {
        data.setDirty();
    }
}