package joshie.enchiridion.gui.book;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import joshie.enchiridion.api.gui.IDrawHelper;
import joshie.enchiridion.api.recipe.IItemStack;
import joshie.enchiridion.helpers.ClientStackHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class GuiBase extends Screen implements IDrawHelper {
    public static final GuiBase INSTANCE = new GuiBase();
    protected final int xSize = 430;
    protected final int ySize = 217;

    public final List<String> TOOLTIP = new ArrayList<>();
    public int mouseX = 0;
    public int mouseY = 0;
    public int x;
    public int y;
    private int renderX;
    private int renderY;
    private double renderWidth;
    private double renderHeight;
    private float renderSize;

    protected GuiBase() {
        super(Component.translatable("enchiridion.guiBase.title"));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int x2, int y2, float partialTicks) {
        x = (width - xSize) / 2;
        y = (height - ySize) / 2;
        TOOLTIP.clear();
    }

    @Override
    public void mouseMoved(double mX, double mY) {
        Minecraft mc = Minecraft.getInstance();
        final double x = mc.mouseHandler.xpos() * ((double) mc.getWindow().getGuiScaledWidth() / mc.getWindow().getScreenWidth());
        final double y = mc.mouseHandler.ypos() * ((double) mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight());

        mouseX = (int) (x - (width - xSize) / 2);
        mouseY = (int) (y - (height - ySize) / 2);
    }

    @Override
    public void setRenderData(int xPos, int yPos, double width, double height, float size) {
        renderX = xPos;
        renderY = yPos;
        renderWidth = width;
        renderHeight = height;
        renderSize = size;
    }

    private int getLeft(double x) {
        return (int) (renderX + ((x / 150D) * renderWidth));
    }

    private int getTop(double y) {
        return (int) (renderY + ((y / 100D) * renderHeight));
    }

    @Override
    public boolean isMouseOverIItemStack(IItemStack stack) {
        if (stack == null || stack.getItemStack().isEmpty()) return false;
        int left = getLeft(stack.getX());
        int top = getTop(stack.getY());
        int scaled = (int) (16 * stack.getScale() * renderSize);
        int right = left + scaled;
        int bottom = top + scaled;
        return mouseX >= left && mouseX <= right && mouseY >= top && mouseY <= bottom;
    }

    @Override
    public boolean isMouseOverArea(double x2, double y2, int width, int height, float scale) {
        int left = getLeft(x2);
        int top = getTop(y2);
        int scaledX = (int) (width * scale * renderSize);
        int scaledY = (int) (height * scale * renderSize);
        int right = left + scaledX;
        int bottom = top + scaledY;
        return mouseX >= left && mouseX <= right && mouseY >= top && mouseY <= bottom;
    }

    @Override
    public void drawIItemStack(IItemStack stack) {
        stack.onDisplayTick(); //Update the display ticker
        drawStack(stack.getItemStack(), getLeft(stack.getX()), getTop(stack.getY()), renderSize * stack.getScale());
    }

    @Override
    public void drawTexturedRectangle(double left, double top, int u, int v, int w, int h, float scale) {
        // TODO: This method needs GuiGraphics parameter for proper rendering in 1.20.4
        float size = renderSize * scale;
        int x2 = (int) Math.floor(((x + getLeft(left)) / size));
        int y2 = (int) Math.floor(((y + getTop(top)) / size));

        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        poseStack.scale(size, size, 1.0F);
        // TODO: blit() needs to be called through GuiGraphics
        // guiGraphics.blit(texture, x2, y2, u, v, w, h);
        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    @Override
    public void drawTexturedReversedRectangle(double left, double top, int u, int v, int w, int h, float scale) {
        // TODO: This method needs GuiGraphics parameter for proper rendering in 1.20.4
        float size = renderSize * scale;
        int x2 = (int) Math.floor(((x + getLeft(left)) / size)) - w;
        int y2 = (int) Math.floor(((y + getTop(top)) / size)) - h;

        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        poseStack.scale(size, size, 1.0F);
        // TODO: blit() needs to be called through GuiGraphics
        // guiGraphics.blit(texture, x2, y2, u, v, w, h);
        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    @Override
    public void drawSplitScaledString(String text, int xPos, int yPos, int wrap, int color, float scale) {
        // TODO: This needs GuiGraphics parameter for proper text rendering in 1.20.4
        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);
        // TODO: Use GuiGraphics for text rendering
        // guiGraphics.drawWordWrap(font, Component.literal(text), (int) ((x + xPos) / scale), (int) ((y + yPos) / scale), wrap, color);
        poseStack.popPose();
    }

    @Override
    public void drawRectangle(int left, int top, int right, int bottom, int colorI) {
        // TODO: This needs GuiGraphics parameter for proper rendering in 1.20.4
        // guiGraphics.fill(x + left, y + top, x + right, y + bottom, colorI);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void drawLine(int left, int top, int right, int bottom, int thickness, int color) {
        //Fix these numbers
        left += x;
        top += y;
        right += x;
        bottom += y;

        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        RenderSystem.setShaderColor(f, f1, f2, f3);

        int posX;
        if (right > left) {
            posX = thickness;
        } else {
            posX = -thickness;
        }

        int posY;
        if (bottom > top) {
            posY = thickness;
        } else {
            posY = -thickness;
        }

        // TODO: Vertex buffer API changed in 1.20.4 - needs to be updated for new format
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        buffer.vertex((double) left, (double) top + posX, 0.0D).endVertex();
        buffer.vertex((double) right, (double) bottom + posX, 0.0D).endVertex();
        buffer.vertex((double) right + posY, (double) bottom, 0.0D).endVertex();
        buffer.vertex((double) left + posY, (double) top, 0.0D).endVertex();
        tessellator.end();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex((double) left, (double) top, 0.0D).color(f, f1, f2, f3).endVertex();
        buffer.vertex((double) left + 5, (double) top, 0.0D).color(f, f1, f2, f3).endVertex();
        buffer.vertex((double) left + 5, (double) top + 5, 0.0D).color(f, f1, f2, f3).endVertex();
        buffer.vertex((double) left, (double) top + 5, 0.0D).color(f, f1, f2, f3).endVertex();
        tessellator.end();

        RenderSystem.disableBlend();
    }

    @Override
    public void drawBorderedRectangle(int left, int top, int right, int bottom, int colorI, int colorB) {
        // TODO: This needs GuiGraphics parameter for proper rendering in 1.20.4
        // guiGraphics.fill(x + left, y + top, x + right, y + bottom, colorI);
        // guiGraphics.fill(x + left, y + top, x + right, y + top + 1, colorB);
        // guiGraphics.fill(x + left, y + bottom - 1, x + right, y + bottom, colorB);
        // guiGraphics.fill(x + left, y + top, x + left + 1, y + bottom, colorB);
        // guiGraphics.fill(x + right - 1, y + top, x + right, y + bottom, colorB);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void drawStack(@Nonnull ItemStack stack, int left, int top, float size) {
        if (stack.isEmpty()) return; //Don't draw stacks that don't exist
        int x2 = (int) Math.floor(((x + left) / size));
        int y2 = (int) Math.floor(((y + top) / size));
        ClientStackHelper.drawStack(stack, x2, y2, size);
    }

    @Override
    public void drawResource(ResourceLocation resource, int left, int top, int width, int height, float scaleX, float scaleY) {
        // TODO: This needs GuiGraphics parameter for proper rendering in 1.20.4
        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, resource);
        poseStack.scale(scaleX, scaleY, 1.0F);
        // TODO: Use guiGraphics.blit() instead
        // guiGraphics.blit(resource, (int) ((x + left) / scaleX), (int) ((y + top) / scaleY), 0, 0, width, height);
        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    @Override
    public void drawImage(ResourceLocation resource, int left, int top, int right, int bottom) {
        if (resource == null) {
            return; //DON'T YOU DARE RENDER BROKEN STUFF!!!
        }

        // TODO: This needs GuiGraphics parameter for proper rendering in 1.20.4
        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        RenderSystem.enableBlend();
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        RenderSystem.setShaderTexture(0, resource);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.vertex((double) (x + left), (double) (y + bottom), 0).uv(0, 1).color(1F, 1F, 1F, 1F).endVertex();
        buffer.vertex((double) (x + right), (double) (y + bottom), 0).uv(1, 1).color(1F, 1F, 1F, 1F).endVertex();
        buffer.vertex((double) (x + right), (double) (y + top), 0).uv(1, 0).color(1F, 1F, 1F, 1F).endVertex();
        buffer.vertex((double) (x + left), (double) (y + top), 0).uv(0, 0).color(1F, 1F, 1F, 1F).endVertex();
        tessellator.end();
        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    // TODO: renderTooltip() signature changed in 1.20.4
    // @Override //From vanilla, switching to my font renderer though
    public void renderTooltip(List<String> textLines, int x, int y, @Nonnull Font font) {
        if (!textLines.isEmpty()) {
            RenderSystem.disableDepthTest();
            int i = 0;

            for (String s : textLines) {
                int j = GuiBase.this.font.width(s);

                if (j > i) {
                    i = j;
                }
            }

            int l1 = x + 12;
            int i2 = y - 12;
            int k = 8;

            if (textLines.size() > 1) {
                k += 2 + (textLines.size() - 1) * 10;
            }

            if (l1 + i > this.width) {
                l1 -= 28 + i;
            }

            if (i2 + k + 6 > this.height) {
                i2 = this.height - k - 6;
            }

            // TODO: This needs complete refactoring for GuiGraphics API in 1.20.4
            // The entire tooltip rendering system changed
            // Use guiGraphics.renderTooltip() or guiGraphics.renderComponentTooltip() instead

            /* Old code commented out - needs GuiGraphics
            this.blitOffset = 300;
            this.itemRenderer.zLevel = 300.0F;
            int l = 0xCC312921;
            this.fillGradient(l1 - 3, i2 - 4, l1 + i + 3, i2 - 3, l, l);
            ... etc ...
            */

            // Simplified stub for compilation
            RenderSystem.enableDepthTest();
        }
    }
}