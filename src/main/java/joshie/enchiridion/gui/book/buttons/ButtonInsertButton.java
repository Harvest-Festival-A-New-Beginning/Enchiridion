package joshie.enchiridion.gui.book.buttons;

import joshie.enchiridion.api.book.IPage;
import joshie.enchiridion.gui.book.GuiBook;
import joshie.enchiridion.gui.book.buttons.actions.ActionJumpPage;
import joshie.enchiridion.gui.book.features.FeatureButton;

public class ButtonInsertButton extends ButtonAbstract {
    public ButtonInsertButton() {
        super("arrow");
    }

    @Override
    public void performAction(Object gui) {
        GuiBook guiBook = (GuiBook) gui;
        IPage current = guiBook.getPage();
        FeatureButton feature = new FeatureButton(new ActionJumpPage().create(guiBook));
        current.addFeature(feature, 0, current.getScroll(), 18D, 10D, false, false, false);
    }
}