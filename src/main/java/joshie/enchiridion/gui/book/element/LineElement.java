package joshie.enchiridion.gui.book.element;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Pure rendering element for lines
 */
public class LineElement implements FeatureElement {
    public static final Codec<LineElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("x2").forGetter(f -> f.x2),
        Codec.INT.fieldOf("y2").forGetter(f -> f.y2),
        Codec.INT.fieldOf("thickness").forGetter(f -> f.thickness),
        Codec.INT.fieldOf("color").forGetter(f -> f.color)
    ).apply(instance, LineElement::new));

    private final int x2;
    private final int y2;
    private final int thickness;
    private final int color;

    public LineElement(int x2, int y2, int thickness, int color) {
        this.x2 = x2;
        this.y2 = y2;
        this.thickness = thickness;
        this.color = color;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, int width, int height, float scale, int mouseX, int mouseY, float partialTicks) {
        // Draw line from (x,y) to (x+x2, y+y2) with given thickness
        int endX = x + x2;
        int endY = y + y2;

        // Simple thick line implementation using filled rectangles
        for (int i = 0; i < thickness; i++) {
            graphics.fill(x, y + i, endX, endY + i, color);
        }
    }

    @Override
    public Codec<? extends FeatureElement> codec() {
        return CODEC;
    }

    public int getX2() {
        return x2;
    }

    public int getY2() {
        return y2;
    }

    public int getThickness() {
        return thickness;
    }

    public int getColor() {
        return color;
    }
}
