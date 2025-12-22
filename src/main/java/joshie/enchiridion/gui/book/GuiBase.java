package joshie.enchiridion.gui.book;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import joshie.enchiridion.api.gui.IDrawHelper;
import joshie.enchiridion.api.recipe.IItemStack;
import joshie.enchiridion.helpers.ClientStackHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import uk.joshiejack.penguinlib.client.gui.book.Book;
import uk.joshiejack.penguinlib.world.inventory.AbstractBookMenu;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class GuiBase extends Book implements IDrawHelper {
    public final List<String> TOOLTIP = new ArrayList<>();
    public int mouseX = 0;
    public int mouseY = 0;
    private int renderX;
    private int renderY;
    private double renderWidth;
    private double renderHeight;
    private float renderSize;

    protected GuiBase(String modid, AbstractBookMenu container, Inventory inventory, Component title) {
            super(modid, container, inventory, title);
            this.imageWidth = 430;
            this.imageHeight = 217;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int x2, int y2, float partialTicks) {
        // leftPos and topPos are set by parent AbstractContainerScreen.init()
        TOOLTIP.clear();
    }

    @Override
    public void mouseMoved(double mX, double mY) {
        Minecraft mc = Minecraft.getInstance();
        final double x = mc.mouseHandler.xpos() * ((double) mc.getWindow().getGuiScaledWidth() / mc.getWindow().getScreenWidth());
        final double y = mc.mouseHandler.ypos() * ((double) mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight());

        mouseX = (int) (x - (width - imageWidth) / 2);
        mouseY = (int) (y - (height - imageHeight) / 2);
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
        int x2 = (int) Math.floor(((leftPos + getLeft(left)) / size));
        int y2 = (int) Math.floor(((topPos + getTop(top)) / size));

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
        int x2 = (int) Math.floor(((leftPos + getLeft(left)) / size)) - w;
        int y2 = (int) Math.floor(((topPos + getTop(top)) / size)) - h;

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
    public void drawStack(@Nonnull ItemStack stack, int left, int top, float size) {
        if (stack.isEmpty()) return; //Don't draw stacks that don't exist
        int x2 = (int) Math.floor(((leftPos + left) / size));
        int y2 = (int) Math.floor(((topPos + top) / size));
        ClientStackHelper.drawStack(stack, x2, y2, size);
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