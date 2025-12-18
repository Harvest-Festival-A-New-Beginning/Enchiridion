package joshie.enchiridion.gui.book.buttons;

import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.gui.book.GuiBook;

public class ButtonToggleGrid extends ButtonAbstract {
    public ButtonToggleGrid() {
        super("grid");
    }

    @Override
    public boolean isLeftAligned() {
        return false;
    }

    @Override
    public void performAction() {
        ((GuiBook) EnchiridionAPI.book).getGrid().toggle();
    }
}