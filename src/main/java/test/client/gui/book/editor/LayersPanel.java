package test.client.gui.book.editor;

import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.test.data.book.element.*;

import java.util.List;

/**
 * Right panel showing layers/elements in the current page
 * Allows selecting, reordering, and managing visibility
 */
@OnlyIn(Dist.CLIENT)
public class LayersPanel {
    private static final int LAYER_HEIGHT = 20;
    private static final int LAYER_SPACING = 2;
    private static final int PANEL_PADDING = 5;
    private static final int BACKGROUND_COLOR = 0xCC2B2B2B; // Semi-transparent dark gray
    private static final int LAYER_BG_COLOR = 0xCC1A1A1A; // Layer background
    private static final int LAYER_SELECTED_COLOR = 0xCC3A3A3A; // Selected layer background
    private static final int LAYER_HOVER_COLOR = 0xCC2A2A2A; // Hover layer background

    private final JsonBookEditor editor;
    private int x, y;
    private int width, height;

    public LayersPanel(JsonBookEditor editor) {
        this.editor = editor;
    }

    public void initialize(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY,
                       List<MoveableWidget.Editable> widgets,
                       java.util.Set<MoveableWidget.Editable> selectedWidgets) {
        // Draw panel background
        graphics.fill(x, y, x + width, y + height, BACKGROUND_COLOR);

        // Draw "Layers" header
        graphics.drawString(editor.getMinecraft().font, "Layers",
            x + PANEL_PADDING, y + PANEL_PADDING, 0xFFFFFF);

        // Draw each layer
        int currentY = y + PANEL_PADDING + 15;

        for (int i = widgets.size() - 1; i >= 0; i--) { // Reverse order (top layer first)
            MoveableWidget.Editable widget = widgets.get(i);
            if (widget == null || widget.getElement() == null) continue;

            // Check if mouse is over this layer
            boolean isHovered = mouseX >= x && mouseX < x + width &&
                               mouseY >= currentY && mouseY < currentY + LAYER_HEIGHT;

            // Check if selected
            boolean isSelected = selectedWidgets.contains(widget);

            // Draw layer background
            int bgColor = isSelected ? LAYER_SELECTED_COLOR :
                         (isHovered ? LAYER_HOVER_COLOR : LAYER_BG_COLOR);
            graphics.fill(x + PANEL_PADDING, currentY,
                         x + width - PANEL_PADDING, currentY + LAYER_HEIGHT, bgColor);

            // Get element info
            RenderElement element = widget.getElement();
            String elementType = getElementTypeName(element);
            String elementInfo = getElementInfo(element);

            // Draw layer text
            String layerText = elementType;
            if (!elementInfo.isEmpty()) {
                layerText += ": " + elementInfo;
            }

            // Truncate if too long
            int maxWidth = width - PANEL_PADDING * 2 - 10;
            String displayText = editor.getMinecraft().font.plainSubstrByWidth(layerText, maxWidth);

            graphics.drawString(editor.getMinecraft().font, displayText,
                x + PANEL_PADDING + 5, currentY + 6, 0xFFFFFF);

            currentY += LAYER_HEIGHT + LAYER_SPACING;

            // Stop if we've run out of space
            if (currentY > y + height - LAYER_HEIGHT) break;
        }
    }

    private String getElementTypeName(RenderElement element) {
        if (element instanceof TextElement) return "Text";
        if (element instanceof TextureElement) return "Texture";
        if (element instanceof IconElement) return "Icon";
        if (element instanceof FillElement) return "Fill";
        if (element instanceof ButtonElement) return "Button";
        return "Unknown";
    }

    private String getElementInfo(RenderElement element) {
        if (element instanceof TextElement textElement) {
            String text = textElement.getText();
            return text.length() > 20 ? text.substring(0, 20) + "..." : text;
        }
        if (element instanceof TextureElement textureElement) {
            return textureElement.getTexture().toString();
        }
        if (element instanceof IconElement iconElement) {
            return iconElement.getIcon().toString();
        }
        if (element instanceof FillElement fillElement) {
            return String.format("#%06X", fillElement.getColor() & 0xFFFFFF);
        }
        if (element instanceof ButtonElement) {
            return "Button";
        }
        return "";
    }

    public MoveableWidget.Editable getLayerAt(double mouseX, double mouseY,
                                               List<MoveableWidget.Editable> widgets) {
        if (mouseX < x || mouseX >= x + width || mouseY < y + PANEL_PADDING + 15) {
            return null;
        }

        int currentY = y + PANEL_PADDING + 15;
        for (int i = widgets.size() - 1; i >= 0; i--) {
            MoveableWidget.Editable widget = widgets.get(i);
            if (widget == null || widget.getElement() == null) continue;

            if (mouseY >= currentY && mouseY < currentY + LAYER_HEIGHT) {
                return widget;
            }

            currentY += LAYER_HEIGHT + LAYER_SPACING;
            if (currentY > y + height - LAYER_HEIGHT) break;
        }

        return null;
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseX < x + width &&
               mouseY >= y && mouseY < y + height;
    }
}
