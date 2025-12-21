package test.client.gui.book.editor;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.test.client.gui.book.page.JsonPage;
import uk.joshiejack.penguinlib.test.data.book.BookPage;
import uk.joshiejack.penguinlib.test.data.book.BookRegistries;
import uk.joshiejack.penguinlib.test.data.book.BookWidget;
import uk.joshiejack.penguinlib.test.data.book.element.*;
import uk.joshiejack.penguinlib.util.icon.ItemIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Manages the toolbar for the JSON book editor
 * Provides buttons for adding different element types to pages
 */
@OnlyIn(Dist.CLIENT)
public class EditorToolbar {
    private static final int BUTTON_SIZE = 20;
    private static final int SPACING = 4;

    private final JsonBookEditor editor;
    private final List<Button> buttons = new ArrayList<>();

    public EditorToolbar(JsonBookEditor editor) {
        this.editor = editor;
    }

    /**
     * Initialize toolbar buttons at the specified position
     */
    public void initialize(int leftPos, int topPos, int imageWidth) {
        clear();

        int startX = leftPos + imageWidth + 10;
        int startY = topPos + 20;
        int currentY = startY;

        // Add Text button
        buttons.add(createButton(startX, currentY, "T", "Add Text Element",
            button -> addTextElement()));
        currentY += BUTTON_SIZE + SPACING;

        // Add Icon button
        buttons.add(createButton(startX, currentY, "I", "Add Icon Element",
            button -> addIconElement()));
        currentY += BUTTON_SIZE + SPACING;

        // Add Fill button
        buttons.add(createButton(startX, currentY, "F", "Add Fill Element",
            button -> addFillElement()));
        currentY += BUTTON_SIZE + SPACING;

        // Add Button element button
        buttons.add(createButton(startX, currentY, "B", "Add Button Element",
            button -> addButtonElement()));
        currentY += BUTTON_SIZE + SPACING;

        // Add Texture button
        buttons.add(createButton(startX, currentY, "X", "Add Texture Element",
            button -> addTextureElement()));

        PenguinLib.LOGGER.info("Initialized toolbar with {} buttons", buttons.size());
    }

    /**
     * Update toolbar button positions (called every frame)
     */
    public void updatePositions(int leftPos, int topPos, int imageWidth) {
        if (buttons.isEmpty()) return;

        int startX = leftPos + imageWidth + 10;
        int startY = topPos + 20;
        int currentY = startY;

        for (Button button : buttons) {
            button.setPosition(startX, currentY);
            currentY += BUTTON_SIZE + SPACING;
        }
    }

    /**
     * Clear all toolbar buttons
     */
    public void clear() {
        for (Button button : buttons) {
            editor.removeWidgetFromGui(button);
        }
        buttons.clear();
    }

    /**
     * Create a toolbar button
     */
    private Button createButton(int x, int y, String label, String tooltip, Button.OnPress onPress) {
        Button button = Button.builder(Component.literal(label), onPress)
            .bounds(x, y, BUTTON_SIZE, BUTTON_SIZE)
            .tooltip(net.minecraft.client.gui.components.Tooltip.create(Component.literal(tooltip)))
            .build();
        editor.addRenderableWidget(button);
        return button;
    }

    // Element creation methods

    private void addTextElement() {
        JsonPage currentPage = editor.getCurrentPage();
        if (currentPage == null) return;

        TextElement textElement = new TextElement("New Text", 0x000000, false);
        BookWidget widget = new BookWidget(50, 50, 100, 20, 0, 1.0f, textElement);
        addWidgetToCurrentPage(currentPage, widget);
        PenguinLib.LOGGER.info("Added text element");
    }

    private void addIconElement() {
        JsonPage currentPage = editor.getCurrentPage();
        if (currentPage == null) return;

        IconElement iconElement = new IconElement(new ItemIcon(Items.APPLE.getDefaultInstance()));
        BookWidget widget = new BookWidget(50, 50, 16, 16, 0, 1.0f, iconElement);
        addWidgetToCurrentPage(currentPage, widget);
        PenguinLib.LOGGER.info("Added icon element");
    }

    private void addFillElement() {
        JsonPage currentPage = editor.getCurrentPage();
        if (currentPage == null) return;

        FillElement fillElement = new FillElement(0xFFCCCCCC);
        BookWidget widget = new BookWidget(50, 50, 100, 50, 0, 1.0f, fillElement);
        addWidgetToCurrentPage(currentPage, widget);
        PenguinLib.LOGGER.info("Added fill element");
    }

    private void addButtonElement() {
        JsonPage currentPage = editor.getCurrentPage();
        if (currentPage == null) return;

        ButtonElement buttonElement = new ButtonElement(
            Optional.of("Button"), Optional.empty(),
            0, 0, 0, 0,
            Optional.empty(), Optional.empty());
        BookWidget widget = new BookWidget(50, 50, 80, 20, 0, 1.0f, buttonElement);
        addWidgetToCurrentPage(currentPage, widget);
        PenguinLib.LOGGER.info("Added button element");
    }

    private void addTextureElement() {
        JsonPage currentPage = editor.getCurrentPage();
        if (currentPage == null) return;

        TextureElement textureElement = new TextureElement(
            new ResourceLocation(PenguinLib.MODID, "textures/gui/book/placeholder.png"),
            0, 0, 256, 256);
        BookWidget widget = new BookWidget(50, 50, 64, 64, 0, 1.0f, textureElement);
        addWidgetToCurrentPage(currentPage, widget);
        PenguinLib.LOGGER.info("Added texture element");
    }

    /**
     * Add a widget to the current page (on the left side by default)
     * Widgets are now embedded directly in pages, no ID or registry needed
     */
    private void addWidgetToCurrentPage(JsonPage currentPage, BookWidget widget) {
        BookPage pageData = BookRegistries.PAGES.get(currentPage.getPageId());
        if (pageData == null) return;

        // Add widget directly to the left side of the page
        pageData.getLeftWidgets().add(widget);

        // Reload the page to refresh widget cache
        currentPage.reload();

        // Notify editor to refresh editable widgets
        editor.refreshEditableWidgets();
    }
}
