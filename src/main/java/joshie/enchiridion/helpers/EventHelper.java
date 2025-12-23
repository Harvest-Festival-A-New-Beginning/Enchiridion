package joshie.enchiridion.helpers;

import joshie.enchiridion.data.book.Page;
import joshie.enchiridion.api.event.FeatureVisibleEvent;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;

public class EventHelper {
    public static boolean isFeatureVisible(Page ipage, boolean isVisible, int layer) {
        if (ipage.getBook() == null) return isVisible;
        Player player = MCClientHelper.getPlayer();
        String bookID = ipage.getBook().getUniqueName();
        int page = ipage.getPageNumber();
        FeatureVisibleEvent event = new FeatureVisibleEvent(player, isVisible, bookID, page, layer);
        NeoForge.EVENT_BUS.post(event);
        return event.isVisible;
    }
}