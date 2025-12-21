package test.data;

import net.neoforged.neoforge.common.data.LanguageProvider;

/**
 * Language entries for test book items
 * Call addTranslations(provider) from the main PenguinLanguage provider
 */
public class TestBookLanguage {
    /**
     * Add test book translations to the given language provider
     */
    public static void addTranslations(LanguageProvider provider) {
        // Items
        provider.add("item.penguinlib.book_creator", "Book Creator");
        provider.add("item.penguinlib.json_book", "JSON Book");

        // Example Book
        provider.add("book.penguinlib.example_book", "Example Book");
        provider.add("tab.penguinlib.example_tab", "Example Tab");

        // Page content
        provider.add("book.example.title", "Example Book");
        provider.add("book.example.subtitle", "A demonstration of the JSON book system");
        provider.add("book.example.button", "Click Me!");
    }
}
