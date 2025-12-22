package joshie.enchiridion.gui.book;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.GuiGraphics;
import uk.joshiejack.penguinlib.client.gui.book.Book;
import uk.joshiejack.penguinlib.world.inventory.AbstractBookMenu;

import java.util.ArrayList;
import java.util.List;

public class GuiBase extends Book {
    public final List<String> TOOLTIP = new ArrayList<>();

    protected GuiBase(String modid, AbstractBookMenu container, Inventory inventory, Component title) {
            super(modid, container, inventory, title);
            this.imageWidth = 430;
            this.imageHeight = 217;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int x2, int y2, float partialTicks) {
        TOOLTIP.clear();
        super.render(guiGraphics, x2, y2, partialTicks);
    }
}