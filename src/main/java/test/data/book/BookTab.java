package test.data.book;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import uk.joshiejack.penguinlib.util.icon.Icon;
import uk.joshiejack.penguinlib.util.icon.ItemIcon;
import uk.joshiejack.penguinlib.util.registry.ReloadableRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A tab contains multiple pages
 */
public class BookTab implements ReloadableRegistry.PenguinRegistry<BookTab> {
    public static final Codec<BookTab> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.listOf().fieldOf("pages").forGetter(t -> t.pages),
            Icon.CODEC.optionalFieldOf("icon").forGetter(t -> Optional.ofNullable(t.icon)),
            Codec.STRING.optionalFieldOf("name", "").forGetter(t -> t.name)
    ).apply(instance, BookTab::new));

    private final List<ResourceLocation> pages;
    private Icon icon;
    private final String name;

    public BookTab(List<ResourceLocation> pages, Optional<Icon> icon, String name) {
        this.pages = new ArrayList<>(pages);
        this.icon = icon.orElse(ItemIcon.EMPTY);
        this.name = name;
    }

    public BookTab() {
        this(List.of(), Optional.empty(), "");
    }

    @Override
    public ResourceLocation id() {
        return BookRegistries.TABS.getID(this);
    }

    public List<ResourceLocation> getPages() {
        return pages;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    @Override
    public BookTab fromNetwork(FriendlyByteBuf buf) {
        int count = buf.readInt();
        List<ResourceLocation> pages = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            pages.add(buf.readResourceLocation());
        }

        Icon icon = buf.readBoolean() ? Icon.fromNetwork(buf) : ItemIcon.EMPTY;
        String name = buf.readUtf();

        return new BookTab(pages, Optional.ofNullable(icon), name);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeInt(pages.size());
        for (ResourceLocation page : pages) {
            buf.writeResourceLocation(page);
        }

        buf.writeBoolean(icon != null && icon != ItemIcon.EMPTY);
        if (icon != null && icon != ItemIcon.EMPTY) {
            buf.writeByte(icon.getType().ordinal());
            icon.toNetwork(buf);
        }

        buf.writeUtf(name);
    }
}
