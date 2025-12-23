package joshie.enchiridion.gui.book.element;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Pure rendering element for filled box/rectangle
 */
public class BoxElement implements FeatureElement {
    public static final Codec<BoxElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("color").forGetter(f -> f.color)
    ).apply(instance, BoxElement::new));

    private final int color;

    public BoxElement(int color) {
        this.color = color;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, int width, int height, float scale, int mouseX, int mouseY, float partialTicks) {
        graphics.fill(x, y, x + width, y + height, color);
    }

    @Override
    public Codec<? extends FeatureElement> codec() {
        return CODEC;
    }

    @Override
    public FeatureElement copy() {
        return new BoxElement(color);
    }

    public int getColor() {
        return color;
    }
}

