package joshie.enchiridion.library;

import joshie.enchiridion.EClientHandler;
import joshie.enchiridion.EConfig;
import joshie.enchiridion.Enchiridion;
import joshie.enchiridion.helpers.MCServerHelper;
import joshie.enchiridion.helpers.SyncHelper;
import joshie.enchiridion.lib.EInfo;
import joshie.enchiridion.network.PacketHandler;
import joshie.enchiridion.network.core.PacketPart;
import joshie.enchiridion.network.packet.PacketOpenLibrary;
import joshie.enchiridion.network.packet.PacketSyncLibraryAllowed;
import joshie.enchiridion.network.packet.PacketSyncLibraryContents;
import joshie.enchiridion.network.packet.PacketSyncMD5;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.apache.logging.log4j.Level;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = EInfo.MODID)
public class LibraryEvents {
    //Setup the Client
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onOpenGui(ScreenEvent.Opening event) {
        if (event.getNewScreen() instanceof SelectWorldScreen || event.getNewScreen() instanceof JoinMultiplayerScreen) {
            LibraryHelper.resetClient();
        }
    }

    //Sync the library
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) { //Sync what's in the library
            ServerPlayer mp = (ServerPlayer) player;
            if (!SyncHelper.playersSynced.contains(mp)) {
                if (EConfig.SETTINGS.debugMode) Enchiridion.log(Level.INFO, "Did you call me?");
                //Sync what's allowed in the library (triggers registry reload on client)
                PacketHandler.sendToClient(new PacketSyncLibraryAllowed(PacketPart.SEND_HASH), mp);

                //Sync what is in the library
                LibraryInventory inventory = LibraryHelper.getServerLibraryContents(player);
                inventory.addDefaultBooks();
                PacketHandler.sendToClient(new PacketSyncLibraryContents(inventory), mp);

                //Start the md5 process
                if (EConfig.SETTINGS.syncDataAndImagesToClients.get()) {
                    PacketHandler.sendToClient(new PacketSyncMD5(PacketPart.SEND_SIZE, "", SyncHelper.servermd5.length), mp);
                }
                SyncHelper.playersSynced.add(mp);
            }
        }
    }

    //Opening the key binding
    @SubscribeEvent
    public static void onKeyPress(InputEvent.Key event) {
        if (EClientHandler.libraryKeyBinding == null) return; //If the keybinding was never created, skip this
        long handle = Minecraft.getInstance().getWindow().getWindow();
        if (EClientHandler.libraryKeyBinding.isDown() && Minecraft.getInstance().isWindowActive() && !InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_F3)) {
            PacketHandler.sendToServer(new PacketOpenLibrary());
        }
    }
}