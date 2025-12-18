package joshie.enchiridion.gui.book;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import uk.joshiejack.penguinlib.world.inventory.AbstractBookMenu;

import javax.annotation.Nonnull;

public class BookMenu extends AbstractBookMenu {
    public BookMenu(MenuType<?> type, int windowId) {
        super(type, windowId);
    }
}
