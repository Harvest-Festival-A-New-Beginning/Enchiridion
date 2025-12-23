package test.data;

import com.google.common.collect.Maps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.test.data.book.*;
import uk.joshiejack.penguinlib.test.data.book.element.*;
import uk.joshiejack.penguinlib.util.icon.ItemIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Data provider for generating example book JSON files
 * Widgets are now embedded directly in pages, so no separate widget files needed
 */
public class TestBookProvider implements DataProvider {
    private final PackOutput.PathProvider bookPathProvider;
    private final PackOutput.PathProvider tabPathProvider;
    private final PackOutput.PathProvider pagePathProvider;

    public TestBookProvider(PackOutput output) {
        this.bookPathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, PenguinLib.MODID + "/book");
        this.tabPathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, PenguinLib.MODID + "/book/tab");
        this.pagePathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, PenguinLib.MODID + "/book/page");
    }

    @Override
    public @NotNull String getName() {
        return "test_books";
    }

    @Override
    public @NotNull CompletableFuture<?> run(final @NotNull CachedOutput output) {
        final List<CompletableFuture<?>> list = new ArrayList<>();
        final Map<ResourceLocation, BookDefinition> books = Maps.newHashMap();
        final Map<ResourceLocation, BookTab> tabs = Maps.newHashMap();
        final Map<ResourceLocation, BookPage> pages = Maps.newHashMap();

        buildExampleBooks(books, tabs, pages);

        // Save all generated data
        // Widgets are now embedded in pages, so no separate widget saving needed
        books.forEach((key, book) -> list.add(DataProvider.saveStable(output, BookDefinition.CODEC, book, bookPathProvider.json(key))));
        tabs.forEach((key, tab) -> list.add(DataProvider.saveStable(output, BookTab.CODEC, tab, tabPathProvider.json(key))));
        pages.forEach((key, page) -> list.add(DataProvider.saveStable(output, BookPage.CODEC, page, pagePathProvider.json(key))));

        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

    protected void buildExampleBooks(Map<ResourceLocation, BookDefinition> books,
                                     Map<ResourceLocation, BookTab> tabs,
                                     Map<ResourceLocation, BookPage> pages) {
        ResourceLocation exampleBookId = new ResourceLocation(PenguinLib.MODID, "example_book");
        ResourceLocation exampleTabId = new ResourceLocation(PenguinLib.MODID, "example_tab");
        ResourceLocation examplePageId = new ResourceLocation(PenguinLib.MODID, "example_page");

        // Create widgets (now embedded in pages, no separate IDs needed)
        BookWidget titleTextWidget = new BookWidget(
            10, 10, 150, 20, 0, 1.5f,  // Proper dimensions for title text
            new TextElement("book.example.title", 0x506154, true)
        );
        BookWidget subtitleTextWidget = new BookWidget(
            10, 30, 150, 15, 0, 1.0f,  // Proper dimensions for subtitle text
            new TextElement("book.example.subtitle", 4210752, false)
        );

        // Create button widget
        BookWidget buttonWidget = new BookWidget(
            50, 100, 80, 20, 1, 1.0f,
            new ButtonElement(
                Optional.of("book.example.button"),
                Optional.empty(),
                0, 0, 0, 0,
                Optional.of("example_script"),
                Optional.of("onButtonClick")
            )
        );

        // Create page with widgets embedded directly
        pages.put(examplePageId, new BookPage(
            List.of(titleTextWidget, subtitleTextWidget),
            List.of(buttonWidget),
            Optional.of(new ItemIcon(Items.BOOK.getDefaultInstance())),
            "example_page"
        ));

        // Create tab with the page
        tabs.put(exampleTabId, new BookTab(
            List.of(examplePageId),
            Optional.of(new ItemIcon(Items.WRITABLE_BOOK.getDefaultInstance())),
            "example_tab"
        ));

        // Create book with the tab
        books.put(exampleBookId, new BookDefinition(
            List.of(exampleTabId),
            "example_book",
            new ResourceLocation("enchiridion", "textures/books/guide_page_left.png"),
            new ResourceLocation("enchiridion", "textures/books/guide_page_right.png"),
            0x857754, 4210752, 0xFFB0A483, 0xFF9C8C63
        ));
    }
}
