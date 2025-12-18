package joshie.enchiridion.gui.book.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.api.book.IFeature;
import joshie.enchiridion.data.book.FeatureProvider;
import joshie.enchiridion.gui.book.GuiSimpleEditor;
import joshie.enchiridion.gui.book.GuiSimpleEditorIcon;
import net.minecraft.client.gui.GuiGraphics;
import uk.joshiejack.penguinlib.util.icon.Icon;

import java.util.List;

/**
 * Feature that displays an Icon from Penguin-Lib.
 * Icons can represent items, textures, or solid colors.
 * More flexible than FeatureItem.
 */
public class FeatureIcon extends FeatureProvider {
    public static final Codec<FeatureIcon> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Icon.CODEC.fieldOf("icon").forGetter(f -> f.icon),
        Codec.BOOL.optionalFieldOf("hide_tooltip", false).forGetter(f -> f.hideTooltip),
        Codec.FLOAT.optionalFieldOf("scale", 1.0F).forGetter(f -> f.scale)
    ).apply(instance, (icon, hideTooltip, scale) -> {
        FeatureIcon feature = new FeatureIcon();
        feature.icon = icon;
        feature.hideTooltip = hideTooltip;
        feature.scale = scale;
        return feature;
    }));

    private Icon icon = Icon.EMPTY;
    private boolean hideTooltip = false;
    private float scale = 1.0F;

    public FeatureIcon() {
        super(0, 0, 0, 0);
    }

    public FeatureIcon(Icon icon) {
        super(0, 0, 0, 0);
        this.icon = icon;
    }

    @Override
    public FeatureProvider copy() {
        FeatureIcon copy = new FeatureIcon(icon);
        copy.hideTooltip = hideTooltip;
        copy.scale = scale;
        return copy;
    }

    @Override
    public String getName() {
        return icon != null && !icon.isEmpty() ? "Icon" : super.getName();
    }

    @Override
    public void update(joshie.enchiridion.api.book.IPage page) {
        super.update(page);
        int width = getWidth();
        setHeight(width);
        scale = (float) (width / 16D);
    }

    @Override
    protected void drawFeature(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (icon != null && !icon.isEmpty()) {
            // Icon.render expects: GuiGraphics, x, y, width, height
            icon.render(guiGraphics, getLeft(), getTop(), getWidth(), getHeight());
        }
    }

    @Override
    public void addTooltip(List<String> list, int mouseX, int mouseY) {
        if (!hideTooltip && icon != null && !icon.isEmpty()) {
            // Get tooltip from icon if available
            // TODO: Icon tooltip API - check Penguin-Lib for tooltip method
        }
    }

    @Override
    public boolean getAndSetEditMode() {
        // TODO: Create GuiSimpleEditorIcon for icon selection
        // GuiSimpleEditor.INSTANCE.setEditor(GuiSimpleEditorIcon.INSTANCE.setIcon(this));
        return false;
    }

    // ===== Getters/Setters for editor =====

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public boolean isHideTooltip() {
        return hideTooltip;
    }

    public void setHideTooltip(boolean hideTooltip) {
        this.hideTooltip = hideTooltip;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    @Override
    public Codec<? extends IFeature> codec() {
        return CODEC;
    }
}
