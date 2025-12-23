package joshie.enchiridion.helpers;

import joshie.enchiridion.api.book.IBook;
import joshie.enchiridion.data.book.FeatureProvider;
import joshie.enchiridion.data.book.Page;
import joshie.enchiridion.gui.book.GuiSimpleEditorTemplate;
import joshie.enchiridion.gui.book.buttons.actions.ActionNextPage;
import joshie.enchiridion.gui.book.buttons.actions.ActionPreviousPage;
import joshie.enchiridion.gui.book.element.ActionButtonElement;
import joshie.enchiridion.util.ELocation;

import java.util.List;

public class DefaultHelper {
    public static Page addArrows(Page page) {
        ActionButtonElement left = new ActionButtonElement(new ActionPreviousPage());
        left.setResourceLocation(true, new ELocation("arrow_left_on"));
        left.setResourceLocation(false, new ELocation("arrow_left_off"));
        FeatureProvider leftProvider = new FeatureProvider(left, 21, 200, 18, 10);
        page.addFeature(leftProvider, 21, 200, 18, 10, true, false, true);
        ActionButtonElement right = new ActionButtonElement(new ActionNextPage());
        right.setResourceLocation(true, new ELocation("arrow_right_on"));
        right.setResourceLocation(false, new ELocation("arrow_right_off"));
        FeatureProvider rightProvider = new FeatureProvider(right, 387, 200, 18, 10);
        page.addFeature(rightProvider, 387, 200, 18, 10, true, false, true);
        return page;
    }

    public static Page addDefaults(IBook book, Page page) {
        if (book.getDefaultFeatures() != null) {
            for (String unique : book.getDefaultFeatures()) {
                List<FeatureProvider> providers = GuiSimpleEditorTemplate.INSTANCE.getFeaturesFromString(unique);
                for (FeatureProvider provider : providers) {
                    page.addFeature(provider.getFeature(), provider.getLeft(), provider.getTop(), provider.getWidth(), provider.getHeight(), provider.isLocked(), !provider.isVisible(), provider.isFromTemplate());
                }
            }
        } else addArrows(page);

        //Initialise everything
        for (FeatureProvider feature : page.getFeatures()) {
            feature.update(page);
        }

        return page;
    }
}