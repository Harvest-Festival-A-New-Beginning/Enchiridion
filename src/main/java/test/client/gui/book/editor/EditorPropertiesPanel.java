package test.client.gui.book.editor;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.test.data.book.element.TextElement;

/**
 * Left panel for editing properties of selected elements
 * Contains color picker, shadow toggle, and transform controls
 */
@OnlyIn(Dist.CLIENT)
public class EditorPropertiesPanel {
    private static final int SECTION_SPACING = 10;
    private static final int SECTION_HEADER_HEIGHT = 15;
    private static final int PANEL_PADDING = 5;
    private static final int BACKGROUND_COLOR = 0xCC2B2B2B; // Semi-transparent dark gray
    private static final int SECTION_COLOR = 0xCC1A1A1A; // Darker section background

    private final JsonBookEditor editor;
    private final ColorPicker colorPicker;
    private Button shadowToggleButton = null;

    private int x, y;
    private int width, height;

    public EditorPropertiesPanel(JsonBookEditor editor, ColorPicker colorPicker) {
        this.editor = editor;
        this.colorPicker = colorPicker;
    }

    public void initialize(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, MoveableWidget.Editable editingWidget,
                       java.util.Set<MoveableWidget.Editable> selectedWidgets) {
        // Draw panel background
        graphics.fill(x, y, x + width, y + height, BACKGROUND_COLOR);

        // Calculate sections
        int currentY = y + PANEL_PADDING + SECTION_HEADER_HEIGHT;

        // Check if we have a colorable element
        boolean hasColorableElement = false;
        int currentColor = 0x000000;
        boolean hasShadow = false;

        // Check inline editing widget first
        if (editingWidget != null && editingWidget.getElement() instanceof TextElement textElement) {
            hasColorableElement = true;
            currentColor = textElement.getColor();
            hasShadow = textElement.hasShadow();
        } else {
            // Check selected widgets
            for (MoveableWidget.Editable widget : selectedWidgets) {
                if (widget.getElement() instanceof TextElement textElement) {
                    hasColorableElement = true;
                    currentColor = textElement.getColor();
                    hasShadow = textElement.hasShadow();
                    break;
                } else if (widget.getElement() instanceof uk.joshiejack.penguinlib.test.data.book.element.FillElement fillElement) {
                    hasColorableElement = true;
                    currentColor = fillElement.getColor() & 0xFFFFFF;
                    break;
                }
            }
        }

        if (hasColorableElement) {
            // Color Section
            graphics.drawString(editor.getMinecraft().font, "Color", x + PANEL_PADDING, y + PANEL_PADDING, 0xFFFFFF);

            // Position color picker
            int colorPickerX = x + PANEL_PADDING;
            int colorPickerY = currentY;
            colorPicker.initialize(colorPickerX, colorPickerY, currentColor);

            // Render color picker
            colorPicker.render(graphics, mouseX, mouseY);

            currentY += 100; // Space for color picker

            // Shadow toggle for text elements
            if (editingWidget != null && editingWidget.getElement() instanceof TextElement ||
                selectedWidgets.stream().anyMatch(w -> w.getElement() instanceof TextElement)) {

                graphics.drawString(editor.getMinecraft().font, "Text Properties",
                    x + PANEL_PADDING, currentY, 0xFFFFFF);
                currentY += SECTION_HEADER_HEIGHT;

                // Update shadow toggle button
                updateShadowToggle(hasShadow, x + PANEL_PADDING, currentY);
            }
        } else {
            colorPicker.clear();
            if (shadowToggleButton != null) {
                editor.removeWidgetFromGui(shadowToggleButton);
                shadowToggleButton = null;
            }
        }
    }

    private void updateShadowToggle(boolean currentShadow, int buttonX, int buttonY) {
        // Remove old button if exists
        if (shadowToggleButton != null) {
            editor.removeWidgetFromGui(shadowToggleButton);
        }

        String buttonText = currentShadow ? "Shadow: ON" : "Shadow: OFF";
        shadowToggleButton = Button.builder(Component.literal(buttonText),
            button -> editor.toggleShadow())
            .bounds(buttonX, buttonY, 100, 20)
            .build();

        editor.addRenderableWidget(shadowToggleButton);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Forward clicks to color picker
        return colorPicker.mouseClicked(mouseX, mouseY, button);
    }

    public void clear() {
        colorPicker.clear();
        if (shadowToggleButton != null) {
            editor.removeWidgetFromGui(shadowToggleButton);
            shadowToggleButton = null;
        }
    }
}
