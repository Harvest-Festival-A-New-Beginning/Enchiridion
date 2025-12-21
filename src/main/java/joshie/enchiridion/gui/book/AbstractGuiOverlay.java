package joshie.enchiridion.gui.book;

import joshie.enchiridion.util.ELocation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public abstract class AbstractGuiOverlay {
    protected static final ResourceLocation TOOLBAR = new ELocation("toolbar");
    protected static final ResourceLocation SIDEBAR = new ELocation("sidebar");

    public abstract void draw(GuiGraphics graphics, int mouseX, int mouseY, GuiBook guiBook);

    public void addToolTip(List<String> tooltip, int mouseX, int mouseY) {
    }

    public void charTyped(char character, int key) {
    }

    public boolean mouseClicked(int mouseX, int mouseY, GuiBook guiBook) {
        return false;
    }

    public void mouseReleased(int mouseX, int mouseY, GuiBook guiBook) {
    }

    public void scroll(boolean down, int mouseX, int mouseY) {
    }

    public void updateSearch(String search) {
    }

    public void init() {
    }

    public void tick() {
    }

    public GuiEventListener getFocused() {
        return null;
    }
}