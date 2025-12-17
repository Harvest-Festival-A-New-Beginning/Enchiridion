package joshie.enchiridion.library;

import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.Collection;

public class LibraryHelper {
    @OnlyIn(Dist.CLIENT)
    private static LibraryProxy theClient;
    private static LibraryProxyServer theServer;

    public static void resetServer(ServerLevel world) {
        if (world != null) {
            theServer = (new LibraryProxyServer(world));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void resetClient() {
        theClient = new LibraryProxyClient();
    }

    private static LibraryProxy getHandler() {
        return isServer() ? theServer : theClient;
    }

    private static boolean isServer() {
        // On the server, theClient will be null (due to @OnlyIn(Dist.CLIENT))
        // On the client in single-player, both may exist, so check environment
        return FMLEnvironment.dist.isDedicatedServer() || theClient == null;
    }

    public static LibraryInventory getLibraryContents(Player player) {
        return getHandler().getLibraryInventory(player);
    }

    @OnlyIn(Dist.CLIENT)
    public static LibraryInventory getClientLibraryContents() {
        return theClient.getLibraryInventory(null);
    }

    public static LibraryInventory getServerLibraryContents(Player player) {
        return theServer.getLibraryInventory(player);
    }

    public static Collection<LibraryInventory> getAllInventories() {
        return theServer.getAllInventories();
    }

    public static void setDirty() {
        theServer.setDirty();
    }

    public static void markDirty() {
        if (theServer != null) {
            theServer.setDirty();
        }
    }

}