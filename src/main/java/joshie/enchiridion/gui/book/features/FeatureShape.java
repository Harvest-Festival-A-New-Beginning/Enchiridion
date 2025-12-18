package joshie.enchiridion.gui.book.features;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.api.book.IFeature;
import joshie.enchiridion.data.book.FeatureProvider;
import joshie.enchiridion.gui.book.GuiSimpleEditor;
import joshie.enchiridion.gui.book.GuiSimpleEditorColor;
import joshie.enchiridion.util.IColorable;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.StringRepresentable;
import org.joml.Matrix4f;

public class FeatureShape extends FeatureProvider implements IColorable {
    public enum ShapeType implements StringRepresentable {
        CIRCLE("circle", 32),
        TRIANGLE("triangle", 3),
        SQUARE("square", 4),
        PENTAGON("pentagon", 5),
        HEXAGON("hexagon", 6),
        HEPTAGON("heptagon", 7),
        OCTAGON("octagon", 8),
        STAR_4("star_4", 4),
        STAR_5("star_5", 5),
        STAR_6("star_6", 6),
        STAR_8("star_8", 8);

        private final String name;
        private final int points;

        ShapeType(String name, int points) {
            this.name = name;
            this.points = points;
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        public int getPoints() {
            return points;
        }

        public boolean isStar() {
            return name.startsWith("star_");
        }
    }

    public static final Codec<FeatureShape> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        StringRepresentable.fromEnum(ShapeType::values).fieldOf("shape_type").forGetter(f -> f.shapeType),
        Codec.BOOL.optionalFieldOf("filled", true).forGetter(f -> f.filled),
        Codec.FLOAT.optionalFieldOf("rotation", 0.0F).forGetter(f -> f.rotation),
        Codec.STRING.optionalFieldOf("color", "FFFFFFFF").forGetter(f -> f.color)
    ).apply(instance, (shapeType, filled, rotation, color) -> {
        FeatureShape feature = new FeatureShape();
        feature.shapeType = shapeType;
        feature.filled = filled;
        feature.rotation = rotation;
        feature.color = color;
        return feature;
    }));

    public ShapeType shapeType = ShapeType.CIRCLE;
    public boolean filled = true;
    public float rotation = 0.0F; // Rotation in degrees
    public String color;
    public transient int colorI;

    public FeatureShape() {
        super(0, 0, 0, 0);
    }

    public FeatureShape(ShapeType shapeType, String color) {
        super(0, 0, 0, 0);
        this.shapeType = shapeType;
        this.color = color;
    }

    @Override
    public FeatureProvider copy() {
        FeatureShape shape = new FeatureShape(shapeType, color);
        shape.filled = filled;
        shape.rotation = rotation;
        return shape;
    }

    @Override
    public String getName() {
        return "Shape: " + shapeType.getSerializedName();
    }

    @Override
    public boolean getAndSetEditMode() {
        GuiSimpleEditor.INSTANCE.setEditor(GuiSimpleEditorColor.INSTANCE.setColorable(this));
        return false;
    }

    private boolean attemptToParseColor() {
        int previousColor = this.colorI;
        if (!attemptToParseString(color)) {
            String doubled = color.replaceAll(".", "$0$0");
            if (!attemptToParseString(doubled)) {
                if (!attemptToParseString(doubled.replace("#", ""))) {
                    this.colorI = previousColor;
                    return false;
                } else return true;
            } else return true;
        } else return true;
    }

    private boolean attemptToParseString(String string) {
        try {
            colorI = (int) Long.parseLong(string, 16);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void update(joshie.enchiridion.api.book.IPage page) {
        super.update(page);
        attemptToParseColor();
    }

    @Override
    protected void drawFeature(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        // Calculate center and radius
        int centerX = (getLeft() + getRight()) / 2;
        int centerY = (getTop() + getBottom()) / 2;
        float radiusX = (getRight() - getLeft()) / 2.0F;
        float radiusY = (getBottom() - getTop()) / 2.0F;

        // Extract color components
        float a = ((colorI >> 24) & 0xFF) / 255.0F;
        float r = ((colorI >> 16) & 0xFF) / 255.0F;
        float g = ((colorI >> 8) & 0xFF) / 255.0F;
        float b = (colorI & 0xFF) / 255.0F;

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        // Translate to center for rotation
        poseStack.translate(centerX, centerY, 0);
        if (rotation != 0) {
            poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(rotation));
        }

        // Set up rendering
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Matrix4f matrix = poseStack.last().pose();
        VertexConsumer consumer = guiGraphics.bufferSource().getBuffer(RenderType.gui());

        if (filled) {
            drawFilledShape(consumer, matrix, radiusX, radiusY, r, g, b, a);
        } else {
            drawOutlineShape(consumer, matrix, radiusX, radiusY, r, g, b, a);
        }

        guiGraphics.flush();
        RenderSystem.disableBlend();

        poseStack.popPose();
    }

    private void drawFilledShape(VertexConsumer consumer, Matrix4f matrix, float radiusX, float radiusY, float r, float g, float b, float a) {
        int points = shapeType.getPoints();

        if (shapeType.isStar()) {
            // Draw star as triangles from center
            float innerRadius = shapeType == ShapeType.STAR_4 ? 0.4F : 0.5F;
            for (int i = 0; i < points * 2; i++) {
                float angle1 = (float) (Math.PI * 2 * i / (points * 2));
                float angle2 = (float) (Math.PI * 2 * (i + 1) / (points * 2));

                float radius1 = (i % 2 == 0) ? 1.0F : innerRadius;
                float radius2 = ((i + 1) % 2 == 0) ? 1.0F : innerRadius;

                float x1 = (float) Math.cos(angle1 - Math.PI / 2) * radiusX * radius1;
                float y1 = (float) Math.sin(angle1 - Math.PI / 2) * radiusY * radius1;
                float x2 = (float) Math.cos(angle2 - Math.PI / 2) * radiusX * radius2;
                float y2 = (float) Math.sin(angle2 - Math.PI / 2) * radiusY * radius2;

                // Triangle fan from center
                consumer.vertex(matrix, 0, 0, 0).color(r, g, b, a).endVertex();
                consumer.vertex(matrix, x1, y1, 0).color(r, g, b, a).endVertex();
                consumer.vertex(matrix, x2, y2, 0).color(r, g, b, a).endVertex();
            }
        } else {
            // Draw regular polygon as triangle fan from center
            for (int i = 0; i < points; i++) {
                float angle1 = (float) (Math.PI * 2 * i / points);
                float angle2 = (float) (Math.PI * 2 * (i + 1) / points);

                float x1 = (float) Math.cos(angle1 - Math.PI / 2) * radiusX;
                float y1 = (float) Math.sin(angle1 - Math.PI / 2) * radiusY;
                float x2 = (float) Math.cos(angle2 - Math.PI / 2) * radiusX;
                float y2 = (float) Math.sin(angle2 - Math.PI / 2) * radiusY;

                // Triangle from center to edge points
                consumer.vertex(matrix, 0, 0, 0).color(r, g, b, a).endVertex();
                consumer.vertex(matrix, x1, y1, 0).color(r, g, b, a).endVertex();
                consumer.vertex(matrix, x2, y2, 0).color(r, g, b, a).endVertex();
            }
        }
    }

    private void drawOutlineShape(VertexConsumer consumer, Matrix4f matrix, float radiusX, float radiusY, float r, float g, float b, float a) {
        int points = shapeType.getPoints();
        float lineWidth = 1.0F;

        if (shapeType.isStar()) {
            // Draw star outline
            float innerRadius = shapeType == ShapeType.STAR_4 ? 0.4F : 0.5F;
            for (int i = 0; i < points * 2; i++) {
                float angle1 = (float) (Math.PI * 2 * i / (points * 2));
                float angle2 = (float) (Math.PI * 2 * (i + 1) / (points * 2));

                float radius1 = (i % 2 == 0) ? 1.0F : innerRadius;
                float radius2 = ((i + 1) % 2 == 0) ? 1.0F : innerRadius;

                float x1 = (float) Math.cos(angle1 - Math.PI / 2) * radiusX * radius1;
                float y1 = (float) Math.sin(angle1 - Math.PI / 2) * radiusY * radius1;
                float x2 = (float) Math.cos(angle2 - Math.PI / 2) * radiusX * radius2;
                float y2 = (float) Math.sin(angle2 - Math.PI / 2) * radiusY * radius2;

                drawLine(consumer, matrix, x1, y1, x2, y2, lineWidth, r, g, b, a);
            }
        } else {
            // Draw regular polygon outline
            for (int i = 0; i < points; i++) {
                float angle1 = (float) (Math.PI * 2 * i / points);
                float angle2 = (float) (Math.PI * 2 * (i + 1) / points);

                float x1 = (float) Math.cos(angle1 - Math.PI / 2) * radiusX;
                float y1 = (float) Math.sin(angle1 - Math.PI / 2) * radiusY;
                float x2 = (float) Math.cos(angle2 - Math.PI / 2) * radiusX;
                float y2 = (float) Math.sin(angle2 - Math.PI / 2) * radiusY;

                drawLine(consumer, matrix, x1, y1, x2, y2, lineWidth, r, g, b, a);
            }
        }
    }

    private void drawLine(VertexConsumer consumer, Matrix4f matrix, float x1, float y1, float x2, float y2, float width, float r, float g, float b, float a) {
        // Calculate perpendicular offset for line thickness
        float dx = x2 - x1;
        float dy = y2 - y1;
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        if (length == 0) return;

        float perpX = -dy / length * width / 2;
        float perpY = dx / length * width / 2;

        // Draw line as a thin rectangle
        consumer.vertex(matrix, x1 - perpX, y1 - perpY, 0).color(r, g, b, a).endVertex();
        consumer.vertex(matrix, x1 + perpX, y1 + perpY, 0).color(r, g, b, a).endVertex();
        consumer.vertex(matrix, x2 + perpX, y2 + perpY, 0).color(r, g, b, a).endVertex();

        consumer.vertex(matrix, x1 - perpX, y1 - perpY, 0).color(r, g, b, a).endVertex();
        consumer.vertex(matrix, x2 + perpX, y2 + perpY, 0).color(r, g, b, a).endVertex();
        consumer.vertex(matrix, x2 - perpX, y2 - perpY, 0).color(r, g, b, a).endVertex();
    }

    @Override
    public String getColorAsHex() {
        return color;
    }

    @Override
    public void setColorAsHex(String color) {
        String previous = this.color;
        this.color = color;
        if (attemptToParseColor()) {
            this.color = color;
        } else this.color = previous;
    }

    @Override
    public Codec<? extends IFeature> codec() {
        return CODEC;
    }
}
