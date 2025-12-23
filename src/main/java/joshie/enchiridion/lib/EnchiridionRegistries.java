package joshie.enchiridion.lib;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.Enchiridion;
import joshie.enchiridion.data.book.Book;
import joshie.enchiridion.data.book.FeatureProvider;
import joshie.enchiridion.data.book.Template;
import joshie.enchiridion.data.library.ModdedBook;
import joshie.enchiridion.gui.book.element.FeatureElement;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.data.PenguinRegistries;
import uk.joshiejack.penguinlib.util.icon.*;
import uk.joshiejack.penguinlib.util.registry.ReloadableRegistry;

import static joshie.enchiridion.lib.EInfo.MODID;

public class EnchiridionRegistries {
    // Reloadable registry for books - will load from data/<modid>/books/<book_name>.json
    public static final ReloadableRegistry<Book> BOOKS =
            new ReloadableRegistry<>(MODID, "books", Book.CODEC, new Book(), true);

    // Reloadable registry for modded book entries - will load from data/<modid>/modded_books/<entry_name>.json
    public static final ReloadableRegistry<ModdedBook> MODDED_BOOKS =
            new ReloadableRegistry<>(MODID, "modded_books", ModdedBook.CODEC, new ModdedBook(), true);

    // Reloadable registry for templates - will load from data/<modid>/templates/<template_name>.json
    public static final ReloadableRegistry<Template> TEMPLATES =
            new ReloadableRegistry<>(MODID, "templates", Template.CODEC, new Template(), true);

    public static class Elements {
        public static final DeferredRegister<Codec<? extends FeatureElement>> ELEMENT_TYPES = DeferredRegister.create(ResourceKey.createRegistryKey(new ResourceLocation(EInfo.MODID, "elements")), EInfo.MODID);
        public static final Registry<Codec<? extends FeatureElement>> ELEMENTS = ELEMENT_TYPES.makeRegistry(b -> b.sync(true));

        // Element registrations - pure rendering elements
        public static final Holder<Codec<? extends FeatureElement>> TEXT = ELEMENT_TYPES.register("text", () -> joshie.enchiridion.gui.book.element.TextElement.CODEC);
        public static final Holder<Codec<? extends FeatureElement>> BOX = ELEMENT_TYPES.register("box", () -> joshie.enchiridion.gui.book.element.BoxElement.CODEC);
        public static final Holder<Codec<? extends FeatureElement>> LINE = ELEMENT_TYPES.register("line", () -> joshie.enchiridion.gui.book.element.LineElement.CODEC);
        public static final Holder<Codec<? extends FeatureElement>> ICON = ELEMENT_TYPES.register("icon", () -> joshie.enchiridion.gui.book.element.IconElement.CODEC);
        public static final Holder<Codec<? extends FeatureElement>> ITEM = ELEMENT_TYPES.register("item", () -> joshie.enchiridion.gui.book.element.ItemElement.CODEC);
        public static final Holder<Codec<? extends FeatureElement>> IMAGE = ELEMENT_TYPES.register("image", () -> joshie.enchiridion.gui.book.element.ImageElement.CODEC);
        public static final Holder<Codec<? extends FeatureElement>> SHAPE = ELEMENT_TYPES.register("shape", () -> joshie.enchiridion.gui.book.element.ShapeElement.CODEC);
        public static final Holder<Codec<? extends FeatureElement>> BUTTON = ELEMENT_TYPES.register("button", () -> joshie.enchiridion.gui.book.element.ButtonElement.CODEC);
        public static final Holder<Codec<? extends FeatureElement>> ERROR = ELEMENT_TYPES.register("error", () -> joshie.enchiridion.gui.book.element.ErrorElement.CODEC);
        public static final Holder<Codec<? extends FeatureElement>> EMPTY = ELEMENT_TYPES.register("empty", () -> joshie.enchiridion.gui.book.element.EmptyElement.CODEC);
        public static final Holder<Codec<? extends FeatureElement>> FLUID = ELEMENT_TYPES.register("fluid", () -> joshie.enchiridion.gui.book.element.FluidElement.CODEC);
    }

    public static class Features {
        public static final DeferredRegister<Codec<? extends FeatureProvider>> FEATURE_TYPES = DeferredRegister.create(ResourceKey.createRegistryKey(new ResourceLocation(EInfo.MODID, "features")), EInfo.MODID);
        public static final Registry<Codec<? extends FeatureProvider>> FEATURES = FEATURE_TYPES.makeRegistry(b -> b.sync(true));

        // Simple features - create FeatureProvider with element directly
        public static final Holder<Codec<? extends FeatureProvider>> BOX = FEATURE_TYPES.register("box", () ->
            RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.optionalFieldOf("color", "FFFFFFFF").forGetter(f -> {
                    if (f.element instanceof joshie.enchiridion.gui.book.element.BoxElement) {
                        return Integer.toHexString(((joshie.enchiridion.gui.book.element.BoxElement)f.element).getColor());
                    }
                    return "FFFFFFFF";
                })
            ).apply(instance, (color) -> {
                int colorI = (int) Long.parseLong(color, 16);
                return new FeatureProvider(new joshie.enchiridion.gui.book.element.BoxElement(colorI), 0, 0, 0, 0);
            })));

        public static final Holder<Codec<? extends FeatureProvider>> LINE = FEATURE_TYPES.register("line", () ->
            RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.optionalFieldOf("x2", 0).forGetter(f -> f.element instanceof joshie.enchiridion.gui.book.element.LineElement ? ((joshie.enchiridion.gui.book.element.LineElement)f.element).getX2() : 0),
                Codec.INT.optionalFieldOf("y2", 0).forGetter(f -> f.element instanceof joshie.enchiridion.gui.book.element.LineElement ? ((joshie.enchiridion.gui.book.element.LineElement)f.element).getY2() : 0),
                Codec.INT.optionalFieldOf("thickness", 1).forGetter(f -> f.element instanceof joshie.enchiridion.gui.book.element.LineElement ? ((joshie.enchiridion.gui.book.element.LineElement)f.element).getThickness() : 1),
                Codec.STRING.optionalFieldOf("color", "FF000000").forGetter(f -> f.element instanceof joshie.enchiridion.gui.book.element.LineElement ? Integer.toHexString(((joshie.enchiridion.gui.book.element.LineElement)f.element).getColor()) : "FF000000")
            ).apply(instance, (x2, y2, thickness, color) -> {
                int colorI = (int) Long.parseLong(color, 16);
                return new FeatureProvider(new joshie.enchiridion.gui.book.element.LineElement(x2, y2, thickness, colorI), 0, 0, 0, 0);
            })));

        public static final Holder<Codec<? extends FeatureProvider>> ICON = FEATURE_TYPES.register("icon", () ->
            RecordCodecBuilder.create(instance -> instance.group(
                uk.joshiejack.penguinlib.util.icon.Icon.CODEC.fieldOf("icon").forGetter(f -> f.element instanceof joshie.enchiridion.gui.book.element.IconElement ? ((joshie.enchiridion.gui.book.element.IconElement)f.element).getIcon() : uk.joshiejack.penguinlib.util.icon.ItemIcon.EMPTY)
            ).apply(instance, (icon) -> new FeatureProvider(new joshie.enchiridion.gui.book.element.IconElement(icon), 0, 0, 0, 0))));

        public static final Holder<Codec<? extends FeatureProvider>> SHAPE = FEATURE_TYPES.register("shape", () ->
            RecordCodecBuilder.create(instance -> instance.group(
                net.minecraft.util.StringRepresentable.fromEnum(joshie.enchiridion.gui.book.element.ShapeElement.Shape::values).optionalFieldOf("shape", joshie.enchiridion.gui.book.element.ShapeElement.Shape.RECTANGLE).forGetter(f -> joshie.enchiridion.gui.book.element.ShapeElement.Shape.RECTANGLE),
                Codec.STRING.optionalFieldOf("color", "FF000000").forGetter(f -> "FF000000"),
                Codec.BOOL.optionalFieldOf("filled", true).forGetter(f -> true)
            ).apply(instance, (shape, color, filled) -> {
                int colorI = (int) Long.parseLong(color, 16);
                return new FeatureProvider(new joshie.enchiridion.gui.book.element.ShapeElement(shape, colorI, filled), 0, 0, 0, 0);
            })));

        public static final Holder<Codec<? extends FeatureProvider>> ERROR = FEATURE_TYPES.register("error", () ->
            RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.optionalFieldOf("error", "Error").forGetter(f -> "Error")
            ).apply(instance, (error) -> new FeatureProvider(new joshie.enchiridion.gui.book.element.ErrorElement(error), 0, 0, 0, 0))));

        public static final Holder<Codec<? extends FeatureProvider>> FLUID = FEATURE_TYPES.register("fluid", () ->
            RecordCodecBuilder.create(instance -> instance.group(
                net.minecraft.resources.ResourceLocation.CODEC.fieldOf("fluid").forGetter(f -> new net.minecraft.resources.ResourceLocation("minecraft:water")),
                Codec.INT.optionalFieldOf("amount", 1000).forGetter(f -> 1000)
            ).apply(instance, (fluid, amount) -> new FeatureProvider(new joshie.enchiridion.gui.book.element.FluidElement(fluid, amount), 0, 0, 0, 0))));

        public static final Holder<Codec<? extends FeatureProvider>> ITEM = FEATURE_TYPES.register("item", () ->
            RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.optionalFieldOf("item_string", "").forGetter(f -> "")
            ).apply(instance, (itemString) -> new FeatureProvider(new joshie.enchiridion.gui.book.element.ItemElement(itemString), 0, 0, 0, 0))));

        public static final Holder<Codec<? extends FeatureProvider>> IMAGE = FEATURE_TYPES.register("image", () ->
            RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.optionalFieldOf("path", "").forGetter(f -> "")
            ).apply(instance, (path) -> {
                net.minecraft.resources.ResourceLocation resource = new net.minecraft.resources.ResourceLocation(path);
                return new FeatureProvider(new joshie.enchiridion.gui.book.element.ImageElement(resource), 0, 0, 0, 0);
            })));

        // Non-visual features - use EmptyElement
        public static final Holder<Codec<? extends FeatureProvider>> SOUND = FEATURE_TYPES.register("sound", () ->
            Codec.unit(() -> new FeatureProvider(joshie.enchiridion.gui.book.element.EmptyElement.INSTANCE, 0, 0, 0, 0)));

        public static final Holder<Codec<? extends FeatureProvider>> JUMP = FEATURE_TYPES.register("jump", () ->
            Codec.unit(() -> new FeatureProvider(joshie.enchiridion.gui.book.element.EmptyElement.INSTANCE, 0, 0, 0, 0)));

        public static final Holder<Codec<? extends FeatureProvider>> TEXT = FEATURE_TYPES.register("text", () ->
            RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.optionalFieldOf("text", "").forGetter(f -> f.element instanceof joshie.enchiridion.gui.book.element.TextElement ? ((joshie.enchiridion.gui.book.element.TextElement)f.element).getText() : ""),
                Codec.FLOAT.optionalFieldOf("size", 1F).forGetter(f -> f.element instanceof joshie.enchiridion.gui.book.element.TextElement ? ((joshie.enchiridion.gui.book.element.TextElement)f.element).getSize() : 1F),
                Codec.INT.optionalFieldOf("color", 0x555555).forGetter(f -> f.element instanceof joshie.enchiridion.gui.book.element.TextElement ? ((joshie.enchiridion.gui.book.element.TextElement)f.element).getColor() : 0x555555)
            ).apply(instance, (text, size, color) -> new FeatureProvider(new joshie.enchiridion.gui.book.element.TextElement(text, size, color), 0, 0, 0, 0))));

        // Complex features - now use element-based codecs (Feature* classes deleted)
        public static final Holder<Codec<? extends FeatureProvider>> BUTTON = FEATURE_TYPES.register("button", () ->
            RecordCodecBuilder.create(instance -> instance.group(
                joshie.enchiridion.gui.book.element.ActionButtonElement.CODEC.fieldOf("element").forGetter(f -> (joshie.enchiridion.gui.book.element.ActionButtonElement) f.element)
            ).apply(instance, (element) -> new FeatureProvider(element, 0, 0, 18, 10))));

        public static final Holder<Codec<? extends FeatureProvider>> RECIPE = FEATURE_TYPES.register("recipe", () ->
            RecordCodecBuilder.create(instance -> instance.group(
                joshie.enchiridion.gui.book.element.RecipeElement.CODEC.fieldOf("element").forGetter(f -> (joshie.enchiridion.gui.book.element.RecipeElement) f.element)
            ).apply(instance, (element) -> new FeatureProvider(element, 0, 0, 160, 80))));

        public static final Holder<Codec<? extends FeatureProvider>> PREVIEW_WINDOW = FEATURE_TYPES.register("preview_window", () ->
            RecordCodecBuilder.create(instance -> instance.group(
                joshie.enchiridion.gui.book.element.PreviewWindowElement.CODEC.fieldOf("element").forGetter(f -> (joshie.enchiridion.gui.book.element.PreviewWindowElement) f.element)
            ).apply(instance, (element) -> new FeatureProvider(element, 0, 0, 100, 100))));

        public static final Holder<Codec<? extends FeatureProvider>> ENTITY = FEATURE_TYPES.register("entity", () ->
            RecordCodecBuilder.create(instance -> instance.group(
                joshie.enchiridion.gui.book.element.EntityElement.CODEC.fieldOf("element").forGetter(f -> (joshie.enchiridion.gui.book.element.EntityElement) f.element)
            ).apply(instance, (element) -> new FeatureProvider(element, 0, 0, 64, 64))));

        public static final Holder<Codec<? extends FeatureProvider>> MODEL = FEATURE_TYPES.register("model", () ->
            RecordCodecBuilder.create(instance -> instance.group(
                joshie.enchiridion.gui.book.element.ModelElement.CODEC.fieldOf("element").forGetter(f -> (joshie.enchiridion.gui.book.element.ModelElement) f.element)
            ).apply(instance, (element) -> new FeatureProvider(element, 0, 0, 64, 64))));

        public static final Holder<Codec<? extends FeatureProvider>> JS = FEATURE_TYPES.register("js", () ->
            RecordCodecBuilder.create(instance -> instance.group(
                joshie.enchiridion.gui.book.element.JSElement.CODEC.fieldOf("element").forGetter(f -> (joshie.enchiridion.gui.book.element.JSElement) f.element)
            ).apply(instance, (element) -> new FeatureProvider(element, 0, 0, 200, 80))));

        public static final Holder<Codec<? extends FeatureProvider>> RESOURCE = FEATURE_TYPES.register("resource", () ->
            RecordCodecBuilder.create(instance -> instance.group(
                joshie.enchiridion.gui.book.element.ResourceElement.CODEC.fieldOf("element").forGetter(f -> (joshie.enchiridion.gui.book.element.ResourceElement) f.element)
            ).apply(instance, (element) -> new FeatureProvider(element, 0, 0, 100, 100))));
    }

    public static void register(IEventBus eventBus) {
        Elements.ELEMENT_TYPES.register(eventBus);
        Features.FEATURE_TYPES.register(eventBus);
    }
}