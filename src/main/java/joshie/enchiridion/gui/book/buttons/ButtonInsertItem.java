package joshie.enchiridion.gui.book.buttons;

import joshie.enchiridion.EConfig;
import joshie.enchiridion.api.book.IPage;
import joshie.enchiridion.data.book.FeatureProvider;
import joshie.enchiridion.gui.book.GuiBook;
import joshie.enchiridion.gui.book.element.ItemElement;

public class ButtonInsertItem extends ButtonAbstract {
    public ButtonInsertItem() {
        super("item");
    }

    @Override
    public void performAction(Object gui) {
        GuiBook guiBook = (GuiBook) gui;
        IPage current = guiBook.getPage();
        FeatureProvider feature = new FeatureProvider(new ItemElement(EConfig.getDefaultItem(), 1.0f, false), 0, 0, 16, 16);
        current.addFeature(feature, 0, current.getScroll(), 16D, 16D, false, false, false);
    }
}