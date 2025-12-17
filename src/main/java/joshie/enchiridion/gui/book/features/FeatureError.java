package joshie.enchiridion.gui.book.features;

import com.mojang.serialization.Codec;
import joshie.enchiridion.api.book.IFeature;

public class FeatureError extends FeatureAbstract {
    public static final Codec<FeatureError> CODEC = Codec.unit(FeatureError::new);

    public FeatureError() {
    }

    @Override
    public IFeature copy() {
        return new FeatureError();
    }
}