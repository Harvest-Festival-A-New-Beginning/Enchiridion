package joshie.enchiridion.library;

import joshie.enchiridion.helpers.MCClientHelper;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LibraryProxyClient extends LibraryProxy {
    private LibraryInventory contents;

    public LibraryProxyClient() {
        contents = new LibraryInventory(MCClientHelper.getPlayer());
    }

    @Override
    public LibraryInventory getLibraryInventory(Player player) {
        return contents;
    }
}