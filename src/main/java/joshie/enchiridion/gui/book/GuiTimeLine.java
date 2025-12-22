package joshie.enchiridion.gui.book;

import com.mojang.blaze3d.vertex.PoseStack;
import joshie.enchiridion.EConfig;
import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.api.book.Page;
import joshie.enchiridion.data.book.Page;
import joshie.enchiridion.helpers.DefaultHelper;
import joshie.enchiridion.helpers.JumpHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class GuiTimeLine extends AbstractGuiOverlay {
    private final GuiBook guiBook;
    private Page dragged = null;
    private int held = 0;
    private int startPage = 0;

    public GuiTimeLine(GuiBook guiBook) {
        this.guiBook = guiBook;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public int getStartPage() {
        return startPage;
    }

    private boolean isValid(int index, int real) {
        if ((real < 10000 && real >= 0) || (real > -1000 && real <= 0)) return index == 0 || (index + 1) % 5 == 0;
        else {
            return (index + 1) % 10 == 0;
        }
    }

    private int getOffsetX(int index, int real) {
        if (index == 0) {
            return +1;
        } else if (index == 109) {
            if (real >= 10000) return -10;
            if (real >= 1000) return -8;
            return -5;
        } else {
            if (index == 4 && real >= 1000) return -1;
            if (real >= 10000) return -6;
            if (real >= 1000) return -4;
            if (real >= 100) return -2;
            if (real >= 10) return -1;
            if (real <= -10000) return -10;
            if (real <= -10000) return -8;
            if (real <= -1000) return -6;
            if (real <= -100) return -3;
        }

        return 0;
    }

    private boolean isOverTimeLine(int xPos, int mouseX, int mouseY) {
        return mouseX >= xPos && mouseX <= xPos + 3 && mouseY >= EConfig.SETTINGS.timelineYPos.get() && mouseY <= EConfig.SETTINGS.timelineYPos.get() + 10;
    }

    @Override
    public void draw(GuiGraphics guiGraphics, int mouseX, int mouseY, GuiBook guiBookParam) {
        int offsetX = guiBook.getLeftPos();
        int offsetY = guiBook.getTopPos();

        // Draw TOOLBAR image
        int left = -9;
        int top = EConfig.SETTINGS.timelineYPos.get() - 9;
        int right = 440;
        int bottom = EConfig.SETTINGS.timelineYPos.get() + 13;
        int w = right - left;
        int h = bottom - top;
        guiGraphics.blit(TOOLBAR, offsetX + left, offsetY + top, 0, 0, w, h, w, h);

        // Draw bordered rectangle
        left = -6;
        top = EConfig.SETTINGS.timelineYPos.get();
        right = 437;
        bottom = EConfig.SETTINGS.timelineYPos.get() + 11;
        guiGraphics.fill(offsetX + left, offsetY + top, offsetX + right, offsetY + bottom, 0x00000000);
        guiGraphics.fill(offsetX + left, offsetY + top, offsetX + right, offsetY + top + 1, 0xFF191511);
        guiGraphics.fill(offsetX + left, offsetY + bottom - 1, offsetX + right, offsetY + bottom, 0xFF191511);
        guiGraphics.fill(offsetX + left, offsetY + top, offsetX + left + 1, offsetY + bottom, 0xFF191511);
        guiGraphics.fill(offsetX + right - 1, offsetY + top, offsetX + right, offsetY + bottom, 0xFF191511);

        int currentPageNumber = guiBookParam.getPage().getPageNumber();
        int hoverX = 0;
        Font font = Minecraft.getInstance().font;
        PoseStack poseStack = guiGraphics.pose();

        for (int j = 0; j < 110; j++) {
            int thisNumber = startPage + j;
            Page page = JumpHelper.getPageByNumber(guiBook.getBook(), thisNumber);
            int positionX = -5 + (j * 4);
            int fill = 0xFFE6D4A7;
            boolean exists = page != null;
            if (exists) fill = 0xFFFFFFFF;

            if (isValid(j, thisNumber + 1)) {
                left = positionX + getOffsetX(j, thisNumber + 1);
                top = EConfig.SETTINGS.timelineYPos.get() - 5;
                poseStack.pushPose();
                poseStack.translate(offsetX, offsetY, 0);
                poseStack.scale(0.5F, 0.5F, 0.5F);
                guiGraphics.drawWordWrap(font, Component.literal("" + (thisNumber + 1)), (int)(left / 0.5F), (int)(top / 0.5F), 199, 0xFFDDDDDD);
                poseStack.popPose();
                fill = exists ? 0xFFEEEEEE : 0xFFB0A483;
            }

            if (currentPageNumber == thisNumber) fill = 0xFF8C0000;
            if (isOverTimeLine(positionX, mouseX, mouseY)) {
                hoverX = positionX;
                fill = 0xFFFFFF00;
            }

            left = positionX;
            top = EConfig.SETTINGS.timelineYPos.get();
            right = positionX + 5;
            bottom = EConfig.SETTINGS.timelineYPos.get() + 10;
            guiGraphics.fill(offsetX + left, offsetY + top, offsetX + right, offsetY + bottom, fill);
        }

        //Dragging!
        if (dragged != null) {
            if (held < 30) {
                held++;
            } else {
                left = hoverX;
                top = EConfig.SETTINGS.timelineYPos.get();
                right = hoverX + 4;
                bottom = EConfig.SETTINGS.timelineYPos.get() + 10;
                guiGraphics.fill(offsetX + left, offsetY + top, offsetX + right, offsetY + bottom, 0xFFFF9326);
            }
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, GuiBook guiBook) {
        for (int i = 0; i < 110; i++) {
            int positionX = -5 + (i * 4);
            if (isOverTimeLine(positionX, mouseX, mouseY)) {
                //If we don't succeed at jumping to the page because it doesn't exist
                //Then we should create it, and then jump to it;
                dragged = JumpHelper.getPageByNumber(guiBook.getBook(), startPage + i);
                return true;
            }
        }

        dragged = null;
        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, GuiBook guiBookParam) {
        boolean placing = held >= 30;
        for (int i = 0; i < 110; i++) {
            int positionX = -5 + (i * 4);
            if (isOverTimeLine(positionX, mouseX, mouseY)) {
                //If we don't succeed at jumping to the page because it doesn't exist
                //Then we should create it, and then jump to it;
                int thisNumber = startPage + i;
                if (placing) {
                    JumpHelper.insertPage(guiBookParam.getBook(), thisNumber, dragged);
                } else if (!guiBookParam.jumpToPageIfExists(thisNumber)) {
                    Page page = DefaultHelper.addDefaults(guiBookParam.getBook(), new Page(thisNumber).setBook(guiBookParam.getBook()));
                    guiBookParam.getBook().addPage(page);
                    guiBookParam.jumpToPageIfExists(thisNumber);
                }
            }
        }

        //Reset everything
        dragged = null;
        held = 0;
    }

    @Override
    public void scroll(boolean down, int mouseX, int mouseY) {
        if (mouseX >= -5 && mouseX <= 431 && mouseY >= EConfig.SETTINGS.timelineYPos.get() && mouseY <= EConfig.SETTINGS.timelineYPos.get() + 10) {
            if (down) {
                startPage += 5;
            } else {
                startPage -= 5;
            }
        }
    }
}