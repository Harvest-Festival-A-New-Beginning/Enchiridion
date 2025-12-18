package joshie.enchiridion.json;

import com.google.gson.*;
import joshie.enchiridion.api.book.FeatureProvider;
import joshie.enchiridion.data.book.FeatureProvider;

import java.lang.reflect.Type;

public class FeatureProviderAbstractAdapter implements JsonSerializer<FeatureProvider>, JsonDeserializer<FeatureProvider> {
    @Override
    public JsonElement serialize(FeatureProvider src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src, FeatureProvider.class);
    }

    @Override
    public FeatureProvider deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return context.deserialize(json, FeatureProvider.class);
    }
}