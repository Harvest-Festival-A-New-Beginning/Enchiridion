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
 * A page contains widgets that will be rendered
 * Widgets can be positioned on left, right, or both sides of the book
 * Widgets are now embedded directly in the page JSON instead of being referenced by ID
 *
 * Supports different page types:
 * - "basic": Static page with fixed widgets (default)
 * - "left": Dynamic list page on left side (like AbstractMultiPage.Left)
 * - "right": Dynamic list page on right side (like AbstractMultiPage.Right)
 * - "both": Dynamic list page spanning both sides (like AbstractMultiPage.Both)
 */
public class BookPage implements ReloadableRegistry.PenguinRegistry<BookPage> {
    public static final Codec<BookPage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("type", "basic").forGetter(p -> p.type),
            BookWidget.CODEC.listOf().optionalFieldOf("left_widgets", List.of()).forGetter(p -> p.leftWidgets),
            BookWidget.CODEC.listOf().optionalFieldOf("right_widgets", List.of()).forGetter(p -> p.rightWidgets),
            Icon.CODEC.optionalFieldOf("icon").forGetter(p -> Optional.ofNullable(p.icon)),
            Codec.STRING.optionalFieldOf("name", "").forGetter(p -> p.name),
            ResourceLocation.CODEC.optionalFieldOf("script").forGetter(p -> Optional.ofNullable(p.script)),
            Codec.STRING.optionalFieldOf("entries_function", "getEntries").forGetter(p -> p.entriesFunction),
            Codec.INT.optionalFieldOf("per_page", 8).forGetter(p -> p.perPage),
            ResourceLocation.CODEC.optionalFieldOf("entry_template").forGetter(p -> Optional.ofNullable(p.entryTemplate))
    ).apply(instance, BookPage::new));

    private final String type;
    private final List<BookWidget> leftWidgets;
    private final List<BookWidget> rightWidgets;
    private Icon icon;
    private final String name;
    private final ResourceLocation script;
    private final String entriesFunction;
    private final int perPage;
    private final ResourceLocation entryTemplate;

    public BookPage(String type, List<BookWidget> leftWidgets, List<BookWidget> rightWidgets,
                    Optional<Icon> icon, String name, Optional<ResourceLocation> script,
                    String entriesFunction, int perPage, Optional<ResourceLocation> entryTemplate) {
        this.type = type;
        this.leftWidgets = new ArrayList<>(leftWidgets);
        this.rightWidgets = new ArrayList<>(rightWidgets);
        this.icon = icon.orElse(ItemIcon.EMPTY);
        this.name = name;
        this.script = script.orElse(null);
        this.entriesFunction = entriesFunction;
        this.perPage = perPage;
        this.entryTemplate = entryTemplate.orElse(null);
    }

    public BookPage(List<BookWidget> leftWidgets, List<BookWidget> rightWidgets,
                    Optional<Icon> icon, String name) {
        this("basic", leftWidgets, rightWidgets, icon, name, Optional.empty(), "getEntries", 8, Optional.empty());
    }

    public BookPage() {
        this(List.of(), List.of(), Optional.empty(), "");
    }

    @Override
    public ResourceLocation id() {
        return BookRegistries.PAGES.getID(this);
    }

    public List<BookWidget> getLeftWidgets() {
        return leftWidgets;
    }

    public List<BookWidget> getRightWidgets() {
        return rightWidgets;
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

    public String getType() {
        return type;
    }

    public ResourceLocation getScript() {
        return script;
    }

    public String getEntriesFunction() {
        return entriesFunction;
    }

    public int getPerPage() {
        return perPage;
    }

    public ResourceLocation getEntryTemplate() {
        return entryTemplate;
    }

    public boolean isDynamic() {
        return !"basic".equals(type);
    }

    @Override
    public BookPage fromNetwork(FriendlyByteBuf buf) {
        String type = buf.readUtf();

        int leftCount = buf.readInt();
        List<BookWidget> leftWidgets = new ArrayList<>();
        for (int i = 0; i < leftCount; i++) {
            leftWidgets.add(new BookWidget().fromNetwork(buf));
        }

        int rightCount = buf.readInt();
        List<BookWidget> rightWidgets = new ArrayList<>();
        for (int i = 0; i < rightCount; i++) {
            rightWidgets.add(new BookWidget().fromNetwork(buf));
        }

        Icon icon = buf.readBoolean() ? Icon.fromNetwork(buf) : ItemIcon.EMPTY;
        String name = buf.readUtf();

        ResourceLocation script = buf.readBoolean() ? buf.readResourceLocation() : null;
        String entriesFunction = buf.readUtf();
        int perPage = buf.readInt();
        ResourceLocation entryTemplate = buf.readBoolean() ? buf.readResourceLocation() : null;

        return new BookPage(type, leftWidgets, rightWidgets, Optional.ofNullable(icon), name,
                Optional.ofNullable(script), entriesFunction, perPage, Optional.ofNullable(entryTemplate));
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeUtf(type);

        buf.writeInt(leftWidgets.size());
        for (BookWidget widget : leftWidgets) {
            widget.toNetwork(buf);
        }

        buf.writeInt(rightWidgets.size());
        for (BookWidget widget : rightWidgets) {
            widget.toNetwork(buf);
        }

        buf.writeBoolean(icon != null && icon != ItemIcon.EMPTY);
        if (icon != null && icon != ItemIcon.EMPTY) {
            buf.writeByte(icon.getType().ordinal());
            icon.toNetwork(buf);
        }

        buf.writeUtf(name);

        buf.writeBoolean(script != null);
        if (script != null) {
            buf.writeResourceLocation(script);
        }

        buf.writeUtf(entriesFunction);
        buf.writeInt(perPage);

        buf.writeBoolean(entryTemplate != null);
        if (entryTemplate != null) {
            buf.writeResourceLocation(entryTemplate);
        }
    }
}
