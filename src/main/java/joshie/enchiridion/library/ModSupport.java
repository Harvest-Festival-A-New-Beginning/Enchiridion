package joshie.enchiridion.library;

import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.data.library.ModdedBook;
import joshie.enchiridion.data.library.ModdedBooks;
import joshie.enchiridion.lib.EnchiridionRegistries;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;

/**
 * Manages modded book entries loaded from data packs via the ReloadableRegistry system.
 * ModdedBook entries are now loaded from data/&lt;modid&gt;/modded_books/&lt;entry_name&gt;.json
 */
public class ModSupport {
    private static ModdedBooks books;

    /**
     * Load and apply all modded book entries from the registry.
     * This should be called after resource reload.
     */
    public static void loadFromRegistry() {
        // Get all ModdedBook entries from the registry
        Collection<ModdedBook> entries = EnchiridionRegistries.MODDED_BOOKS.getAll();
        books = new ModdedBooks(entries);

        // Apply all book handlers
        EnchiridionAPI.library.resetStacksAllowedInLibrary();
        for (ModdedBook book : books.getHandledBooks()) {
            try {
                ItemStack stack = book.getItemStack();
                if (!stack.isEmpty()) {
                    switch (book.getHandler()) {
                        case "blacklist":
                            LibraryRegistry.INSTANCE.unregisterBookHandlerForStackFromJSON(stack, book.shouldMatchNBT());
                            break;
                        case "customwood":
                            EnchiridionAPI.library.registerWood(stack, book.shouldMatchNBT());
                            break;
                        default:
                            LibraryRegistry.INSTANCE.registerBookHandlerForStackFromJSON(book.getHandler(), stack, book.shouldMatchNBT());
                            break;
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Get all free books (books without handlers that are just added to the library).
     */
    public static ItemStack[] getFreeBooks() {
        if (books == null) {
            loadFromRegistry();
        }
        return books.getFreeBooks();
    }

    /**
     * Get the collection of all modded book entries.
     */
    public static ModdedBooks getBooks() {
        if (books == null) {
            loadFromRegistry();
        }
        return books;
    }

    /**
     * Reset the cache. Should be called on resource reload.
     */
    public static void reset() {
        books = null;
    }
}
