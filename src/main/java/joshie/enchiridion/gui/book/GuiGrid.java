package joshie.enchiridion.gui.book;

import net.minecraft.client.gui.GuiGraphics;

public class GuiGrid extends AbstractGuiOverlay {
    public static final GuiGrid INSTANCE = new GuiGrid();
    private boolean pixelGrid = true;
    private boolean isVisible;
    private boolean isFullWidth;
    private int gridSize = 5;

    private GuiGrid() {
    }

    public int getGridSize() {
        return isPixelGrid() ? 3 : gridSize;
    }

    public boolean isActivated() {
        return isVisible;
    }

    public boolean isPixelGrid() {
        return pixelGrid;
    }

    public void toggle() {
        if (pixelGrid && !isVisible) {
            isVisible = true;
            gridSize = 5;
        } else if (pixelGrid) {
            pixelGrid = false;
            isVisible = false;
        } else if (!isVisible) {
            gridSize = 5;
            isVisible = true;
        } else {
            if (!isFullWidth) {
                if (gridSize == 5) {
                    gridSize = 10;
                } else if (gridSize == 10) {
                    gridSize = 5;
                    isFullWidth = true;
                }
            } else {
                if (gridSize == 5) {
                    gridSize = 10;
                } else if (gridSize == 10) {
                    isFullWidth = false;
                    isVisible = false;
                    pixelGrid = true;
                }
            }
        }
    }

    @Override
    public void draw(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (isActivated()) {
            int offsetX = GuiBook.INSTANCE.x;
            int offsetY = GuiBook.INSTANCE.y;

            if (pixelGrid) {
                int xOffset = -1;
                int yOffset = 2;
                for (int x = -10; x < 440; x += getGridSize()) {
                    for (int y = -15; y < 245; y += getGridSize()) {
                        guiGraphics.fill(offsetX + x + xOffset, offsetY + y + yOffset, offsetX + x + 1 + xOffset, offsetY + y + yOffset + getGridSize(), 0x22000000);
                        guiGraphics.fill(offsetX + x + xOffset, offsetY + y + yOffset, offsetX + x + xOffset + getGridSize(), offsetY + y + 1 + yOffset, 0x22000000);
                    }
                }
            } else if (isFullWidth) {
                for (int x = -10; x < 440; x += getGridSize()) {
                    for (int y = -15; y < 245; y += getGridSize()) {
                        guiGraphics.fill(offsetX + x, offsetY + y, offsetX + x + 1, offsetY + y + getGridSize(), 0x22000000);
                        guiGraphics.fill(offsetX + x, offsetY + y, offsetX + x + getGridSize(), offsetY + y + 1, 0x22000000);
                    }
                }
            } else {
                for (int x = -10; x < 440; x += getGridSize()) {
                    for (int y = -15; y < 245; y += getGridSize()) {
                        guiGraphics.fill(offsetX + x, offsetY + y, offsetX + x + 1, offsetY + y + 1, 0x22000000);
                    }
                }
            }
        }
    }
}