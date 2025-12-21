package test.client.gui.book.editor;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.test.client.gui.book.editor.MoveableWidget;

import java.util.function.Consumer;

/**
 * Color picker UI for selecting element colors
 * Displays Paint-style color grid loaded from JSON and hex input field
 */
@OnlyIn(Dist.CLIENT)
public class ColorPicker {
    private static final int SWATCH_SIZE = 14; // Comfortable swatch size
    private final ColorPalette palette = ColorPalette.getInstance();

    private final JsonBookEditor editor;
    private final Consumer<Integer> onColorSelected;
    private EditBox hexInput;
    private int x, y;
    private int currentColor = 0x000000;

    public ColorPicker(JsonBookEditor editor, Consumer<Integer> onColorSelected) {
        this.editor = editor;
        this.onColorSelected = onColorSelected;
    }

    public void initialize(int x, int y, int currentColor) {
        // Clear any existing hex input first to avoid duplicates
        clear();

        this.x = x;
        this.y = y;
        this.currentColor = currentColor;

        // Create hex input field
        String hexString = String.format("%06X", currentColor & 0xFFFFFF);
        int inputWidth = palette.getColorsPerRow() * (SWATCH_SIZE + 1) - 1;
        hexInput = new EditBox(editor.getMinecraft().font, x, y, inputWidth, 16,
            Component.literal("Hex"));
        hexInput.setValue(hexString);
        hexInput.setMaxLength(6);
        hexInput.setResponder(this::onHexInput);
        editor.addRenderableWidget(hexInput);
    }

    public void clear() {
        if (hexInput != null) {
            editor.removeWidgetFromGui(hexInput);
            hexInput = null;
        }
    }

    private void onHexInput(String hex) {
        try {
            if (hex.length() == 6) {
                int color = Integer.parseInt(hex, 16);
                currentColor = color;
                onColorSelected.accept(color);
            }
        } catch (NumberFormatException e) {
            // Invalid hex, ignore
        }
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        if (hexInput == null) return;

        // Render color swatches from palette
        int swatchY = y + 20;
        java.util.List<Integer> colors = palette.getColors();
        int colorsPerRow = palette.getColorsPerRow();

        for (int i = 0; i < colors.size(); i++) {
            int swatchX = x + (i % colorsPerRow) * (SWATCH_SIZE + 1);
            int row = i / colorsPerRow;
            int sy = swatchY + row * (SWATCH_SIZE + 1);

            int color = colors.get(i);

            // Draw swatch
            graphics.fill(swatchX, sy, swatchX + SWATCH_SIZE, sy + SWATCH_SIZE, 0xFF000000 | color);

            // Draw border (yellow for selected, dark gray for normal)
            int borderColor = (color == currentColor) ? 0xFFFFFF00 : 0xFF444444;
            graphics.fill(swatchX - 1, sy - 1, swatchX + SWATCH_SIZE + 1, sy, borderColor); // Top
            graphics.fill(swatchX - 1, sy + SWATCH_SIZE, swatchX + SWATCH_SIZE + 1, sy + SWATCH_SIZE + 1, borderColor); // Bottom
            graphics.fill(swatchX - 1, sy, swatchX, sy + SWATCH_SIZE, borderColor); // Left
            graphics.fill(swatchX + SWATCH_SIZE, sy, swatchX + SWATCH_SIZE + 1, sy + SWATCH_SIZE, borderColor); // Right
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hexInput == null) return false;

        // Check hex input click first and forward to the EditBox
        if (mouseX >= hexInput.getX() && mouseX < hexInput.getX() + hexInput.getWidth() &&
            mouseY >= hexInput.getY() && mouseY < hexInput.getY() + hexInput.getHeight()) {
            // Forward click to EditBox so it can gain focus and handle cursor positioning
            hexInput.mouseClicked(mouseX, mouseY, button);
            return true; // Consume event to prevent deselection
        }

        // Check swatch clicks
        int swatchY = y + 20;
        java.util.List<Integer> colors = palette.getColors();
        int colorsPerRow = palette.getColorsPerRow();

        for (int i = 0; i < colors.size(); i++) {
            int swatchX = x + (i % colorsPerRow) * (SWATCH_SIZE + 1);
            int row = i / colorsPerRow;
            int sy = swatchY + row * (SWATCH_SIZE + 1);

            if (mouseX >= swatchX && mouseX < swatchX + SWATCH_SIZE &&
                mouseY >= sy && mouseY < sy + SWATCH_SIZE) {
                currentColor = colors.get(i);
                hexInput.setValue(String.format("%06X", currentColor & 0xFFFFFF));
                onColorSelected.accept(currentColor);
                return true;
            }
        }

        return false;
    }
}
