package test.data.book.element;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Renders text at a specific position
 * Position and size are stored in BookWidget, not here
 */
public class TextElement extends RenderElement {
    public static final Codec<TextElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("text").forGetter(TextElement::getText),
            Codec.INT.optionalFieldOf("color", 0x000000).forGetter(e -> e.color),
            Codec.BOOL.optionalFieldOf("shadow", false).forGetter(e -> e.shadow)
    ).apply(instance, TextElement::new));

    private final String text;
    private final int color;
    private final boolean shadow;

    public TextElement(String text, int color, boolean shadow) {
        this.text = text;
        this.color = color;
        this.shadow = shadow;
    }

    /**
     * Calculate the width of the text element based on the text and scale
     */
    @OnlyIn(Dist.CLIENT)
    private static int calculateWidth(String text, float scale) {
        if (text == null || text.isEmpty()) return 0;
        Font font = Minecraft.getInstance().font;
        Component component = Component.translatable(text);
        return (int) (font.width(component) * scale);
    }

    /**
     * Calculate the height of the text element based on the scale
     */
    @OnlyIn(Dist.CLIENT)
    private static int calculateHeight(float scale) {
        return (int) (9 * scale); // Font height is 9 pixels
    }

    @Override
    public Codec<? extends RenderElement> codec() {
        return CODEC;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, int width, int height, float scale, int mouseX, int mouseY, float partialTicks) {
        Font font = Minecraft.getInstance().font;

        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(scale, scale, 1.0f);

        // Handle multiline text by splitting on newlines
        String[] lines = text.split("\n", -1);
        int lineHeight = font.lineHeight + 2;
        int currentY = 0;

        for (String line : lines) {
            // Use literal text, not translatable (preserves exact user input including newlines)
            Component component = Component.literal(line);
            if (shadow) {
                graphics.drawString(font, component, 0, currentY, color, true);
            } else {
                graphics.drawString(font, component, 0, currentY, color, false);
            }
            currentY += lineHeight;
        }

        graphics.pose().popPose();
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeByte(getType().ordinal());
        buf.writeUtf(text);
        buf.writeInt(color);
        buf.writeBoolean(shadow);
    }

    public static TextElement fromNetwork(FriendlyByteBuf buf) {
        return new TextElement(
                buf.readUtf(), // text
                buf.readInt(), // color
                buf.readBoolean() // shadow
        );
    }

    @Override
    public Type getType() {
        return Type.TEXT;
    }

    public String getText() {
        return text;
    }

    public int getColor() {
        return color;
    }

    public boolean hasShadow() {
        return shadow;
    }
}
