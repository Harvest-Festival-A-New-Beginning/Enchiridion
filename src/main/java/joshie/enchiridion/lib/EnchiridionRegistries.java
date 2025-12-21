package joshie.enchiridion.lib;

import com.mojang.serialization.Codec;
import joshie.enchiridion.Enchiridion;
import joshie.enchiridion.data.book.Book;
import joshie.enchiridion.data.book.FeatureProvider;
import joshie.enchiridion.data.book.Template;
import joshie.enchiridion.data.library.ModdedBook;
import joshie.enchiridion.gui.book.element.FeatureElement;
import joshie.enchiridion.gui.book.features.*;
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

        // Element registrations will be added here as features are converted
        // Example: public static final Holder<Codec<? extends FeatureElement>> TEXT = ELEMENT_TYPES.register("text", () -> TextElement.CODEC);
    }

    @Deprecated // Legacy feature system - being replaced by Elements registry
    public static class Features {
        public static final DeferredRegister<Codec<? extends FeatureProvider>> FEATURE_TYPES = DeferredRegister.create(ResourceKey.createRegistryKey(new ResourceLocation(EInfo.MODID, "features")), EInfo.MODID);
        public static final Registry<Codec<? extends FeatureProvider>> FEATURES = FEATURE_TYPES.makeRegistry(b -> b.sync(true));
        public static final Holder<Codec<? extends FeatureProvider>> TEXT = FEATURE_TYPES.register("text", () -> FeatureText.CODEC);
        public static final Holder<Codec<? extends FeatureProvider>> IMAGE = FEATURE_TYPES.register("image", () -> FeatureImage.CODEC);
        public static final Holder<Codec<? extends FeatureProvider>> ITEM = FEATURE_TYPES.register("item", () -> FeatureItem.CODEC);
        public static final Holder<Codec<? extends FeatureProvider>> ICON = FEATURE_TYPES.register("icon", () -> FeatureIcon.CODEC);
        public static final Holder<Codec<? extends FeatureProvider>> RECIPE = FEATURE_TYPES.register("recipe", () -> FeatureRecipe.CODEC);
        public static final Holder<Codec<? extends FeatureProvider>> BUTTON = FEATURE_TYPES.register("button", () -> FeatureButton.CODEC);
        public static final Holder<Codec<? extends FeatureProvider>> BOX = FEATURE_TYPES.register("box", () -> FeatureBox.CODEC);
        public static final Holder<Codec<? extends FeatureProvider>> LINE = FEATURE_TYPES.register("line", () -> FeatureLine.CODEC);
        public static final Holder<Codec<? extends FeatureProvider>> SHAPE = FEATURE_TYPES.register("shape", () -> FeatureShape.CODEC);
        public static final Holder<Codec<? extends FeatureProvider>> ENTITY = FEATURE_TYPES.register("entity", () -> FeatureEntity.CODEC);
        public static final Holder<Codec<? extends FeatureProvider>> JS = FEATURE_TYPES.register("js", () -> FeatureJS.CODEC);
        public static final Holder<Codec<? extends FeatureProvider>> FLUID = FEATURE_TYPES.register("fluid", () -> FeatureFluid.CODEC);
        public static final Holder<Codec<? extends FeatureProvider>> MODEL = FEATURE_TYPES.register("model", () -> FeatureModel.CODEC);
        public static final Holder<Codec<? extends FeatureProvider>> SOUND = FEATURE_TYPES.register("sound", () -> FeatureSound.CODEC);
        public static final Holder<Codec<? extends FeatureProvider>> PREVIEW_WINDOW = FEATURE_TYPES.register("preview_window", () -> FeaturePreviewWindow.CODEC);
    }

    public static void register(IEventBus eventBus) {
        Elements.ELEMENT_TYPES.register(eventBus);
        Features.FEATURE_TYPES.register(eventBus); // Keep for legacy compat during transition
    }
}