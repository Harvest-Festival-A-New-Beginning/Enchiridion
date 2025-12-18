package joshie.enchiridion.gui.book.features.script;

import joshie.enchiridion.data.book.FeatureProvider;

/**
 * JavaScript wrapper for FeatureProvider to allow scripts to access feature state
 */
public class JSFeature {
    private final FeatureProvider feature;

    public JSFeature(FeatureProvider feature) {
        this.feature = feature;
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
     * Get the raw FeatureProvider object (for advanced usage)
     */
    public FeatureProvider getRaw() {
        return feature;
    }
}
