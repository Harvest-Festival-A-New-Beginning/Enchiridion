package joshie.enchiridion.gui.book.buttons;

import joshie.enchiridion.api.book.Page;
import joshie.enchiridion.data.book.FeatureProvider;
import joshie.enchiridion.gui.book.GuiBook;
import joshie.enchiridion.gui.book.element.BoxElement;

public class ButtonInsertBox extends ButtonAbstract {
    public ButtonInsertBox() {
        super("box");
    }

    @Override
    public void performAction(GuiBook guiBook) {
        
        Page current = guiBook.getPage();
        int color = (int) Long.parseLong("ff000000", 16);
        FeatureProvider feature = new FeatureProvider(new BoxElement(color), 0, 0, 50, 5);
        current.addFeature(feature, 0, current.getScroll(), 50D, 5D, false, false, false);
    }
}