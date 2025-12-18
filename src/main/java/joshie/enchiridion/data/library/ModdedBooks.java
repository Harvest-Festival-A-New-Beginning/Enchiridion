package joshie.enchiridion.data.library;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for working with collections of ModdedBook entries.
 * ModdedBooks are now loaded via the ReloadableRegistry system from data packs.
 */
public class ModdedBooks {
    private final List<ModdedBook> books;

    public ModdedBooks() {
        this.books = new ArrayList<>();
    }

    public ModdedBooks(Collection<ModdedBook> books) {
        this.books = new ArrayList<>(books);
    }

    public void add(ResourceLocation id, String item, String handlerType, boolean matchNBT, boolean free) {
        books.add(new ModdedBook(id, item, handlerType, matchNBT, free));
    }

    public void addAll(Collection<ModdedBook> entries) {
        books.addAll(entries);
    }

    public List<ModdedBook> getAll() {
        return new ArrayList<>(books);
    }

    public List<ModdedBook> getHandledBooks() {
        return books.stream()
            .filter(book -> !book.isFree())
            .collect(Collectors.toList());
    }

    public List<ModdedBook> getFreeBookEntries() {
        return books.stream()
            .filter(ModdedBook::isFree)
            .collect(Collectors.toList());
    }

    public ItemStack[] getFreeBooks() {
        List<ModdedBook> freeBooks = getFreeBookEntries();
        ItemStack[] stacks = new ItemStack[freeBooks.size()];
        for (int i = 0; i < freeBooks.size(); i++) {
            stacks[i] = freeBooks.get(i).getItemStack();
        }
        return stacks;
    }

    public int size() {
        return books.size();
    }

    @Override
    public int hashCode() {
        return books.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ModdedBooks other = (ModdedBooks) obj;
        return books.equals(other.books);
    }
}
