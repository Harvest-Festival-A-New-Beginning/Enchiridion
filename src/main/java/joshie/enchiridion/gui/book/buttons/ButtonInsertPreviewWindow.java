package joshie.enchiridion.gui.book.buttons;

import joshie.enchiridion.api.book.IPage;
import joshie.enchiridion.gui.book.GuiBook;
import joshie.enchiridion.gui.book.GuiSimpleEditor;
import joshie.enchiridion.gui.book.features.FeaturePreviewWindow;

public class ButtonInsertPreviewWindow extends ButtonAbstract {
    public ButtonInsertPreviewWindow() {
        super("preview");
    }

    @Override
    public void performAction(Object gui) {
        GuiBook guiBook = (GuiBook) gui;
        IPage current = guiBook.getPage();
        FeaturePreviewWindow feature = new FeaturePreviewWindow(0);
        current.addFeature(feature, 0, current.getScroll(), 100D, 100D, false, false, false);
        guiBook.getSimpleEditor().setEditor(null);
    }
}