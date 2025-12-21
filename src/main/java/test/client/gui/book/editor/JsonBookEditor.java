package test.client.gui.book.editor;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.test.client.gui.book.JsonBook;
import uk.joshiejack.penguinlib.test.client.gui.book.page.JsonPage;
import uk.joshiejack.penguinlib.test.data.book.*;
import uk.joshiejack.penguinlib.test.data.book.element.*;
import uk.joshiejack.penguinlib.test.item.JsonBookMenu;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Book editor that allows editing JSON book definitions in-game
 * Toggle edit mode by holding shift while in creative mode
 */
@OnlyIn(Dist.CLIENT)
public class JsonBookEditor extends JsonBook {
    protected static final Object2ObjectMap<ResourceLocation, JsonBookEditor> INSTANCES = new Object2ObjectOpenHashMap<>();
    private static final int GRID_SIZE = 4;
    private static final int GRID_COLOR = 0x40FFFFFF; // Semi-transparent white

    // Layout constants for editor panels
    private static final int LEFT_PANEL_WIDTH = 200;
    private static final int RIGHT_PANEL_WIDTH = 200;
    private static final int TOOLBAR_HEIGHT = 30;
    private static final int LEFT_PANEL_X = 0;
    private static final int TOOLBAR_Y = 0;

    private boolean showGrid = false;
    private boolean showBoundingBoxes = false;

    // Editing state - editor-level event handling for proper drag/resize support
    private final Set<MoveableWidget.Editable> selectedWidgets = new HashSet<>();

    // Editor-level drag/resize state
    private boolean isDragging = false;
    private boolean isResizing = false;
    private MoveableWidget.Editable draggedWidget = null;
    private int dragStartX = 0;
    private int dragStartY = 0;
    private int draggedWidgetStartX = 0;
    private int draggedWidgetStartY = 0;
    private int draggedWidgetStartWidth = 0;
    private int draggedWidgetStartHeight = 0;

    // Text editing state
    private InlineTextEditor inlineTextEditor = null;
    private MoveableWidget.Editable textEditingWidget = null;
    private long lastClickTime = 0;
    private MoveableWidget.Editable lastClickedWidget = null;
    private static final long DOUBLE_CLICK_THRESHOLD = 500; // milliseconds

    // Current page being edited
    @Nullable
    private JsonPage currentPage = null;
    private boolean editingLeftSide = true;

    // UI Panels
    private final EditorToolbar toolbar;
    private final ColorPicker colorPicker;
    private final EditorPropertiesPanel propertiesPanel;
    private final LayersPanel layersPanel;

    private JsonBookEditor(ResourceLocation bookId, JsonBookMenu container, Inventory inv) {
        super(bookId, container, inv);
        this.toolbar = new EditorToolbar(this);
        this.colorPicker = new ColorPicker(this, this::onColorSelected);
        this.propertiesPanel = new EditorPropertiesPanel(this, colorPicker);
        this.layersPanel = new LayersPanel(this);
    }

    private void onColorSelected(int color) {
        // If we're in inline edit mode, update the editing widget's color
        if (inlineTextEditor != null && textEditingWidget != null &&
            textEditingWidget.getElement() instanceof TextElement textElement) {
            TextElement newElement = new TextElement(textElement.getText(), color, textElement.hasShadow());
            textEditingWidget.getParentWidget().setElement(newElement);
            // Don't refresh widgets - we're still editing
            PenguinLib.LOGGER.info("Updated inline editing text color to: {}", String.format("%06X", color));
            return;
        }

        // Otherwise, apply color to all selected text and fill elements
        for (MoveableWidget.Editable widget : selectedWidgets) {
            if (widget.getElement() instanceof TextElement textElement) {
                TextElement newElement = new TextElement(textElement.getText(), color, textElement.hasShadow());
                widget.getParentWidget().setElement(newElement);
            } else if (widget.getElement() instanceof uk.joshiejack.penguinlib.test.data.book.element.FillElement) {
                uk.joshiejack.penguinlib.test.data.book.element.FillElement newElement =
                    new uk.joshiejack.penguinlib.test.data.book.element.FillElement(0xFF000000 | color);
                widget.getParentWidget().setElement(newElement);
            }
        }
        // Refresh widgets to show new color
        initializeEditableWidgets();
    }

    public void toggleShadow() {
        // Toggle shadow for inline editing widget or all selected text widgets
        if (inlineTextEditor != null && textEditingWidget != null &&
            textEditingWidget.getElement() instanceof TextElement textElement) {
            // Toggle shadow for inline editing text
            boolean newShadow = !textElement.hasShadow();
            TextElement newElement = new TextElement(textElement.getText(), textElement.getColor(), newShadow);
            textEditingWidget.getParentWidget().setElement(newElement);
            PenguinLib.LOGGER.info("Toggled shadow to: {}", newShadow);
        } else {
            // Toggle shadow for all selected text elements
            for (MoveableWidget.Editable widget : selectedWidgets) {
                if (widget.getElement() instanceof TextElement textElement) {
                    boolean newShadow = !textElement.hasShadow();
                    TextElement newElement = new TextElement(textElement.getText(), textElement.getColor(), newShadow);
                    widget.getParentWidget().setElement(newElement);
                }
            }
            initializeEditableWidgets();
        }
    }

    public static JsonBookEditor getInstance(ResourceLocation bookId, Inventory inv) {
        return INSTANCES.computeIfAbsent(bookId, id -> new JsonBookEditor(bookId, new JsonBookMenu(-1, bookId), inv));
    }

    /**
     * Initialize editor for the current page
     */
    public void initializeEditor() {
        // Clear editing state
        selectedWidgets.clear();
        isDragging = false;
        isResizing = false;
        draggedWidget = null;

        // Rebuild current page with editable widgets
        if (currentPage != null) {
            initializeEditableWidgets();
            toolbar.initialize(leftPos, topPos, imageWidth);
        }

        PenguinLib.LOGGER.info("Initialized editor for book: {}", bookId);
    }

    @Override
    protected void renderExtras(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        // Initialize panels with current screen dimensions
        int screenWidth = this.width;
        int screenHeight = this.height;

        // Left panel - properties
        propertiesPanel.initialize(LEFT_PANEL_X, TOOLBAR_HEIGHT, LEFT_PANEL_WIDTH, screenHeight - TOOLBAR_HEIGHT);

        // Right panel - layers (aligned to right edge)
        int rightPanelX = screenWidth - RIGHT_PANEL_WIDTH;
        layersPanel.initialize(rightPanelX, TOOLBAR_HEIGHT, RIGHT_PANEL_WIDTH, screenHeight - TOOLBAR_HEIGHT);

        // Update toolbar button positions (now at top)
        toolbar.updatePositions(LEFT_PANEL_WIDTH, TOOLBAR_Y, screenWidth - LEFT_PANEL_WIDTH - RIGHT_PANEL_WIDTH);

        if (showGrid) {
            renderGrid(graphics);
        }

        // Render left properties panel
        propertiesPanel.render(graphics, mouseX, mouseY, textEditingWidget, selectedWidgets);

        // Render right layers panel
        List<MoveableWidget.Editable> editableWidgets = getEditableWidgets();
        layersPanel.render(graphics, mouseX, mouseY, editableWidgets, selectedWidgets);

        // Show edit mode indicator and hotkey info (now at top below toolbar)
        String helpText = inlineTextEditor != null ?
            "TEXT EDIT MODE [Enter=Save Shift+Enter=Newline Esc=Cancel]" :
            "EDIT MODE [G=Grid B=Bounds +/-=Scale Ctrl+S=Save Arrows=Move DoubleClick=Edit]";
        graphics.drawString(font, helpText, LEFT_PANEL_WIDTH + 10, TOOLBAR_HEIGHT + 5, 0xFFFF00);

        if (!selectedWidgets.isEmpty() && inlineTextEditor == null) {
            MoveableWidget.Editable firstSelected = selectedWidgets.iterator().next();
            BookWidget parentWidget = firstSelected.getParentWidget();
            String info1 = String.format("Selected: %d | Layer %d | Data: (%d,%d) %dx%d | Scale: %.2f",
                    selectedWidgets.size(),
                    parentWidget.getLayer(),
                    parentWidget.getX(), parentWidget.getY(),
                    parentWidget.getWidth(), parentWidget.getHeight(),
                    parentWidget.getScale());
            String info2 = String.format("Widget Screen Pos: (%d,%d)",
                    firstSelected.getX(), firstSelected.getY());
            graphics.drawString(font, info1, LEFT_PANEL_WIDTH + 10, TOOLBAR_HEIGHT + 20, 0xFFFFFF);
            graphics.drawString(font, info2, LEFT_PANEL_WIDTH + 10, TOOLBAR_HEIGHT + 32, 0xFFFFFF);
        }

        // Render inline text editor if active
        if (inlineTextEditor != null && textEditingWidget != null) {
            inlineTextEditor.tick();
            TextElement textElement = (TextElement) textEditingWidget.getElement();
            inlineTextEditor.render(graphics,
                textEditingWidget.getX(),
                textEditingWidget.getY(),
                textEditingWidget.getWidth(),
                textEditingWidget.getParentWidget().getScale(),
                textElement.getColor(),
                textElement.hasShadow());
        }
    }

    private void renderGrid(GuiGraphics graphics) {
        // Render grid on both pages
        int leftStart = leftPos;
        int rightStart = leftPos + imageWidth / 2;
        int topStart = topPos;

        for (int x = 0; x < imageWidth / 2; x += GRID_SIZE) {
            // Left page vertical lines
            graphics.fill(leftStart + x, topStart, leftStart + x + 1, topStart + imageHeight, GRID_COLOR);
            // Right page vertical lines
            graphics.fill(rightStart + x, topStart, rightStart + x + 1, topStart + imageHeight, GRID_COLOR);
        }

        for (int y = 0; y < imageHeight; y += GRID_SIZE) {
            // Left page horizontal lines
            graphics.fill(leftStart, topStart + y, leftStart + imageWidth / 2, topStart + y + 1, GRID_COLOR);
            // Right page horizontal lines
            graphics.fill(rightStart, topStart + y, rightStart + imageWidth / 2, topStart + y + 1, GRID_COLOR);
        }
    }

    public List<MoveableWidget.Editable> getEditableWidgets() {
        return renderables.stream()
                .filter(wgt -> wgt instanceof MoveableWidget.Editable)
                .map(w -> (MoveableWidget.Editable) w)
                .toList();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        List<MoveableWidget.Editable> editableWidgets = getEditableWidgets();

        // Check layers panel first (clicking on a layer selects it)
        if (layersPanel.isMouseOver(mouseX, mouseY)) {
            MoveableWidget.Editable layerWidget = layersPanel.getLayerAt(mouseX, mouseY, editableWidgets);
            if (layerWidget != null) {
                boolean shiftHeld = hasShiftDown();
                if (shiftHeld) {
                    // Toggle selection
                    if (selectedWidgets.contains(layerWidget)) {
                        selectedWidgets.remove(layerWidget);
                        layerWidget.setSelected(false);
                    } else {
                        selectedWidgets.add(layerWidget);
                        layerWidget.setSelected(true);
                    }
                } else {
                    // Select this one, deselect others
                    for (MoveableWidget.Editable widget : selectedWidgets) {
                        widget.setSelected(false);
                    }
                    selectedWidgets.clear();
                    selectedWidgets.add(layerWidget);
                    layerWidget.setSelected(true);
                }
                return true;
            }
        }

        // Check properties panel (includes color picker)
        if (propertiesPanel.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        boolean shiftHeld = hasShiftDown();
        MoveableWidget.Editable clickedWidget = null;
        // Find which widget was clicked (iterate in reverse to prioritize higher layers)
        for (int i = editableWidgets.size() - 1; i >= 0; i--) {
            MoveableWidget.Editable widget = editableWidgets.get(i);
            boolean mouseOver = widget.isMouseOver(mouseX, mouseY);
            PenguinLib.LOGGER.info("  Checking widget {}: isMouseOver = {} (bounds: {}, {}, {}, {})",
                i, mouseOver, widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight());
            if (mouseOver) {
                clickedWidget = widget;
                break;
            }
        }

        if (clickedWidget != null) {
            PenguinLib.LOGGER.info("Found clicked widget: {}", clickedWidget.getElement().getClass().getSimpleName());
            // Check for double-click on TextElement to enter text editing mode
            long currentTime = System.currentTimeMillis();
            if (clickedWidget == lastClickedWidget &&
                (currentTime - lastClickTime) < DOUBLE_CLICK_THRESHOLD &&
                clickedWidget.getElement() instanceof TextElement) {
                openTextEditor(clickedWidget);
                lastClickedWidget = null; // Reset to prevent triple-click
                return true;
            }
            lastClickedWidget = clickedWidget;
            lastClickTime = currentTime;

            // Check if clicking on resize handle of an already selected widget
            if (selectedWidgets.contains(clickedWidget) && clickedWidget.isOnResizeHandle(mouseX, mouseY)) {
                isResizing = true;
                isDragging = false;
                draggedWidget = clickedWidget;
                dragStartX = (int) mouseX;
                dragStartY = (int) mouseY;
                BookWidget parentWidget = clickedWidget.getParentWidget();
                draggedWidgetStartWidth = parentWidget.getWidth();
                draggedWidgetStartHeight = parentWidget.getHeight();
                PenguinLib.LOGGER.info("Started resizing widget");
                return true;
            }

            // Handle selection
            if (shiftHeld) {
                // Shift+click: toggle selection
                if (selectedWidgets.contains(clickedWidget)) {
                    selectedWidgets.remove(clickedWidget);
                    clickedWidget.setSelected(false);
                    PenguinLib.LOGGER.info("Deselected widget (shift+click)");
                } else {
                    selectedWidgets.add(clickedWidget);
                    clickedWidget.setSelected(true);
                    PenguinLib.LOGGER.info("Added widget to selection (shift+click)");
                }
            } else {
                // Normal click: deselect all others, select this one
                for (MoveableWidget.Editable widget : selectedWidgets) {
                    widget.setSelected(false);
                }
                selectedWidgets.clear();
                selectedWidgets.add(clickedWidget);
                clickedWidget.setSelected(true);
                PenguinLib.LOGGER.info("Selected widget: {}", clickedWidget.getElement().getClass().getSimpleName());
            }

            // Start dragging
            isDragging = true;
            isResizing = false;
            draggedWidget = clickedWidget;
            dragStartX = (int) mouseX;
            dragStartY = (int) mouseY;
            BookWidget parentWidget = clickedWidget.getParentWidget();
            draggedWidgetStartX = parentWidget.getX();
            draggedWidgetStartY = parentWidget.getY();

            return true;
        }

        // Didn't click on a widget - deselect all if not holding shift
        if (!shiftHeld) {
            for (MoveableWidget.Editable widget : selectedWidgets) {
                widget.setSelected(false);
            }
            selectedWidgets.clear();
            colorPicker.clear();
            PenguinLib.LOGGER.info("Deselected all widgets");
        }

        // Let toolbar and other UI elements handle the click
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (draggedWidget == null) {
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }

        BookWidget parentWidget = draggedWidget.getParentWidget();
        boolean snapToGrid = hasShiftDown();

        if (isDragging) {
            // Calculate delta from drag start
            int deltaX = (int) (mouseX - dragStartX);
            int deltaY = (int) (mouseY - dragStartY);
            int newX = draggedWidgetStartX + deltaX;
            int newY = draggedWidgetStartY + deltaY;

            // Apply grid snapping if shift is held
            if (snapToGrid) {
                newX = snapToGridValue(newX);
                newY = snapToGridValue(newY);
            }

            // Update widget position
            parentWidget.setX(newX);
            parentWidget.setY(newY);

            // Update widget bounds for rendering
            draggedWidget.updatePositionFromParent();

            return true;
        } else if (isResizing) {
            // Calculate delta from drag start
            int deltaX = (int) (mouseX - dragStartX);
            int deltaY = (int) (mouseY - dragStartY);
            int newWidth = Math.max(1, draggedWidgetStartWidth + deltaX);
            int newHeight = Math.max(1, draggedWidgetStartHeight + deltaY);

            // Apply grid snapping if shift is held
            if (snapToGrid) {
                newWidth = snapToGridValue(newWidth);
                newHeight = snapToGridValue(newHeight);
            }

            // Update widget size
            parentWidget.setWidth(newWidth);
            parentWidget.setHeight(newHeight);

            // Update widget bounds for rendering
            draggedWidget.updatePositionFromParent();

            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isDragging || isResizing) {
            BookWidget parentWidget = draggedWidget.getParentWidget();
            PenguinLib.LOGGER.info("Released widget at ({}, {}) size ({}, {})",
                parentWidget.getX(), parentWidget.getY(),
                parentWidget.getWidth(), parentWidget.getHeight());

            isDragging = false;
            isResizing = false;
            draggedWidget = null;
            return true;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    private int snapToGridValue(int value) {
        return (value / GRID_SIZE) * GRID_SIZE;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Handle text editing mode keys
        if (inlineTextEditor != null) {
            boolean shiftHeld = hasShiftDown();
            if ((keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) && !shiftHeld) {
                // Enter without shift - save and exit
                saveTextEdit();
                return true;
            } else if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                closeTextEditor();
                return true;
            }
            // Let inline editor handle other keys (including Shift+Enter for newlines)
            return inlineTextEditor.keyPressed(keyCode, scanCode, modifiers);
        }

        // Editor-level hotkeys (always handled by editor)
        if (keyCode == GLFW.GLFW_KEY_S && (modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
            // Save with Ctrl+S
            saveToJson();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_G) {
            // Toggle grid with G
            showGrid = !showGrid;
            PenguinLib.LOGGER.info("Grid display: {}", showGrid ? "ON" : "OFF");
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_B) {
            // Toggle bounding boxes with B
            showBoundingBoxes = !showBoundingBoxes;
            getEditableWidgets().forEach(wgt -> wgt.setShowBoundingBox(showBoundingBoxes));

            PenguinLib.LOGGER.info("Bounding boxes: {}", showBoundingBoxes ? "ON" : "OFF");
            return true;
        }

        // Arrow key movement for selected widgets
        if (!selectedWidgets.isEmpty()) {
            int moveAmount = hasShiftDown() ? 10 : 1;
            boolean moved = false;

            switch (keyCode) {
                case GLFW.GLFW_KEY_UP:
                    for (MoveableWidget.Editable widget : selectedWidgets) {
                        BookWidget parent = widget.getParentWidget();
                        parent.setY(parent.getY() - moveAmount);
                        widget.updatePositionFromParent();
                    }
                    moved = true;
                    break;
                case GLFW.GLFW_KEY_DOWN:
                    for (MoveableWidget.Editable widget : selectedWidgets) {
                        BookWidget parent = widget.getParentWidget();
                        parent.setY(parent.getY() + moveAmount);
                        widget.updatePositionFromParent();
                    }
                    moved = true;
                    break;
                case GLFW.GLFW_KEY_LEFT:
                    for (MoveableWidget.Editable widget : selectedWidgets) {
                        BookWidget parent = widget.getParentWidget();
                        parent.setX(parent.getX() - moveAmount);
                        widget.updatePositionFromParent();
                    }
                    moved = true;
                    break;
                case GLFW.GLFW_KEY_RIGHT:
                    for (MoveableWidget.Editable widget : selectedWidgets) {
                        BookWidget parent = widget.getParentWidget();
                        parent.setX(parent.getX() + moveAmount);
                        widget.updatePositionFromParent();
                    }
                    moved = true;
                    break;
                case GLFW.GLFW_KEY_DELETE:
                    // Delete selected widgets
                    for (MoveableWidget.Editable widget : selectedWidgets) {
                        widget.getParentWidget().setElement(null);
                    }
                    selectedWidgets.clear();
                    initializeEditableWidgets(); // Refresh widget list
                    PenguinLib.LOGGER.info("Deleted selected widgets");
                    return true;
                case GLFW.GLFW_KEY_EQUAL: // + key (with or without shift)
                case GLFW.GLFW_KEY_KP_ADD: // Numpad +
                    // Increase scale
                    for (MoveableWidget.Editable widget : selectedWidgets) {
                        BookWidget parent = widget.getParentWidget();
                        float newScale = Math.min(parent.getScale() + 0.1f, 5.0f); // Max 5x scale
                        parent.setScale(newScale);
                        widget.updatePositionFromParent();
                    }
                    PenguinLib.LOGGER.info("Increased scale for {} widget(s)", selectedWidgets.size());
                    return true;
                case GLFW.GLFW_KEY_MINUS: // - key
                case GLFW.GLFW_KEY_KP_SUBTRACT: // Numpad -
                    // Decrease scale
                    for (MoveableWidget.Editable widget : selectedWidgets) {
                        BookWidget parent = widget.getParentWidget();
                        float newScale = Math.max(parent.getScale() - 0.1f, 0.1f); // Min 0.1x scale
                        parent.setScale(newScale);
                        widget.updatePositionFromParent();
                    }
                    PenguinLib.LOGGER.info("Decreased scale for {} widget(s)", selectedWidgets.size());
                    return true;
            }

            if (moved) {
                PenguinLib.LOGGER.info("Moved {} widget(s) by {} pixels", selectedWidgets.size(), moveAmount);
                return true;
            }
        }

        // Let widgets handle their own keyboard events (layer/scale changes)
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char character, int modifiers) {
        // Forward character input to inline text editor
        if (inlineTextEditor != null) {
            return inlineTextEditor.charTyped(character, modifiers);
        }
        return super.charTyped(character, modifiers);
    }

    @Override
    protected void addWidget(BookWidget widget, int offsetX, int offsetY) {
        //Create MoveableWidget.Editable for edit mode
        MoveableWidget.Editable moveable = new MoveableWidget.Editable(widget.getElement(), widget, offsetX, offsetY,
                widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight(), widget.getLayer(), widget.getScale());
        //Add as renderable only - widgets don't consume events in edit mode
        addRenderableOnly(moveable);
    }

    /**
     * Initialize MoveableWidgets for all render elements on the current page
     */
    private void initializeEditableWidgets() {
        // Save which widgets (by their parent BookWidget) were selected
        Set<BookWidget> selectedParents = new HashSet<>();
        for (MoveableWidget.Editable widget : selectedWidgets) {
            selectedParents.add(widget.getParentWidget());
        }

        // Remove all existing editable widgets before creating new ones
        List<MoveableWidget.Editable> oldWidgets = getEditableWidgets();
        for (MoveableWidget.Editable widget : oldWidgets) {
            removeWidget(widget);
        }

        selectedWidgets.clear();

        if (currentPage == null) {
            return;
        }

        // Create moveable widgets for left page elements
        // Use same offset calculation as Book class: bgLeftOffset = centre - 154, where centre = leftPos + imageWidth/2
        int rightOffsetX = leftPos + imageWidth / 2;
        int leftOffsetX = rightOffsetX - 154;
        int leftOffsetY = 15 + topPos;  // Book class adds 15 pixel margin
        int rightOffsetY = 15 + topPos;
        PenguinLib.LOGGER.info("EDIT MODE - Left page offset: ({}, {})", leftOffsetX, leftOffsetY);
        currentPage.getLeftWidgetInstances().forEach(wgt -> addWidget(wgt, leftOffsetX, leftOffsetY));
        currentPage.getRightWidgetInstances().forEach(wgt -> addWidget(wgt, rightOffsetX, rightOffsetY));

        // Restore selection state
        for (MoveableWidget.Editable widget : getEditableWidgets()) {
            if (selectedParents.contains(widget.getParentWidget())) {
                widget.setSelected(true);
                selectedWidgets.add(widget);
            }
        }
    }

    private void saveToJson() {
        PenguinLib.LOGGER.info("Saving book to JSON: {}", bookId);

        // Save the main book definition
        boolean success = JsonBookSaver.saveBook(bookId, bookData);

        // Save all tabs
        for (ResourceLocation tabId : bookData.getTabs()) {
            BookTab tab = BookRegistries.TABS.get(tabId);
            if (tab != null) {
                JsonBookSaver.saveTab(tabId, tab);

                // Save all pages in the tab
                // Widgets are now embedded directly in pages, so no separate widget saving needed
                for (ResourceLocation pageId : tab.getPages()) {
                    BookPage page = BookRegistries.PAGES.get(pageId);
                    if (page != null) {
                        JsonBookSaver.savePage(pageId, page);
                    }
                }
            }
        }

        String message = success ? "Book saved successfully!" : "Failed to save book!";
        Minecraft.getInstance().player.displayClientMessage(
                Component.literal(message), false);
    }

    public void setCurrentPage(JsonPage page) {
        PenguinLib.LOGGER.info("Setting current page to: {}", page.getPageId());
        this.currentPage = page;

        // Initialize editable widgets for this page
        PenguinLib.LOGGER.info("Initializing editable widgets");
        initializeEditableWidgets();
        // Always reinitialize toolbar to ensure it's visible after GUI rebuilds
        toolbar.clear();
        toolbar.initialize(leftPos, topPos, imageWidth);
        PenguinLib.LOGGER.info("Reinitialized toolbar");
    }

    public ResourceLocation getBookId() {
        return bookId;
    }

    @Nullable
    public JsonPage getCurrentPage() {
        return currentPage;
    }

    /**
     * Refresh editable widgets (called by toolbar when adding new widgets)
     */
    public void refreshEditableWidgets() {
        initializeEditableWidgets();
    }

    /**
     * Remove a widget from the GUI (used by toolbar and color picker)
     */
    public void removeWidgetFromGui(AbstractWidget widget) {
        removeWidget(widget);
    }

    /**
     * Open text editor for a TextElement widget
     */
    private void openTextEditor(MoveableWidget.Editable widget) {
        if (!(widget.getElement() instanceof TextElement)) return;

        TextElement textElement = (TextElement) widget.getElement();
        textEditingWidget = widget;

        // Mark widget as being edited (prevents double rendering)
        widget.setBeingEdited(true);

        // Create inline text editor
        inlineTextEditor = new InlineTextEditor(font, textElement.getText());
        inlineTextEditor.setMaxLength(1000);

        // Set up auto-resizing (adjust width/height to fit text, not scale)
        BookWidget parentWidget = widget.getParentWidget();
        inlineTextEditor.setOnSizeChanged((newWidth, newHeight) -> {
            parentWidget.setWidth(newWidth);
            parentWidget.setHeight(newHeight);
            widget.updatePositionFromParent();
            PenguinLib.LOGGER.info("Auto-resized text to: {}x{}", newWidth, newHeight);
        });
        inlineTextEditor.setAutoResize(true);

        PenguinLib.LOGGER.info("Opened inline text editor for: {}", textElement.getText());
    }

    /**
     * Close text editor without saving
     */
    private void closeTextEditor() {
        if (inlineTextEditor != null) {
            // Clear the editing flag
            if (textEditingWidget != null) {
                textEditingWidget.setBeingEdited(false);
            }
            inlineTextEditor = null;
            textEditingWidget = null;

            PenguinLib.LOGGER.info("Closed text editor (cancelled)");
        }
    }

    /**
     * Save text edit and update the TextElement
     */
    private void saveTextEdit() {
        if (inlineTextEditor == null || textEditingWidget == null) return;

        String newText = inlineTextEditor.getText();
        TextElement oldElement = (TextElement) textEditingWidget.getElement();

        // Create new TextElement with updated text (preserving color and shadow from old element)
        TextElement newElement = new TextElement(newText, oldElement.getColor(), oldElement.hasShadow());

        // Update the BookWidget with the new element
        BookWidget parentWidget = textEditingWidget.getParentWidget();
        parentWidget.setElement(newElement);

        // Close editor (this will also call updateColorPicker)
        closeTextEditor();

        // Refresh widgets to show updated text
        initializeEditableWidgets();

        PenguinLib.LOGGER.info("Saved text edit: {}", newText);
    }

}
