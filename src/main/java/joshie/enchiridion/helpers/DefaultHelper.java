package joshie.enchiridion.helpers;

import joshie.enchiridion.api.book.IBook;
import joshie.enchiridion.data.book.FeatureProvider;
import joshie.enchiridion.api.book.Page;
import joshie.enchiridion.gui.book.GuiSimpleEditorTemplate;
import joshie.enchiridion.gui.book.buttons.actions.ActionNextPage;
import joshie.enchiridion.gui.book.buttons.actions.ActionPreviousPage;
import joshie.enchiridion.gui.book.features.FeatureButton;
import joshie.enchiridion.util.ELocation;

import java.util.List;

public class DefaultHelper {
    public static Page addArrows(Page page) {
        FeatureButton left = new FeatureButton(new ActionPreviousPage());
        left.setResourceLocation(true, new ELocation("arrow_left_on")).setResourceLocation(false, new ELocation("arrow_left_off"));
        page.addFeature(left, 21, 200, 18, 10, true, false, true);
        FeatureButton right = new FeatureButton(new ActionNextPage());
        right.setResourceLocation(true, new ELocation("arrow_right_on")).setResourceLocation(false, new ELocation("arrow_right_off"));
        page.addFeature(right, 387, 200, 18, 10, true, false, true);
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