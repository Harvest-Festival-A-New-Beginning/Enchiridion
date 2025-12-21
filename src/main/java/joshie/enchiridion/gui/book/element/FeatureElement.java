package joshie.enchiridion.gui.book.element;

import com.mojang.serialization.Codec;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Base interface for all feature rendering elements
 * Elements are pure rendering logic - positioning/sizing is handled by FeatureProvider
 *
 * Similar to PenguinLib's RenderElement pattern
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
     * Get the codec for this element type (for serialization)
     */
    Codec<? extends FeatureElement> codec();
}
