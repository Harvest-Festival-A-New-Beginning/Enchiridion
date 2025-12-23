package joshie.enchiridion.gui.book.buttons;

import joshie.enchiridion.EConfig;
import joshie.enchiridion.data.book.Page;
import joshie.enchiridion.data.book.FeatureProvider;
import joshie.enchiridion.gui.book.GuiBook;
import joshie.enchiridion.gui.book.element.RecipeElement;

public class ButtonInsertRecipe extends ButtonAbstract {
    public ButtonInsertRecipe() {
        super("crafting");
    }

    @Override
    public void performAction(GuiBook guiBook) {
        Page current = guiBook.getPage();
        RecipeElement element = new RecipeElement(EConfig.getDefaultItem());
        FeatureProvider feature = new FeatureProvider(element, 0, current.getScroll(), 160, 80);
        current.addFeature(feature, 0, current.getScroll(), 160D, 80D, false, false, false);
    }
}