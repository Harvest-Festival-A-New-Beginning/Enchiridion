package joshie.enchiridion.gui.book.buttons;

import joshie.enchiridion.EConfig;
import joshie.enchiridion.api.book.IPage;
import joshie.enchiridion.gui.book.GuiBook;
import joshie.enchiridion.gui.book.features.FeatureItem;

public class ButtonInsertItem extends ButtonAbstract {
    public ButtonInsertItem() {
        super("item");
    }

    @Override
    public void performAction(Object gui) {
        GuiBook guiBook = (GuiBook) gui;
        IPage current = guiBook.getPage();
        FeatureItem feature = new FeatureItem(EConfig.getDefaultItem());
        current.addFeature(feature, 0, current.getScroll(), 16D, 16D, false, false, false);
    }
}