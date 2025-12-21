package test.client.gui.book;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.client.gui.book.Book;
import uk.joshiejack.penguinlib.client.gui.book.tab.Tab;
import uk.joshiejack.penguinlib.test.client.gui.book.page.JsonPage;
import uk.joshiejack.penguinlib.test.data.book.BookDefinition;
import uk.joshiejack.penguinlib.test.data.book.BookPage;
import uk.joshiejack.penguinlib.test.data.book.BookRegistries;
import uk.joshiejack.penguinlib.test.data.book.BookTab;
import uk.joshiejack.penguinlib.test.data.book.BookWidget;
import uk.joshiejack.penguinlib.test.item.JsonBookMenu;

/**
 * Abstract base class for JSON-defined books
 * Handles loading book definitions and rendering pages
 * Subclasses implement editor vs display functionality
 */
@OnlyIn(Dist.CLIENT)
public abstract class JsonBook extends Book<JsonBookMenu> {
    protected final ResourceLocation bookId;
    protected BookDefinition bookData;

    protected JsonBook(ResourceLocation bookId, JsonBookMenu container, Inventory inv) {
        super(PenguinLib.MODID, container, inv, Component.translatable("book." + bookId.getNamespace() + "." + bookId.getPath()));
        this.bookId = bookId;
        loadBookData();
        initializeBookFromDefinition();
    }

    protected void loadBookData() {
        bookData = BookRegistries.BOOKS.get(bookId);
        if (bookData == null) {
            bookData = new BookDefinition();
            PenguinLib.LOGGER.warn("Book definition not found: {}", bookId);
        }
    }

    /**
     * Initialize the book with tabs and pages from the JSON definition
     */
    protected void initializeBookFromDefinition() {
        if (bookData == null) return;
        // Apply styling from book definition
        this.fontColor1 = bookData.getFontColor1();
        this.fontColor2 = bookData.getFontColor2();
        this.lineColor1 = bookData.getLineColor1();
        this.lineColor2 = bookData.getLineColor2();
        // Create tabs and pages from book definition
        for (ResourceLocation tabId : bookData.getTabs()) {
            BookTab tabDef = BookRegistries.TABS.get(tabId);
            if (tabDef != null) {
                Tab tab = this.withTab(
                    new Tab(
                        Component.translatable("tab." + tabId.getNamespace() + "." + tabId.getPath()),
                        tabDef.getIcon()
                    )
                );

                // Add pages to this tab
                for (ResourceLocation pageId : tabDef.getPages()) {
                    BookPage pageDef = BookRegistries.PAGES.get(pageId);
                    if (pageDef != null) {
                        tab.withPage(new JsonPage(pageId));
                    }
                }
            }
        }

        PenguinLib.LOGGER.info("Initialized book {} with {} tabs", bookId, bookData.getTabs().size());
    }

    protected abstract void addWidget(BookWidget widget, int offsetX, int offsetY);

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        renderExtras(graphics, mouseX, mouseY, partialTicks);
    }

    protected abstract void renderExtras(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks);

    public ResourceLocation getBookId() {
        return bookId;
    }
}
