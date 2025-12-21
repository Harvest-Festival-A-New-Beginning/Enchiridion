package test.data.book.element;

import com.mojang.serialization.Codec;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.test.data.book.BookRegistries;

import java.util.function.Function;

/**
 * Base class for all render elements in JSON-defined books
 * Elements are pure rendering logic - positioning/sizing is handled by MoveableWidget
 */
public abstract class RenderElement {
    public static final Codec<RenderElement> CODEC = BookRegistries.Elements.ELEMENT.byNameCodec()
            .dispatchStable(RenderElement::codec, Function.identity());

    public abstract Codec<? extends RenderElement> codec();

    // No position/size data - that's stored in MoveableWidget now!

    /**
     * Render this element at the specified position and size
     * @param scale The scale factor from the widget (default 1.0)
     */
    @OnlyIn(Dist.CLIENT)
    public abstract void render(GuiGraphics graphics, int x, int y, int width, int height, float scale, int mouseX, int mouseY, float partialTicks);

    /**
     * Handle click events - return true if handled
     */
    public void onClick(double mouseX, double mouseY) {
        // Default: no click handling
    }

    /**
     * Serialize to network
     */
    public abstract void toNetwork(FriendlyByteBuf buf);

    /**
     * Deserialize from network (implemented by subclasses)
     */
    public static RenderElement fromNetwork(FriendlyByteBuf buf) {
        Type type = Type.values()[buf.readByte()];
        return type.factory.apply(buf);
    }

    public abstract Type getType();

    public enum Type {
        TEXT(TextElement::fromNetwork),
        TEXTURE(TextureElement::fromNetwork),
        BUTTON(ButtonElement::fromNetwork),
        ICON(IconElement::fromNetwork),
        FILL(FillElement::fromNetwork);

        private final Function<FriendlyByteBuf, RenderElement> factory;

        Type(Function<FriendlyByteBuf, RenderElement> factory) {
            this.factory = factory;
        }

        public RenderElement fromNetwork(FriendlyByteBuf buf) {
            return factory.apply(buf);
        }
    }
}
