package joshie.enchiridion.gui.book.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.data.book.FeatureProvider;
import joshie.enchiridion.gui.book.GuiSimpleEditor;
import joshie.enchiridion.gui.book.GuiSimpleEditorColor;
import joshie.enchiridion.util.IColorable;
import net.minecraft.client.gui.GuiGraphics;

public class FeatureBox extends joshie.enchiridion.data.book.FeatureProvider implements IColorable {
    public static final Codec<FeatureBox> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.optionalFieldOf("color", "FFFFFFFF").forGetter(f -> f.color)
    ).apply(instance, (color) -> {
        FeatureBox feature = new FeatureBox();
        feature.color = color;
        return feature;
    }));

    public String color;
    public transient int colorI;

    public FeatureBox() {
        super(0, 0, 0, 0);
    }

    public FeatureBox(String color) {
        super(0, 0, 0, 0);
        this.color = color;
    }

    @Override
    public FeatureProvider copy() {
        return new FeatureBox(color);
    }

    @Override
    public String getName() {
        return "Box: " + Integer.toHexString(colorI);
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
    protected void drawFeature(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        guiGraphics.fill(getLeft(), getTop(), getRight(), getBottom(), colorI);
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