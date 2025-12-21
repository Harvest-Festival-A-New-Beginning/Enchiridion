package test.data.book.element;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Renders a texture at a specific position
 * Position and size are stored in BookWidget, not here
 */
public class TextureElement extends RenderElement {
    public static final Codec<TextureElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(TextureElement::getTexture),
            Codec.INT.optionalFieldOf("u", 0).forGetter(e -> e.u),
            Codec.INT.optionalFieldOf("v", 0).forGetter(e -> e.v),
            Codec.INT.optionalFieldOf("textureWidth", 256).forGetter(e -> e.textureWidth),
            Codec.INT.optionalFieldOf("textureHeight", 256).forGetter(e -> e.textureHeight)
    ).apply(instance, TextureElement::new));

    private final ResourceLocation texture;
    private final int u;
    private final int v;
    private final int textureWidth;
    private final int textureHeight;

    public TextureElement(ResourceLocation texture, int u, int v, int textureWidth, int textureHeight) {
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    public Codec<? extends RenderElement> codec() {
        return CODEC;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, int width, int height, float scale, int mouseX, int mouseY, float partialTicks) {
        // Apply scale if not 1.0
        if (scale != 1.0f) {
            graphics.pose().pushPose();
            graphics.pose().translate(x, y, 0);
            graphics.pose().scale(scale, scale, 1.0f);
            graphics.blit(texture, 0, 0, u, v, (int)(width / scale), (int)(height / scale), textureWidth, textureHeight);
            graphics.pose().popPose();
        } else {
            graphics.blit(texture, x, y, u, v, width, height, textureWidth, textureHeight);
        }
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeByte(getType().ordinal());
        buf.writeResourceLocation(texture);
        buf.writeInt(u);
        buf.writeInt(v);
        buf.writeInt(textureWidth);
        buf.writeInt(textureHeight);
    }

    public static TextureElement fromNetwork(FriendlyByteBuf buf) {
        return new TextureElement(
                buf.readResourceLocation(), // texture
                buf.readInt(), // u
                buf.readInt(), // v
                buf.readInt(), // textureWidth
                buf.readInt()  // textureHeight
        );
    }

    @Override
    public Type getType() {
        return Type.TEXTURE;
    }

    public ResourceLocation getTexture() {
        return texture;
    }
}
