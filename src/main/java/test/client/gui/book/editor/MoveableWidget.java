package test.client.gui.book.editor;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.test.data.book.BookWidget;
import uk.joshiejack.penguinlib.test.data.book.element.ButtonElement;
import uk.joshiejack.penguinlib.test.data.book.element.RenderElement;

/**
 * Abstract base class for widgets that display BookWidget content
 * Provides two implementations:
 * - Editable: For edit mode with selection, dragging, resizing capabilities
 * - Display: For normal viewing mode with optional button interactions
 */
@OnlyIn(Dist.CLIENT)
public abstract class MoveableWidget extends AbstractWidget {
    protected static final int GRID_SIZE = 4;
    protected static final int HANDLE_SIZE = 6;
    protected static final int SELECTION_COLOR = 0xFFFFFF00; // Yellow
    protected static final int HANDLE_COLOR = 0xFF00FF00; // Green

    protected final RenderElement element;
    protected final BookWidget parentWidget;
    protected final int offsetX;
    protected final int offsetY;

    // Element properties stored in MoveableWidget
    protected int elementX;
    protected int elementY;
    protected int elementWidth;
    protected int elementHeight;
    protected int layer;
    protected float scale;

    public MoveableWidget(RenderElement element, BookWidget parentWidget, int offsetX, int offsetY, int x, int y, int width, int height, int layer, float scale) {
        super(offsetX + x, offsetY + y, width, height, Component.literal("Widget"));
        this.element = element;
        this.parentWidget = parentWidget;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.elementX = x;
        this.elementY = y;
        this.elementWidth = width;
        this.elementHeight = height;
        this.layer = layer;
        this.scale = scale;
    }

    public boolean isButton() {
        return element instanceof ButtonElement;
    }

    /**
     * Update widget bounds based on element position and size
     */
    protected void updateBounds() {
        setX(offsetX + elementX);
        setY(offsetY + elementY);
        setWidth(elementWidth);
        setHeight(elementHeight);
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        // Render the element at the widget's position (null check for deleted elements)
        if (element != null) {
            element.render(graphics, getX(), getY(), getWidth(), getHeight(), scale, mouseX, mouseY, partialTicks);
        }
    }

    /**
     * Update widget screen position from parent BookWidget data
     * Called when BookWidget position/size changes
     */
    public void updatePositionFromParent() {
        if (parentWidget != null) {
            elementX = parentWidget.getX();
            elementY = parentWidget.getY();
            elementWidth = parentWidget.getWidth();
            elementHeight = parentWidget.getHeight();
            scale = parentWidget.getScale();
            layer = parentWidget.getLayer();
            updateBounds();
        }
    }

    public RenderElement getElement() {
        return element;
    }

    public BookWidget getParentWidget() {
        return parentWidget;
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narration) {
        narration.add(net.minecraft.client.gui.narration.NarratedElementType.HINT,
            Component.literal("Widget element at " + elementX + ", " + elementY));
    }

    /**
     * Editable widget for edit mode with selection, dragging, and resizing capabilities
     */
    @OnlyIn(Dist.CLIENT)
    public static class Editable extends MoveableWidget {
        private boolean isSelected = false;
        private boolean showBoundingBox = false;
        private boolean isBeingEdited = false;

        public Editable(RenderElement element, BookWidget parentWidget, int offsetX, int offsetY, int x, int y, int width, int height, int layer, float scale) {
            super(element, parentWidget, offsetX, offsetY, x, y, width, height, layer, scale);
        }

        @Override
        public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
            // Don't render the element if it's being edited (inline editor will render it)
            if (!isBeingEdited) {
                // Render the base element
                super.renderWidget(graphics, mouseX, mouseY, partialTicks);
            }

            // Get position and size once for both rendering methods
            int x = getX();
            int y = getY();
            int width = getWidth();
            int height = getHeight();

            // Render bounding box if enabled
            if (showBoundingBox) {
                renderBoundingBox(graphics, x, y, width, height);
            }

            // Render selection overlay if selected
            if (isSelected) {
                renderSelection(graphics, x, y, width, height);
            }
        }

        private void renderSelection(GuiGraphics graphics, int x, int y, int width, int height) {

            // Draw selection border
            graphics.fill(x - 1, y - 1, x + width + 1, y, SELECTION_COLOR); // Top
            graphics.fill(x - 1, y + height, x + width + 1, y + height + 1, SELECTION_COLOR); // Bottom
            graphics.fill(x - 1, y, x, y + height, SELECTION_COLOR); // Left
            graphics.fill(x + width, y, x + width + 1, y + height, SELECTION_COLOR); // Right

            // Draw resize handles at corners
            drawHandle(graphics, x - HANDLE_SIZE / 2, y - HANDLE_SIZE / 2); // Top-left
            drawHandle(graphics, x + width - HANDLE_SIZE / 2, y - HANDLE_SIZE / 2); // Top-right
            drawHandle(graphics, x - HANDLE_SIZE / 2, y + height - HANDLE_SIZE / 2); // Bottom-left
            drawHandle(graphics, x + width - HANDLE_SIZE / 2, y + height - HANDLE_SIZE / 2); // Bottom-right
        }

        private void drawHandle(GuiGraphics graphics, int x, int y) {
            graphics.fill(x, y, x + HANDLE_SIZE, y + HANDLE_SIZE, HANDLE_COLOR);
        }

        private void renderBoundingBox(GuiGraphics graphics, int x, int y, int width, int height) {

            // Draw bounding box in cyan
            int color = 0x8800FFFF; // Semi-transparent cyan
            graphics.fill(x, y, x + width, y + 1, color); // Top
            graphics.fill(x, y + height - 1, x + width, y + height, color); // Bottom
            graphics.fill(x, y, x + 1, y + height, color); // Left
            graphics.fill(x + width - 1, y, x + width, y + height, color); // Right
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            // Don't consume events - let the editor handle all mouse interactions
            // The editor will manage selection, dragging, and resizing
            return false;
        }

        /**
         * Check if a point is on a resize handle (screen coordinates)
         * Uses a larger hit area than the visual handle for easier clicking
         */
        public boolean isOnResizeHandle(double mouseX, double mouseY) {
            // Convert to relative coordinates
            int relativeX = (int) (mouseX - offsetX);
            int relativeY = (int) (mouseY - offsetY);

            // Check bottom-right corner (main resize handle)
            // Use a larger hit area (12x12) than the visual size (6x6) for easier clicking
            int hitAreaSize = HANDLE_SIZE * 2;
            int handleX = elementX + elementWidth - hitAreaSize / 2;
            int handleY = elementY + elementHeight - hitAreaSize / 2;

            return relativeX >= handleX && relativeX < handleX + hitAreaSize &&
                   relativeY >= handleY && relativeY < handleY + hitAreaSize;
        }

        public void setSelected(boolean selected) {
            this.isSelected = selected;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setShowBoundingBox(boolean show) {
            this.showBoundingBox = show;
        }

        public boolean isShowingBoundingBox() {
            return showBoundingBox;
        }

        public void setBeingEdited(boolean editing) {
            this.isBeingEdited = editing;
        }

        public boolean isBeingEdited() {
            return isBeingEdited;
        }
    }

    /**
     * Display widget for normal viewing mode
     * Handles button clicks but no editing capabilities
     */
    @OnlyIn(Dist.CLIENT)
    public static class Display extends MoveableWidget {
        public Display(RenderElement element, BookWidget parentWidget, int offsetX, int offsetY, int x, int y, int width, int height, int layer, float scale) {
            super(element, parentWidget, offsetX, offsetY, x, y, width, height, layer, scale);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            // If this is a button element, handle the click
            if (isButton() && isMouseOver(mouseX, mouseY)) {
                ButtonElement buttonElement = (ButtonElement) element;
                // Button click handling would go here
                // For now, we'll let it through to be handled by the screen
                return true;
            }
            return false;
        }
    }
}
