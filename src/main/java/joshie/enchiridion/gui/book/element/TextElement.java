package joshie.enchiridion.gui.book.element;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Pure rendering element for text display
 * Handles scaled text rendering with word wrapping
 */
public class TextElement implements FeatureElement {
    public static final Codec<TextElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.optionalFieldOf("text", "").forGetter(f -> f.text),
        Codec.FLOAT.optionalFieldOf("size", 1F).forGetter(f -> f.size),
        Codec.INT.optionalFieldOf("color", 0x555555).forGetter(f -> f.color)
    ).apply(instance, TextElement::new));

    private final String text;
    private final float size;
    private final int color;

    public TextElement(String text, float size, int color) {
        this.text = text;
        this.size = size;
        this.color = color;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, int width, int height, float scale, int mouseX, int mouseY, float partialTicks) {
        if (text == null || text.isEmpty()) return;

        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.scale(size, size, size);

        Font font = Minecraft.getInstance().font;
        int scaledX = (int)(x / size);
        int scaledY = (int)(y / size);
        int wrap = Math.max(50, (int)(width / size) + 4);

        graphics.drawWordWrap(font, Component.literal(text), scaledX, scaledY, wrap, color);
        poseStack.popPose();
    }

    @Override
    public Codec<? extends FeatureElement> codec() {
        return CODEC;
    }

    @Override
    public FeatureElement copy() {
        return new TextElement(text, size, color);
    }

    public String getText() {
        return text;
    }

    public float getSize() {
        return size;
    }

    public int getColor() {
        return color;
    }
}
