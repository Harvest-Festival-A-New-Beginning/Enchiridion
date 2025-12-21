package joshie.enchiridion.gui.book.features;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.data.book.FeatureProvider;
import joshie.enchiridion.gui.book.GuiBook;
import joshie.enchiridion.gui.book.GuiSimpleEditor;
import joshie.enchiridion.gui.book.GuiSimpleEditorColor;
import joshie.enchiridion.util.IColorable;

public class FeatureLine extends FeatureProvider implements IColorable {
    public static final Codec<FeatureLine> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.optionalFieldOf("x2", 0).forGetter(f -> f.x2),
        Codec.INT.optionalFieldOf("y2", 0).forGetter(f -> f.y2),
        Codec.INT.optionalFieldOf("thickness", 1).forGetter(f -> f.thickness),
        Codec.STRING.optionalFieldOf("color", "FF000000").forGetter(f -> f.color)
    ).apply(instance, (x2, y2, thickness, color) -> {
        FeatureLine feature = new FeatureLine();
        feature.x2 = x2;
        feature.y2 = y2;
        feature.thickness = thickness;
        feature.color = color;
        return feature;
    }));

    public int x2; // End X position (relative to page coordinates)
    public int y2; // End Y position (relative to page coordinates)
    public int thickness = 1; // Line thickness in pixels
    public String color;
    public transient int colorI;

    public FeatureLine() {
        super(0, 0, 0, 0);
    }

    public FeatureLine(String color, int thickness) {
        super(0, 0, 0, 0);
        this.color = color;
        this.thickness = thickness;
    }

    @Override
    public FeatureProvider copy() {
        FeatureLine line = new FeatureLine(color, thickness);
        line.x2 = x2;
        line.y2 = y2;
        return line;
    }

    @Override
    public String getName() {
        return "Line: " + Integer.toHexString(colorI);
    }

    @Override
    public boolean getAndSetEditMode(Object gui) {
        GuiBook guiBook = (GuiBook) gui;
        guiBook.getSimpleEditor().setEditor(GuiSimpleEditorColor.INSTANCE.setColorable(this));
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
        // Calculate line parameters
        int x1 = getX();
        int y1 = getY();

        // Calculate length and angle
        double dx = x2 - x1;
        double dy = y2 - y1;
        double length = Math.sqrt(dx * dx + dy * dy);
        double angle = Math.atan2(dy, dx);

        // Use PoseStack to rotate and draw the line as a rectangle
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        // Translate to start point
        poseStack.translate(x1, y1, 0);

        // Rotate to the line angle
        poseStack.mulPose(com.mojang.math.Axis.ZP.rotation((float) angle));

        // Draw the line as a rectangle from (0, -thickness/2) to (length, thickness/2)
        int halfThickness = thickness / 2;
        guiGraphics.fill(0, -halfThickness, (int) length, halfThickness + (thickness % 2), colorI);

        poseStack.popPose();
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
    public Codec<? extends FeatureProvider> codec() {
        return CODEC;
    }
}
