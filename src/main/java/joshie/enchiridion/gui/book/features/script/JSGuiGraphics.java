package joshie.enchiridion.gui.book.features.script;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * JavaScript wrapper for GuiGraphics to provide safe drawing methods
 */
public class JSGuiGraphics {
    private final GuiGraphics guiGraphics;
    private final int baseX;
    private final int baseY;

    public JSGuiGraphics(GuiGraphics guiGraphics, int baseX, int baseY) {
        this.guiGraphics = guiGraphics;
        this.baseX = baseX;
        this.baseY = baseY;
    }

    /**
     * Draw a filled rectangle
     */
    public void fillRect(int x, int y, int width, int height, int color) {
        guiGraphics.fill(baseX + x, baseY + y, baseX + x + width, baseY + y + height, color);
    }

    /**
     * Draw text
     */
    public void drawText(String text, int x, int y, int color) {
        Font font = Minecraft.getInstance().font;
        guiGraphics.drawString(font, text, baseX + x, baseY + y, color);
    }

    /**
     * Draw centered text
     */
    public void drawCenteredText(String text, int x, int y, int color) {
        Font font = Minecraft.getInstance().font;
        guiGraphics.drawCenteredString(font, Component.literal(text), baseX + x, baseY + y, color);
    }

    /**
     * Draw word-wrapped text
     */
    public void drawWordWrap(String text, int x, int y, int width, int color) {
        Font font = Minecraft.getInstance().font;
        guiGraphics.drawWordWrap(font, Component.literal(text), baseX + x, baseY + y, width, color);
    }

    /**
     * Draw an image/texture
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
     * Draw a textured rectangle with UV coordinates
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
     * Get the raw GuiGraphics object (for advanced usage)
     * WARNING: Using this directly bypasses position offsetting
     */
    public GuiGraphics getRaw() {
        return guiGraphics;
    }

    /**
     * Get base X offset
     */
    public int getBaseX() {
        return baseX;
    }

    /**
     * Get base Y offset
     */
    public int getBaseY() {
        return baseY;
    }
}
