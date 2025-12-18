package joshie.enchiridion.gui.book.features.script;

import joshie.enchiridion.api.book.IPage;
import joshie.enchiridion.data.book.FeatureProvider;

/**
 * JavaScript wrapper for FeatureJS - exposed to scripts as "feature" global and parameter
 * Provides access to feature position, size, and page information
 */
public class FeatureJSWrapper {
    private final FeatureProvider feature;
    private final IPage page;

    public FeatureJSWrapper(FeatureProvider feature, IPage page) {
        this.feature = feature;
        this.page = page;
    }

    /**
     * Get the feature's left position
     */
    public int getLeft() {
        return feature.getLeft();
    }

    /**
     * Get the feature's top position
     */
    public int getTop() {
        return feature.getTop();
    }

    /**
     * Get the feature's right position
     */
    public int getRight() {
        return feature.getRight();
    }

    /**
     * Get the feature's bottom position
     */
    public int getBottom() {
        return feature.getBottom();
    }

    /**
     * Get the feature's width
     */
    public int getWidth() {
        return feature.getWidth();
    }

    /**
     * Get the feature's height
     */
    public int getHeight() {
        return feature.getHeight();
    }

    /**
     * Check if a position is over this feature
     */
    public boolean isMouseOver(int mouseX, int mouseY) {
        return feature.isOverFeature(mouseX, mouseY);
    }

    /**
     * Get the page number (0-indexed), or -1 if page is null
     */
    public int getPageNumber() {
        return page != null ? page.getPageNumber() : -1;
    }

    /**
     * Get the total number of pages in the book, or 0 if page is null
     */
    public int getTotalPages() {
        return page != null && page.getBook() != null ? page.getBook().getPages().size() : 0;
    }

    /**
     * Check if this is the first page
     */
    public boolean isFirstPage() {
        return getPageNumber() == 0;
    }

    /**
     * Check if this is the last page
     */
    public boolean isLastPage() {
        return getPageNumber() >= getTotalPages() - 1;
    }

    /**
     * Get the raw FeatureProvider object (for advanced usage)
     */
    public FeatureProvider getFeature() {
        return feature;
    }

    /**
     * Get the raw IPage object (for advanced usage), may be null
     */
    public IPage getPage() {
        return page;
    }
}
