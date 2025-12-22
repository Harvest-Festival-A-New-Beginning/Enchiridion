package joshie.enchiridion.gui.book.buttons;

import joshie.enchiridion.EConfig;
import joshie.enchiridion.data.book.FeatureProvider;
import joshie.enchiridion.data.book.Page;
import joshie.enchiridion.gui.book.GuiBook;
import joshie.enchiridion.gui.book.element.TextElement;

public class ButtonInsertText extends ButtonAbstract {
    public ButtonInsertText() {
        super("text");
    }

    @Override
    public void performAction(GuiBook guiBook) {
        Page current = guiBook.getPage();
        TextElement element = new TextElement(EConfig.SETTINGS.defaultText.get(), 1F, 0x555555);
        FeatureProvider feature = new FeatureProvider(element, 0, current.getScroll(), 200, 80);
        current.addFeature(feature, 0, current.getScroll(), 200D, 80D, false, false, false);
    }
}