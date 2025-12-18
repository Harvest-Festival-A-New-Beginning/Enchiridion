package joshie.enchiridion.data.library;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.helpers.StackHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import uk.joshiejack.penguinlib.util.registry.ReloadableRegistry;

/**
 * Represents a modded book entry that can be registered in the library system.
 * Each entry defines an item, its handler type, and whether it's a free book.
 */
public class ModdedBook implements ReloadableRegistry.PenguinRegistry<ModdedBook> {
    public static final Codec<ModdedBook> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("id").forGetter(ModdedBook::id),
        Codec.STRING.fieldOf("item").forGetter(mb -> mb.item),
        Codec.STRING.fieldOf("handler_type").forGetter(mb -> mb.handlerType),
        Codec.BOOL.optionalFieldOf("match_nbt", false).forGetter(mb -> mb.matchNBT),
        Codec.BOOL.optionalFieldOf("free", false).forGetter(mb -> mb.free)
    ).apply(instance, (id, item, handlerType, matchNBT, free) -> {
        ModdedBook book = new ModdedBook();
        book.bookId = id;
        book.item = item;
        book.handlerType = handlerType;
        book.matchNBT = matchNBT;
        book.free = free;
        return book;
    }));

    private ResourceLocation bookId;
    private String item;
    private String handlerType;
    private boolean matchNBT;
    private boolean free; // If true, this is a "free book" (no handler, just added to library)

    public ModdedBook() {}

    public ModdedBook(ResourceLocation id, String item, String handlerType, boolean matchNBT, boolean free) {
        this.bookId = id;
        this.item = item;
        this.handlerType = handlerType;
        this.matchNBT = matchNBT;
        this.free = free;
    }

    @Override
    public ResourceLocation id() {
        return bookId != null ? bookId : new ResourceLocation("enchiridion", "default");
    }

    @Override
    public ModdedBook fromNetwork(FriendlyByteBuf buf) {
        // Network serialization if needed
        return this;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf) {
        // Network serialization if needed
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

    public boolean isFree() {
        return free;
    }

    public ItemStack getItemStack() {
        return StackHelper.getStackFromString(item);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bookId == null) ? 0 : bookId.hashCode());
        result = prime * result + ((item == null) ? 0 : item.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ModdedBook other = (ModdedBook) obj;
        if (bookId == null) {
            if (other.bookId != null) return false;
        } else if (!bookId.equals(other.bookId)) return false;
        if (item == null) {
            if (other.item != null) return false;
        } else if (!item.equals(other.item)) return false;
        return true;
    }
}
