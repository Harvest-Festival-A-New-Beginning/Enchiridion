package joshie.enchiridion.lib;

import joshie.enchiridion.data.book.Book;
import joshie.enchiridion.data.book.Template;
import joshie.enchiridion.data.library.ModdedBook;
import uk.joshiejack.penguinlib.util.registry.ReloadableRegistry;

import static joshie.enchiridion.lib.EInfo.MODID;

public class EnchiridionRegistries {
    // Reloadable registry for books - will load from data/<modid>/books/<book_name>.json
    public static final ReloadableRegistry<Book> BOOKS =
            new ReloadableRegistry<>(MODID, "books", Book.CODEC, new Book(), true);

    // Reloadable registry for modded book entries - will load from data/<modid>/modded_books/<entry_name>.json
    public static final ReloadableRegistry<ModdedBook> MODDED_BOOKS =
            new ReloadableRegistry<>(MODID, "modded_books", ModdedBook.CODEC, new ModdedBook(), true);

    // Reloadable registry for templates - will load from data/<modid>/templates/<template_name>.json
    public static final ReloadableRegistry<Template> TEMPLATES =
            new ReloadableRegistry<>(MODID, "templates", Template.CODEC, new Template(), true);
}