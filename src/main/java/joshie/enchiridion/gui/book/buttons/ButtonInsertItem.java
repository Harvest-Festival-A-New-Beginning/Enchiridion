package joshie.enchiridion.gui.book.buttons;

import joshie.enchiridion.EConfig;
import joshie.enchiridion.data.book.Page;
import joshie.enchiridion.data.book.FeatureProvider;
import joshie.enchiridion.gui.book.GuiBook;
import joshie.enchiridion.gui.book.element.ItemElement;
import joshie.enchiridion.helpers.StackHelper;

public class ButtonInsertItem extends ButtonAbstract {
    public ButtonInsertItem() {
        super("item");
    }

    @Override
    public void performAction(GuiBook guiBook) {
        
        Page current = guiBook.getPage();
        String itemString = StackHelper.getStringFromStack(EConfig.getDefaultItem());
        FeatureProvider feature = new FeatureProvider(new ItemElement(itemString), 0, 0, 16, 16);
        current.addFeature(feature, 0, current.getScroll(), 16D, 16D, false, false, false);
        feature.init(guiBook);
        guiBook.addRenderableWidget(feature);
    }
}