package joshie.enchiridion.helpers;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Helper class for Codec-based JSON serialization/deserialization.
 * Replaces GsonHelper with Mojang's Codec system for better type safety and consistency.
 */
public class CodecHelper {

    /**
     * Deserialize a JSON string into an object using the provided Codec
     * @param codec The codec to use for deserialization
     * @param json The JSON string to parse
     * @param <T> The type of object to deserialize to
     * @return The deserialized object, or throws an exception if deserialization fails
     */
    public static <T> T fromJson(Codec<T> codec, String json) {
        JsonElement element = JsonParser.parseString(json);
        DataResult<T> result = codec.parse(JsonOps.INSTANCE, element);
        return result.getOrThrow(false, error -> {
            throw new RuntimeException("Failed to deserialize JSON: " + error);
        });
    }

    /**
     * Serialize an object to a JSON string using the provided Codec
     * @param codec The codec to use for serialization
     * @param object The object to serialize
     * @param <T> The type of object to serialize
     * @return The JSON string representation, pretty-printed
     */
    public static <T> String toJson(Codec<T> codec, T object) {
        DataResult<JsonElement> result = codec.encodeStart(JsonOps.INSTANCE, object);
        JsonElement element = result.getOrThrow(false, error -> {
            throw new RuntimeException("Failed to serialize to JSON: " + error);
        });

        // Pretty print the JSON
        try {
            StringWriter stringWriter = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(stringWriter);
            jsonWriter.setIndent("  ");
            jsonWriter.setLenient(true);
            Streams.write(element, jsonWriter);
            return stringWriter.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write JSON", e);
        }
    }
}
