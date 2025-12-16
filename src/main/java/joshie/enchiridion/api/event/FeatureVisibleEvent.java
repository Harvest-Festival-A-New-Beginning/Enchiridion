package joshie.enchiridion.api.event;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class FeatureVisibleEvent extends PlayerEvent {
    public final String bookID;
    public final int page;
    public final int layer;
    public boolean isVisible;

    public FeatureVisibleEvent(Player player, boolean isVisible, String bookID, int page, int layer) {
        super(player);
        this.isVisible = isVisible;
        this.bookID = bookID;
        this.page = page;
        this.layer = layer;
    }
}