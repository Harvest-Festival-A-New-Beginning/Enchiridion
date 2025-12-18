package joshie.enchiridion.gui.book.features.script;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * JavaScript wrapper for GuiGraphics - exposed to scripts as "graphics" parameter
 * Provides safe drawing methods with automatic position offsetting
 */
public class GraphicsJS {
    private final GuiGraphics guiGraphics;
    private final int baseX;
    private final int baseY;
    private final int width;
    private final int height;

    public GraphicsJS(GuiGraphics guiGraphics, int baseX, int baseY, int width, int height) {
        this.guiGraphics = guiGraphics;
        this.baseX = baseX;
        this.baseY = baseY;
        this.width = width;
        this.height = height;
    }

    /**
     * Draw a filled rectangle (relative to feature position)
     */
    public void fillRect(int x, int y, int width, int height, int color) {
        guiGraphics.fill(baseX + x, baseY + y, baseX + x + width, baseY + y + height, color);
    }

    /**
     * Draw text (relative to feature position)
     */
    public void drawText(String text, int x, int y, int color) {
        Font font = Minecraft.getInstance().font;
        guiGraphics.drawString(font, text, baseX + x, baseY + y, color);
    }

    /**
     * Draw centered text (relative to feature position)
     */
    public void drawCenteredText(String text, int x, int y, int color) {
        Font font = Minecraft.getInstance().font;
        guiGraphics.drawCenteredString(font, Component.literal(text), baseX + x, baseY + y, color);
    }

    /**
     * Draw word-wrapped text (relative to feature position)
     */
    public void drawWordWrap(String text, int x, int y, int maxWidth, int color) {
        Font font = Minecraft.getInstance().font;
        guiGraphics.drawWordWrap(font, Component.literal(text), baseX + x, baseY + y, maxWidth, color);
    }

    /**
     * Draw an image/texture (relative to feature position)
     */
    public void drawImage(String resourceLocation, int x, int y, int width, int height) {
        try {
            ResourceLocation texture = new ResourceLocation(resourceLocation);
            guiGraphics.blit(texture, baseX + x, baseY + y, 0, 0, width, height, width, height);
        } catch (Exception e) {
            // Invalid resource location - fail silently
        }
    }

    /**
     * Draw a textured rectangle with UV coordinates (relative to feature position)
     */
    public void drawTexturedRect(String resourceLocation, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
        try {
            ResourceLocation texture = new ResourceLocation(resourceLocation);
            guiGraphics.blit(texture, baseX + x, baseY + y, u, v, width, height, textureWidth, textureHeight);
        } catch (Exception e) {
            // Invalid resource location - fail silently
        }
    }

    /**
     * Get the base X offset
     */
    public int getBaseX() {
        return baseX;
    }

    /**
     * Get the base Y offset
     */
    public int getBaseY() {
        return baseY;
    }

    /**
     * Get the feature width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the feature height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Get the raw GuiGraphics object (for advanced usage)
     * WARNING: Using this directly bypasses position offsetting
     */
    public GuiGraphics getRaw() {
        return guiGraphics;
    }
}
