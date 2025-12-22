package joshie.enchiridion.gui.book.buttons;

import joshie.enchiridion.Enchiridion;
import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.gui.book.GuiBook;
import joshie.enchiridion.lib.EInfo;
import net.minecraft.resources.ResourceLocation;

public class ButtonToggleScrollable extends ButtonAbstract {
    private ResourceLocation selected_dflt;
    private ResourceLocation selected_hover;
    private String translate_selected;

    public ButtonToggleScrollable() {
        super("scroll");
        dflt = new ResourceLocation(EInfo.TEXPATH + name + "_unselected_dftl.png");
        hover = new ResourceLocation(EInfo.TEXPATH + name + "_unselected_hover.png");
        selected_dflt = new ResourceLocation(EInfo.TEXPATH + name + "_selected_dftl.png");
        selected_hover = new ResourceLocation(EInfo.TEXPATH + name + "_selected_hover.png");
        translate_selected = "button." + name + ".selected";
    }

    @Override
    public boolean isLeftAligned() {
        return false;
    }

    @Override
    public ResourceLocation getResource(Object gui) {
        
        return guiBook.getPage().isScrollingEnabled() ? selected_dflt : dflt;
    }

    @Override
    public ResourceLocation getHoverResource(Object gui) {
        
        return guiBook.getPage().isScrollingEnabled() ? selected_hover : hover;
    }

    @Override
    public String getTooltipText(Object gui) {
        
        return guiBook.getPage().isScrollingEnabled() ? Enchiridion.format(translate_selected) : Enchiridion.format(translate);
    }

    @Override
    public void performAction(GuiBook guiBook) {
        
        guiBook.getPage().toggleScroll();
    }
}