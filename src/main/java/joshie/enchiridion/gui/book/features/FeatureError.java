package joshie.enchiridion.gui.book.features;

import com.mojang.serialization.Codec;
import joshie.enchiridion.api.book.IFeature;
import joshie.enchiridion.api.book.IFeatureProvider;

public class FeatureError extends joshie.enchiridion.data.book.FeatureProvider {
    public static final Codec<FeatureError> CODEC = Codec.unit(FeatureError::new);

    public FeatureError() {
        super(0, 0, 0, 0);
    }

    @Override
    protected void drawFeature(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        // Error feature doesn't draw anything
    }

    @Override
    public IFeatureProvider copy() {
        return new FeatureError();
    }

    @Override
    public Codec<? extends IFeature> codec() {
        return CODEC;
    }
}