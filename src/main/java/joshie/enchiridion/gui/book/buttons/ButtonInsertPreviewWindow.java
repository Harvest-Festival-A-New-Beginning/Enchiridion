package joshie.enchiridion.gui.book.buttons;

import joshie.enchiridion.data.book.Page;
import joshie.enchiridion.data.book.FeatureProvider;
import joshie.enchiridion.gui.book.GuiBook;
import joshie.enchiridion.gui.book.element.PreviewWindowElement;

public class ButtonInsertPreviewWindow extends ButtonAbstract {
    public ButtonInsertPreviewWindow() {
        super("preview");
    }

    @Override
    public void performAction(GuiBook guiBook) {
        Page current = guiBook.getPage();
        PreviewWindowElement element = new PreviewWindowElement(0);
        FeatureProvider feature = new FeatureProvider(element, 0, current.getScroll(), 100, 100);
        current.addFeature(feature, 0, current.getScroll(), 100D, 100D, false, false, false);
        feature.init(guiBook);
        guiBook.addRenderableWidget(feature);
        guiBook.getSimpleEditor().setEditor(null);
    }
}