package joshie.enchiridion.data.library;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.helpers.StackHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;

import java.util.List;
import java.util.ArrayList;

public class ModdedBooks {
    public static final Codec<ModdedBooks> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ModdedBook.CODEC.listOf().optionalFieldOf("books", new ArrayList<>()).forGetter(mb -> mb.books),
        Codec.STRING.listOf().optionalFieldOf("freeBooks", new ArrayList<>()).forGetter(mb -> mb.freeBooks)
    ).apply(instance, (books, freeBooks) -> {
        ModdedBooks moddedBooks = new ModdedBooks();
        moddedBooks.books = new ArrayList<>(books);
        moddedBooks.freeBooks = new ArrayList<>(freeBooks);
        return moddedBooks;
    }));

    // Changed to List to allow Codec deserialization
    private List<ModdedBook> books = new ArrayList<>();
    private List<String> freeBooks = new ArrayList<>();

    public ModdedBooks() {
    }

    public void mergeIn(ModdedBooks outer) {
        books.addAll(outer.books);
        freeBooks.addAll(outer.freeBooks);
    }

    public static class ModdedBook {
        public static final Codec<ModdedBook> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("item").forGetter(mb -> mb.item),
            Codec.STRING.fieldOf("handlerType").forGetter(mb -> mb.handlerType),
            Codec.BOOL.optionalFieldOf("matchNBT", false).forGetter(mb -> mb.matchNBT)
        ).apply(instance, ModdedBook::new));

        private String item;
        private String handlerType;
        private boolean matchNBT;

        private ModdedBook() {}
        public ModdedBook(String item, String handlerType, boolean matchNBT) {
            this.item = item;
            this.handlerType = handlerType;
            this.matchNBT = matchNBT;
        }

        public String getItem() {
            return item;
        }

        public String getHandler() {
            return handlerType;
        }

        public boolean shouldMatchNBT() {
            return matchNBT;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((item == null) ? 0 : item.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            ModdedBook other = (ModdedBook) obj;
            if (item == null) {
                if (other.item != null) return false;
            } else if (!item.equals(other.item)) return false;
            return true;
        }
    }

    public void add(String handlerType, String itemString, boolean matchNBT) {
        books.add(new ModdedBook(itemString, handlerType, matchNBT));
    }

    public List<ModdedBook> getList() {
        return books;
    }

    public ItemStack[] getFreeBooks() {
        ItemStack[] books = new ItemStack[freeBooks.size()];
        for (int i = 0; i < freeBooks.size(); i++) {
            books[i] = StackHelper.getStackFromString(freeBooks.get(i));
        }
        return books;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((books == null) ? 0 : books.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ModdedBooks other = (ModdedBooks) obj;
        if (books == null) {
            if (other.books != null) return false;
        } else if (!books.equals(other.books)) return false;
        return true;
    }
}