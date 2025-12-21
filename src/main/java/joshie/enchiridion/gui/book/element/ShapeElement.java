package joshie.enchiridion.gui.book.element;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.StringRepresentable;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ShapeElement implements FeatureElement {
    public enum Shape implements StringRepresentable {
        RECTANGLE("rectangle"),
        CIRCLE("circle"),
        TRIANGLE("triangle");

        private final String name;
        Shape(String name) { this.name = name; }

        @Override
        public String getSerializedName() { return name; }
    }

    public static final Codec<ShapeElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        StringRepresentable.fromEnum(Shape::values).optionalFieldOf("shape", Shape.RECTANGLE).forGetter(f -> f.shape),
        Codec.INT.fieldOf("color").forGetter(f -> f.color),
        Codec.BOOL.optionalFieldOf("filled", true).forGetter(f -> f.filled)
    ).apply(instance, ShapeElement::new));

    private final Shape shape;
    private final int color;
    private final boolean filled;

    public ShapeElement(Shape shape, int color, boolean filled) {
        this.shape = shape;
        this.color = color;
        this.filled = filled;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, int width, int height, float scale, int mouseX, int mouseY, float partialTicks) {
        if (filled) {
            switch (shape) {
                case RECTANGLE -> graphics.fill(x, y, x + width, y + height, color);
                case CIRCLE -> drawCircle(graphics, x + width/2, y + height/2, Math.min(width, height)/2, color, true);
                case TRIANGLE -> drawTriangle(graphics, x, y, width, height, color, true);
            }
        } else {
            switch (shape) {
                case RECTANGLE -> {
                    graphics.fill(x, y, x + width, y + 1, color); // top
                    graphics.fill(x, y + height - 1, x + width, y + height, color); // bottom
                    graphics.fill(x, y, x + 1, y + height, color); // left
                    graphics.fill(x + width - 1, y, x + width, y + height, color); // right
                }
                case CIRCLE -> drawCircle(graphics, x + width/2, y + height/2, Math.min(width, height)/2, color, false);
                case TRIANGLE -> drawTriangle(graphics, x, y, width, height, color, false);
            }
        }
    }

    private void drawCircle(GuiGraphics graphics, int centerX, int centerY, int radius, int color, boolean filled) {
        // Simple circle drawing using Bresenham-style algorithm
        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                if (filled) {
                    if (dx * dx + dy * dy <= radius * radius) {
                        graphics.fill(centerX + dx, centerY + dy, centerX + dx + 1, centerY + dy + 1, color);
                    }
                } else {
                    int dist = dx * dx + dy * dy;
                    if (dist >= (radius - 1) * (radius - 1) && dist <= radius * radius) {
                        graphics.fill(centerX + dx, centerY + dy, centerX + dx + 1, centerY + dy + 1, color);
                    }
                }
            }
        }
    }

    private void drawTriangle(GuiGraphics graphics, int x, int y, int width, int height, int color, boolean filled) {
        int topX = x + width / 2;
        int topY = y;
        int leftX = x;
        int leftY = y + height;
        int rightX = x + width;
        int rightY = y + height;

        if (filled) {
            // Fill triangle scanline by scanline
            for (int scanY = 0; scanY < height; scanY++) {
                int leftEdge = x + (scanY * width) / (height * 2);
                int rightEdge = x + width - (scanY * width) / (height * 2);
                graphics.fill(leftEdge, y + scanY, rightEdge, y + scanY + 1, color);
            }
        } else {
            // Draw triangle outline (simple line approximation)
            drawLine(graphics, topX, topY, leftX, leftY, color);
            drawLine(graphics, topX, topY, rightX, rightY, color);
            drawLine(graphics, leftX, leftY, rightX, rightY, color);
        }
    }

    private void drawLine(GuiGraphics graphics, int x1, int y1, int x2, int y2, int color) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            graphics.fill(x1, y1, x1 + 1, y1 + 1, color);
            if (x1 == x2 && y1 == y2) break;
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }

    @Override
    public Codec<? extends FeatureElement> codec() {
        return CODEC;
    }
}
