package joshie.enchiridion.gui.book.buttons;

import joshie.enchiridion.data.book.Page;
import joshie.enchiridion.data.book.FeatureProvider;
import joshie.enchiridion.gui.book.GuiBook;
import joshie.enchiridion.gui.book.buttons.actions.ActionJumpPage;
import joshie.enchiridion.gui.book.element.ActionButtonElement;

public class ButtonInsertButton extends ButtonAbstract {
    public ButtonInsertButton() {
        super("arrow");
    }

    @Override
    public void performAction(GuiBook guiBook) {
        Page current = guiBook.getPage();
        ActionButtonElement element = new ActionButtonElement(new ActionJumpPage().create(guiBook));
        FeatureProvider feature = new FeatureProvider(element, 0, current.getScroll(), 18, 10);
        current.addFeature(feature, 0, current.getScroll(), 18D, 10D, false, false, false);
    }
}