package joshie.enchiridion.json;

import com.google.gson.*;
import joshie.enchiridion.api.book.IFeature;
import joshie.enchiridion.data.book.FeatureProvider;
import joshie.enchiridion.helpers.JSONHelper;

import java.lang.reflect.Type;

/**
 * Legacy JSON adapter for FeatureProvider.
 * NOTE: This is no longer functional as FeatureProvider is now abstract.
 * Use Codec-based serialization instead (see Page.CODEC for polymorphic feature handling).
 * This class is kept for reference but should not be used.
 */
@Deprecated
public class FeatureAbstractAdapter implements JsonDeserializer<FeatureProvider> {
    @Override
    public FeatureProvider deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // FeatureProvider is now abstract and cannot be instantiated
        // Features now extend FeatureProvider directly and are serialized via Codecs
        throw new UnsupportedOperationException(
            "FeatureProvider is abstract and cannot be deserialized via GSON. " +
            "Use Codec-based serialization instead."
        );
    }
}