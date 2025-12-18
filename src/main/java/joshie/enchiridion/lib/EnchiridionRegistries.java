package joshie.enchiridion.lib;

import com.mojang.serialization.Codec;
import joshie.enchiridion.api.book.IFeature;
import joshie.enchiridion.data.book.Book;
import joshie.enchiridion.gui.book.features.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import uk.joshiejack.penguinlib.util.registry.ReloadableRegistry;

import static joshie.enchiridion.lib.EInfo.MODID;

public class EnchiridionRegistries {
    // Reloadable registry for books - will load from data/<modid>/books/<book_name>.json
    public static final ReloadableRegistry<Book> BOOKS =
        new ReloadableRegistry<>(MODID, "books", Book.CODEC, new Book(), true);

    public static class Features {
        public static final DeferredRegister<Codec<? extends IFeature>> FEATURE_TYPES =
            DeferredRegister.create(
                ResourceKey.createRegistryKey(new ResourceLocation(MODID, "features")),
                MODID
            );

        public static final Registry<Codec<? extends IFeature>> FEATURE =
            FEATURE_TYPES.makeRegistry(b -> b.sync(true));

        // Register all feature type codecs
        public static final DeferredHolder<Codec<? extends IFeature>, Codec<? extends IFeature>> TEXT =
            FEATURE_TYPES.register("text", () -> FeatureText.CODEC);
        public static final DeferredHolder<Codec<? extends IFeature>, Codec<? extends IFeature>> IMAGE =
            FEATURE_TYPES.register("image", () -> FeatureImage.CODEC);
        public static final DeferredHolder<Codec<? extends IFeature>, Codec<? extends IFeature>> ITEM =
            FEATURE_TYPES.register("item", () -> FeatureItem.CODEC);
        public static final DeferredHolder<Codec<? extends IFeature>, Codec<? extends IFeature>> RECIPE =
            FEATURE_TYPES.register("recipe", () -> FeatureRecipe.CODEC);
        public static final DeferredHolder<Codec<? extends IFeature>, Codec<? extends IFeature>> BUTTON =
            FEATURE_TYPES.register("button", () -> FeatureButton.CODEC);
        public static final DeferredHolder<Codec<? extends IFeature>, Codec<? extends IFeature>> BOX =
            FEATURE_TYPES.register("box", () -> FeatureBox.CODEC);
        public static final DeferredHolder<Codec<? extends IFeature>, Codec<? extends IFeature>> PREVIEW_WINDOW =
            FEATURE_TYPES.register("preview_window", () -> FeaturePreviewWindow.CODEC);
    }

    public static void register(IEventBus eventBus) {
        Features.FEATURE_TYPES.register(eventBus);
    }
}
