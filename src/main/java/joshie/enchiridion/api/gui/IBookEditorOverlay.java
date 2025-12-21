package joshie.enchiridion.api.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;

import java.util.List;

public interface IBookEditorOverlay {
    void draw(GuiGraphics guiGraphics, int mouseX, int mouseY, joshie.enchiridion.gui.book.GuiBook guiBook);
    void addToolTip(List<String> tooltip, int mouseX, int mouseY);
    void charTyped(char character, int key);
    boolean mouseClicked(int mouseX, int mouseY, joshie.enchiridion.gui.book.GuiBook guiBook);
    void mouseReleased(int mouseX, int mouseY, joshie.enchiridion.gui.book.GuiBook guiBook);
    void scroll(boolean down, int mouseX, int mouseY);
    void updateSearch(String string);
    void init();
    void tick();
    GuiEventListener getFocused();
}