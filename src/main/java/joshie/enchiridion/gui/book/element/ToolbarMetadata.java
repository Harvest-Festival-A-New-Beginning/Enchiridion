package joshie.enchiridion.gui.book.element;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * Metadata for elements that should appear in the editor toolbar
 */
public record ToolbarMetadata(
    /**
     * Sort order in the toolbar (lower = earlier in toolbar)
     * 0-99: Simple elements (text, box, image, etc.)
     * 100-199: Complex elements (button, recipe, etc.)
     * 200+: Special elements (preview window, etc.)
     */
    int sortOrder,

    /**
     * Icon to display in the toolbar button
     */
    ResourceLocation icon,

    /**
     * Factory method to create a default instance for insertion
     */
    Supplier<FeatureElement> defaultFactory,

    /**
     * Default width when inserting
     */
    int defaultWidth,

    /**
     * Default height when inserting
     */
    int defaultHeight
) {
    public FeatureElement createDefault() {
        return defaultFactory.get();
    }
}
