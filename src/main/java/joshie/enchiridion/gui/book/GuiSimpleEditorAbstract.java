package joshie.enchiridion.gui.book;

import com.mojang.blaze3d.vertex.PoseStack;
import joshie.enchiridion.EConfig;
import joshie.enchiridion.api.EnchiridionAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public abstract class GuiSimpleEditorAbstract extends AbstractGuiOverlay {
    public void drawImage(GuiGraphics guiGraphics, ResourceLocation location, int x, int y, int x2, int y2, GuiBook guiBook) {
        int left = EConfig.SETTINGS.editorXPos + x;
        int top = EConfig.SETTINGS.toolbarYPos.get() + y;
        int right = EConfig.SETTINGS.editorXPos + x2;
        int bottom = EConfig.SETTINGS.toolbarYPos.get() + y2;
        int w = right - left;
        int h = bottom - top;
        int offsetX = guiBook.leftPos;
        int offsetY = guiBook.topPos;
        guiGraphics.blit(location, offsetX + left, offsetY + top, 0, 0, w, h, w, h);
    }

    public void drawBorderedRectangle(GuiGraphics guiGraphics, int x, int y, int x2, int y2, int colorI, int colorB, GuiBook guiBook) {
        int left = EConfig.SETTINGS.editorXPos + x;
        int top = EConfig.SETTINGS.toolbarYPos.get() + y;
        int right = EConfig.SETTINGS.editorXPos + x2;
        int bottom = EConfig.SETTINGS.toolbarYPos.get() + y2;
        int offsetX = guiBook.leftPos;
        int offsetY = guiBook.topPos;
        guiGraphics.fill(offsetX + left, offsetY + top, offsetX + right, offsetY + bottom, colorI);
        guiGraphics.fill(offsetX + left, offsetY + top, offsetX + right, offsetY + top + 1, colorB);
        guiGraphics.fill(offsetX + left, offsetY + bottom - 1, offsetX + right, offsetY + bottom, colorB);
        guiGraphics.fill(offsetX + left, offsetY + top, offsetX + left + 1, offsetY + bottom, colorB);
        guiGraphics.fill(offsetX + right - 1, offsetY + top, offsetX + right, offsetY + bottom, colorB);
    }

    public void drawRectangle(GuiGraphics guiGraphics, int x, int y, int x2, int y2, int colorI, GuiBook guiBook) {
        int left = EConfig.SETTINGS.editorXPos + x;
        int top = EConfig.SETTINGS.toolbarYPos.get() + y;
        int right = EConfig.SETTINGS.editorXPos + x2;
        int bottom = EConfig.SETTINGS.toolbarYPos.get() + y2;
        int offsetX = guiBook.leftPos;
        int offsetY = guiBook.topPos;
        guiGraphics.fill(offsetX + left, offsetY + top, offsetX + right, offsetY + bottom, colorI);
    }

    public void drawSplitScaledString(GuiGraphics guiGraphics, String text, int x, int y, int color, float scale, GuiBook guiBook) {
        int left = EConfig.SETTINGS.editorXPos + x;
        int top = EConfig.SETTINGS.toolbarYPos.get() + y;
        int offsetX = guiBook.leftPos;
        int offsetY = guiBook.topPos;
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(offsetX, offsetY, 0);
        poseStack.scale(scale, scale, scale);
        Font font = Minecraft.getInstance().font;
        guiGraphics.drawWordWrap(font, Component.literal(text), (int)(left / scale), (int)(top / scale), 155, color);
        poseStack.popPose();
    }
}