package test.client;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.test.client.gui.book.JsonBookViewer;
import uk.joshiejack.penguinlib.test.client.gui.book.editor.JsonBookEditor;
import uk.joshiejack.penguinlib.test.item.JsonBookMenu;
import uk.joshiejack.penguinlib.test.item.TestMenus;

/**
 * Client-side event handlers for JSON book system
 */
@Mod.EventBusSubscriber(modid = PenguinLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class TestClientEvents {

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        // Register JsonBookEditor as the screen for JsonBookMenu
        // Uses cached instances to preserve editor state across screen reopens
        event.register(TestMenus.JSON_BOOK.get(),
            (JsonBookMenu menu, Inventory inv, Component text) -> menu.shouldStartInEditMode() ?
                JsonBookEditor.getInstance(menu.getBookId(), inv) :
                JsonBookViewer.getInstance(menu.getBookId(), inv));
    }
}