package joshie.enchiridion.library;

import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public abstract class LibraryProxy {
    public abstract LibraryInventory getLibraryInventory(@Nullable Player player);
}