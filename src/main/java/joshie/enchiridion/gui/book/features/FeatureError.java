package joshie.enchiridion.gui.book.features;

import com.mojang.serialization.Codec;
import joshie.enchiridion.data.book.FeatureProvider;

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
    public FeatureProvider copy() {
        return new FeatureError();
    }

    @Override
    public Codec<? extends FeatureProvider> codec() {
        return CODEC;
    }
}