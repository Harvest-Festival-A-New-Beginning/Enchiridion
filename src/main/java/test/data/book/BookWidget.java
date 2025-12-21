package test.data.book;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import uk.joshiejack.penguinlib.test.data.book.element.RenderElement;
import uk.joshiejack.penguinlib.util.registry.ReloadableRegistry;

/**
 * A widget contains a single render element that can be positioned and sized
 * Position, size, and layer are stored here, not in the element itself
 */
public class BookWidget implements ReloadableRegistry.PenguinRegistry<BookWidget> {
    public static final Codec<BookWidget> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("x").forGetter(BookWidget::getX),
            Codec.INT.fieldOf("y").forGetter(BookWidget::getY),
            Codec.INT.optionalFieldOf("width", 0).forGetter(BookWidget::getWidth),
            Codec.INT.optionalFieldOf("height", 0).forGetter(BookWidget::getHeight),
            Codec.INT.optionalFieldOf("layer", 0).forGetter(BookWidget::getLayer),
            Codec.FLOAT.optionalFieldOf("scale", 1.0f).forGetter(BookWidget::getScale),
            RenderElement.CODEC.fieldOf("element").forGetter(BookWidget::getElement)
    ).apply(instance, BookWidget::new));

    private RenderElement element;
    private int x;
    private int y;
    private int width;
    private int height;
    private int layer;
    private float scale;

    public BookWidget(int x, int y, int width, int height, int layer, float scale, RenderElement element) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.layer = layer;
        this.scale = scale;
        this.element = element;
    }

    public BookWidget() {
        this.element = null;
        this.x = 0;
        this.y = 0;
        this.width = 0;
        this.height = 0;
        this.layer = 0;
        this.scale = 1.0f;
    }

    @Override
    public ResourceLocation id() {
        return BookRegistries.WIDGETS.getID(this);
    }

    public RenderElement getElement() {
        return element;
    }

    public void setElement(RenderElement element) {
        this.element = element;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    @Override
    public BookWidget fromNetwork(FriendlyByteBuf buf) {
        int x = buf.readInt();
        int y = buf.readInt();
        int width = buf.readInt();
        int height = buf.readInt();
        int layer = buf.readInt();
        float scale = buf.readFloat();
        boolean hasElement = buf.readBoolean();
        if (hasElement) {
            return new BookWidget(x, y, width, height, layer, scale, RenderElement.fromNetwork(buf));
        }
        return new BookWidget();
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(width);
        buf.writeInt(height);
        buf.writeInt(layer);
        buf.writeFloat(scale);
        buf.writeBoolean(element != null);
        if (element != null) {
            element.toNetwork(buf);
        }
    }
}
