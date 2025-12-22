package joshie.enchiridion.gui.book.element;

import com.mojang.serialization.Codec;
import joshie.enchiridion.api.book.Page;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * Base interface for all feature elements
 * Elements contain ALL feature-specific logic: rendering, interaction, editing
 * FeatureProvider only handles position, size, dragging, resizing - container logic
 *
 * Similar to PenguinLib's Element pattern but with full interaction support
 */
public interface FeatureElement {
    /**
     * Render this element at the specified position and size
     * @param graphics The GuiGraphics to render with
     * @param x The x position to render at (absolute screen coordinates)
     * @param y The y position to render at (absolute screen coordinates)
     * @param width The width to render at
     * @param height The height to render at
     * @param scale The scale factor (default 1.0)
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @param partialTicks Partial tick time
     */
    @OnlyIn(Dist.CLIENT)
    void render(GuiGraphics graphics, int x, int y, int width, int height, float scale, int mouseX, int mouseY, float partialTicks);

    /**
     * Add tooltip text for this element
     * @param tooltip The tooltip list to add to
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     */
    default void addTooltip(java.util.List<String> tooltip, int mouseX, int mouseY) {
        // Default: no tooltip
    }

    /**
     * Called when this element is clicked
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @param button Mouse button (0=left, 1=right, 2=middle)
     * @param gui The GuiBook instance
     * @return true if the click was handled
     */
    default boolean onClick(int mouseX, int mouseY, int button, Object gui) {
        return false;
    }

    /**
     * Called when mouse is released over this element
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @param button Mouse button
     */
    default void onRelease(int mouseX, int mouseY, int button) {
    }

    /**
     * Called when a key is typed while this element is selected
     * @param character The character typed
     * @param key The key code
     * @param gui The GuiBook instance
     * @return true if the key was handled
     */
    default boolean onKeyPress(char character, int key, Object gui) {
        return false;
    }

    /**
     * Called when scrolling over this element
     * @param down True if scrolling down
     * @param amount Amount to scroll
     */
    default void onScroll(boolean down, int amount) {
    }

    /**
     * Enter edit mode for this element
     * @param gui The GuiBook instance
     * @return true if element has special edit mode (yellow corners), false for normal selection (blue corners)
     */
    default boolean enterEditMode(Object gui) {
        return false;
    }

    /**
     * Called when this element is deselected
     */
    default void onDeselected() {
    }

    /**
     * Called when page is changed or element is updated
     * @param page The page containing this element
     */
    default void onUpdate(@Nullable Page page) {
    }

    /**
     * Create a copy of this element
     * @return A new instance with the same data
     */
    FeatureElement copy();

    /**
     * Get display name for this element (for debugging/layers)
     * @return The name
     */
    default String getName() {
        return getClass().getSimpleName();
    }

    /**
     * Get the codec for this element type (for serialization)
     */
    Codec<? extends FeatureElement> codec();
}
