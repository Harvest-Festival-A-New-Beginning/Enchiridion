package joshie.enchiridion.gui.book;

import joshie.enchiridion.EConfig;
import joshie.enchiridion.api.gui.IToolbarButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class GuiToolbar extends AbstractGuiOverlay {
    private final GuiBook guiBook;
    private List<IToolbarButton> leftButtons = new ArrayList<>();
    private List<IToolbarButton> rightButtons = new ArrayList<>();

    public GuiToolbar(GuiBook guiBook) {
        this.guiBook = guiBook;
    }

    public void registerButton(IToolbarButton button) {
        if (button.isLeftAligned()) leftButtons.add(button);
        else rightButtons.add(button);
    }

    private boolean isOverButton(int xPos, int mouseX, int mouseY) {
        return mouseX >= xPos && mouseX <= xPos + 8 && mouseY >= EConfig.SETTINGS.toolbarYPos.get() + 2 && mouseY <= EConfig.SETTINGS.toolbarYPos.get() + 10;
    }

    private static final int X_START = -3;
    private static final int X_END = 426;

    @Override
    public void draw(GuiGraphics guiGraphics, int mouseX, int mouseY, GuiBook guiBookParam) {
        int offsetX = guiBook.x;
        int offsetY = guiBook.y;

        //Draw toolbar background
        int left = -10;
        int top = EConfig.SETTINGS.toolbarYPos.get() - 5;
        int right = 441;
        int bottom = EConfig.SETTINGS.toolbarYPos.get() + 17;
        int w = right - left;
        int h = bottom - top;
        guiGraphics.blit(TOOLBAR, offsetX + left, offsetY + top, 0, 0, w, h, w, h);

        // Draw bordered rectangle overlay
        left = -6;
        top = EConfig.SETTINGS.toolbarYPos.get();
        right = 437;
        bottom = EConfig.SETTINGS.toolbarYPos.get() + 12;
        int colorI = 0xFFE4D6AE;
        int colorB = 0x5579725A;
        guiGraphics.fill(offsetX + left, offsetY + top, offsetX + right, offsetY + bottom, colorI);
        guiGraphics.fill(offsetX + left, offsetY + top, offsetX + right, offsetY + top + 1, colorB);
        guiGraphics.fill(offsetX + left, offsetY + bottom - 1, offsetX + right, offsetY + bottom, colorB);
        guiGraphics.fill(offsetX + left, offsetY + top, offsetX + left + 1, offsetY + bottom, colorB);
        guiGraphics.fill(offsetX + right - 1, offsetY + top, offsetX + right, offsetY + bottom, colorB);

        //Draw the left hand buttons first
        int x = X_START;
        for (IToolbarButton button : leftButtons) {
            ResourceLocation resource = isOverButton(x, mouseX, mouseY) ? button.getHoverResource(guiBook) : button.getResource(guiBook);
            left = x;
            top = EConfig.SETTINGS.toolbarYPos.get() + 2;
            right = x + 8;
            bottom = EConfig.SETTINGS.toolbarYPos.get() + 10;
            w = right - left;
            h = bottom - top;
            guiGraphics.blit(resource, offsetX + left, offsetY + top, 0, 0, w, h, w, h);
            x += 12;
        }

        //Now draw the right hand buttons
        x = X_END;
        for (IToolbarButton button : rightButtons) {
            ResourceLocation resource = isOverButton(x, mouseX, mouseY) ? button.getHoverResource(guiBook) : button.getResource(guiBook);
            left = x;
            top = EConfig.SETTINGS.toolbarYPos.get() + 2;
            right = x + 8;
            bottom = EConfig.SETTINGS.toolbarYPos.get() + 10;
            w = right - left;
            h = bottom - top;
            guiGraphics.blit(resource, offsetX + left, offsetY + top, 0, 0, w, h, w, h);
            x -= 12;
        }
    }

    @Override
    public void addToolTip(List<String> tooltip, int mouseX, int mouseY) {
        int x = X_START;
        for (IToolbarButton button : leftButtons) {
            if (isOverButton(x, mouseX, mouseY)) {
                tooltip.add(button.getTooltipText(guiBook));
            }
            x += 12;
        }

        //Right side of buttons
        x = X_END;
        for (IToolbarButton button : rightButtons) {
            if (isOverButton(x, mouseX, mouseY)) {
                tooltip.add(button.getTooltipText(guiBook));
            }
            x -= 12;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, GuiBook guiBook) {
        int x = X_START;
        for (IToolbarButton button : leftButtons) {
            if (isOverButton(x, mouseX, mouseY)) {
                button.performAction(guiBook);
            }
            x += 12;
        }

        //Right side of buttons
        x = X_END;
        for (IToolbarButton button : rightButtons) {
            if (isOverButton(x, mouseX, mouseY)) {
                button.performAction(guiBook);
            }
            x -= 12;
        }
    }
}
