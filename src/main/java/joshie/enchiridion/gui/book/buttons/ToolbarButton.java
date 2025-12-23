package joshie.enchiridion.gui.book.buttons;

import joshie.enchiridion.Enchiridion;
import joshie.enchiridion.api.gui.IToolbarButton;
import joshie.enchiridion.data.book.FeatureProvider;
import joshie.enchiridion.data.book.Page;
import joshie.enchiridion.gui.book.GuiBook;
import joshie.enchiridion.gui.book.element.FeatureElement;
import joshie.enchiridion.gui.book.element.ToolbarMetadata;
import net.minecraft.resources.ResourceLocation;

/**
 * Generic toolbar button that creates elements from ToolbarMetadata
 */
public class ToolbarButton extends IToolbarButton {
    private final ToolbarMetadata metadata;

    public ToolbarButton(ToolbarMetadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public ResourceLocation getResource(GuiBook gui) {
        return metadata.icon();
    }

    @Override
    public ResourceLocation getHoverResource(GuiBook gui) {
        return metadata.icon(); // Same icon for now, could add hover variant to metadata
    }

    @Override
    public void performAction(GuiBook guiBook) {
        Page current = guiBook.getPage();
        FeatureElement element = metadata.createDefault();
        FeatureProvider feature = new FeatureProvider(element, 0, current.getScroll(), metadata.defaultWidth(), metadata.defaultHeight());
        current.addFeature(feature, 0, current.getScroll(), (double) metadata.defaultWidth(), (double) metadata.defaultHeight(), false, false, false);
        feature.init(guiBook);
        guiBook.addRenderableWidget(feature);
    }

    @Override
    public String getTooltipText(GuiBook gui) {
        // Extract name from icon path (e.g., "enchiridion:textures/gui/toolbar/text.png" -> "text")
        String path = metadata.icon().getPath();
        String name = path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf('.'));
        return Enchiridion.format("button." + name);
    }

    @Override
    public boolean isLeftAligned() {
        return true; // All element insert buttons are left-aligned
    }
}
