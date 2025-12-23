package test.data.book;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.util.registry.ReloadableRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * A book definition contains tabs and book-level settings
 */
public class BookDefinition implements ReloadableRegistry.PenguinRegistry<BookDefinition> {
    public static final Codec<BookDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.listOf().fieldOf("tabs").forGetter(b -> b.tabs),
            Codec.STRING.optionalFieldOf("title", "").forGetter(b -> b.title),
            ResourceLocation.CODEC.optionalFieldOf("background_left",
                    new ResourceLocation("enchiridion", "textures/books/guide_page_left.png")).forGetter(b -> b.backgroundLeft),
            ResourceLocation.CODEC.optionalFieldOf("background_right",
                    new ResourceLocation("enchiridion", "textures/books/guide_page_right.png")).forGetter(b -> b.backgroundRight),
            Codec.INT.optionalFieldOf("font_color_1", 0x857754).forGetter(b -> b.fontColor1),
            Codec.INT.optionalFieldOf("font_color_2", 4210752).forGetter(b -> b.fontColor2),
            Codec.INT.optionalFieldOf("line_color_1", 0xFFB0A483).forGetter(b -> b.lineColor1),
            Codec.INT.optionalFieldOf("line_color_2", 0xFF9C8C63).forGetter(b -> b.lineColor2)
    ).apply(instance, BookDefinition::new));

    private final List<ResourceLocation> tabs;
    private final String title;
    private final ResourceLocation backgroundLeft;
    private final ResourceLocation backgroundRight;
    private final int fontColor1;
    private final int fontColor2;
    private final int lineColor1;
    private final int lineColor2;

    public BookDefinition(List<ResourceLocation> tabs, String title,
                         ResourceLocation backgroundLeft, ResourceLocation backgroundRight,
                         int fontColor1, int fontColor2, int lineColor1, int lineColor2) {
        this.tabs = new ArrayList<>(tabs);
        this.title = title;
        this.backgroundLeft = backgroundLeft;
        this.backgroundRight = backgroundRight;
        this.fontColor1 = fontColor1;
        this.fontColor2 = fontColor2;
        this.lineColor1 = lineColor1;
        this.lineColor2 = lineColor2;
    }

    public BookDefinition() {
        this(List.of(), "",
             new ResourceLocation("enchiridion", "textures/books/guide_page_left.png"),
             new ResourceLocation("enchiridion", "textures/books/guide_page_right.png"),
             0x857754, 4210752, 0xFFB0A483, 0xFF9C8C63);
    }

    @Override
    public ResourceLocation id() {
        return BookRegistries.BOOKS.getID(this);
    }

    public List<ResourceLocation> getTabs() {
        return tabs;
    }

    public String getTitle() {
        return title;
    }

    public ResourceLocation getBackgroundLeft() {
        return backgroundLeft;
    }

    public ResourceLocation getBackgroundRight() {
        return backgroundRight;
    }

    public int getFontColor1() {
        return fontColor1;
    }

    public int getFontColor2() {
        return fontColor2;
    }

    public int getLineColor1() {
        return lineColor1;
    }

    public int getLineColor2() {
        return lineColor2;
    }

    @Override
    public BookDefinition fromNetwork(FriendlyByteBuf buf) {
        int count = buf.readInt();
        List<ResourceLocation> tabs = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            tabs.add(buf.readResourceLocation());
        }

        String title = buf.readUtf();
        ResourceLocation backgroundLeft = buf.readResourceLocation();
        ResourceLocation backgroundRight = buf.readResourceLocation();
        int fontColor1 = buf.readInt();
        int fontColor2 = buf.readInt();
        int lineColor1 = buf.readInt();
        int lineColor2 = buf.readInt();

        return new BookDefinition(tabs, title, backgroundLeft, backgroundRight,
                fontColor1, fontColor2, lineColor1, lineColor2);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeInt(tabs.size());
        for (ResourceLocation tab : tabs) {
            buf.writeResourceLocation(tab);
        }

        buf.writeUtf(title);
        buf.writeResourceLocation(backgroundLeft);
        buf.writeResourceLocation(backgroundRight);
        buf.writeInt(fontColor1);
        buf.writeInt(fontColor2);
        buf.writeInt(lineColor1);
        buf.writeInt(lineColor2);
    }
}
