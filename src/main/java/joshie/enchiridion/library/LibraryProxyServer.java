package joshie.enchiridion.library;

import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;
import java.util.Collection;

public class LibraryProxyServer extends LibraryProxy {
    private LibrarySavedData data;

    public LibraryProxyServer(ServerLevel world) {
        data = world.getDataStorage().computeIfAbsent(
            LibrarySavedData::load,
            LibrarySavedData::new,
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