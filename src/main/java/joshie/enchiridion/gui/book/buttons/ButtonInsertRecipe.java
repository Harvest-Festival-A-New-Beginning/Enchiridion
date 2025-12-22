package joshie.enchiridion.gui.book.buttons;

import joshie.enchiridion.EConfig;
import joshie.enchiridion.api.book.Page;
import joshie.enchiridion.gui.book.GuiBook;
import joshie.enchiridion.gui.book.features.FeatureRecipe;

public class ButtonInsertRecipe extends ButtonAbstract {
    public ButtonInsertRecipe() {
        super("crafting");
    }

    @Override
    public void performAction(GuiBook guiBook) {
        Page current = guiBook.getPage();
        FeatureRecipe feature = new FeatureRecipe(EConfig.getDefaultItem());
        current.addFeature(feature, 0, current.getScroll(), 160D, 80D, false, false, false);
    }
}