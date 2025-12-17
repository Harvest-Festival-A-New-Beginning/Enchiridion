package joshie.enchiridion.lib;

import joshie.enchiridion.data.book.Book;
import uk.joshiejack.penguinlib.data.database.ReloadableRegistry;

import static joshie.enchiridion.lib.EInfo.MODID;

public class EnchiridionRegistries {
    // Reloadable registry for books - will load from data/<modid>/books/<book_name>.json
    public static final ReloadableRegistry<Book> BOOKS =
        new ReloadableRegistry<>(MODID, "books", Book.CODEC, new Book(), true);
}
