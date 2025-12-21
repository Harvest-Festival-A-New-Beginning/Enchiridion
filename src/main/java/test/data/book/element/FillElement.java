package test.data.book.element;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Renders a filled rectangle (useful for lines, backgrounds, separators)
 * Position and size are stored in BookWidget, not here
 */
public class FillElement extends RenderElement {
    public static final Codec<FillElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("color").forGetter(FillElement::getColor)
    ).apply(instance, FillElement::new));

    private final int color;

    public FillElement(int color) {
        this.color = color;
    }

    @Override
    public Codec<? extends RenderElement> codec() {
        return CODEC;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, int width, int height, float scale, int mouseX, int mouseY, float partialTicks) {
        // Scale doesn't really apply to fills, but we could scale the dimensions if needed
        graphics.fill(x, y, x + width, y + height, color);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeByte(getType().ordinal());
        buf.writeInt(color);
    }

    public static FillElement fromNetwork(FriendlyByteBuf buf) {
        return new FillElement(buf.readInt());
    }

    @Override
    public Type getType() {
        return Type.FILL;
    }

    public int getColor() {
        return color;
    }
}
