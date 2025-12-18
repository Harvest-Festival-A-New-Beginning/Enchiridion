package joshie.enchiridion.json;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class BookIconTemplate {
    public static final Codec<BookIconTemplate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("parent").forGetter(t -> t.parent),
        Icons.CODEC.fieldOf("textures").forGetter(t -> t.textures)
    ).apply(instance, (parent, textures) -> {
        BookIconTemplate template = new BookIconTemplate();
        template.parent = parent;
        template.textures = textures;
        return template;
    }));

    public String parent;
    public Icons textures;

    public static class Icons {
        public static final Codec<Icons> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("layer0").forGetter(i -> i.layer0)
        ).apply(instance, layer0 -> {
            Icons icons = new Icons();
            icons.layer0 = layer0;
            return icons;
        }));

        public String layer0;
    }
}