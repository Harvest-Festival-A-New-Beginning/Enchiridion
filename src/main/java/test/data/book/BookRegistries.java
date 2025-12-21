package test.data.book;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.test.data.book.element.*;
import uk.joshiejack.penguinlib.util.registry.ReloadableRegistry;

/**
 * Central registry for all JSON book-related data
 */
public class BookRegistries {
    // Reloadable registries for JSON data
    public static final ReloadableRegistry<BookWidget> WIDGETS =
            new ReloadableRegistry<>(PenguinLib.MODID, "book/widget", BookWidget.CODEC, new BookWidget(), true);

    public static final ReloadableRegistry<BookPage> PAGES =
            new ReloadableRegistry<>(PenguinLib.MODID, "book/page", BookPage.CODEC, new BookPage(), true);

    public static final ReloadableRegistry<BookTab> TABS =
            new ReloadableRegistry<>(PenguinLib.MODID, "book/tab", BookTab.CODEC, new BookTab(), true);

    public static final ReloadableRegistry<BookDefinition> BOOKS =
            new ReloadableRegistry<>(PenguinLib.MODID, "book", BookDefinition.CODEC, new BookDefinition(), true);

    static {
        PenguinLib.LOGGER.info("BookRegistries initialized:");
        PenguinLib.LOGGER.info("  - WIDGETS registry: penguinlib/book/widget");
        PenguinLib.LOGGER.info("  - PAGES registry: penguinlib/book/page");
        PenguinLib.LOGGER.info("  - TABS registry: penguinlib/book/tab");
        PenguinLib.LOGGER.info("  - BOOKS registry: penguinlib/book");
    }

    /**
     * Registry for render element types (similar to Icon registry)
     */
    public static class Elements {
        public static final DeferredRegister<Codec<? extends RenderElement>> ELEMENT_TYPES =
                DeferredRegister.create(ResourceKey.createRegistryKey(
                        new ResourceLocation(PenguinLib.MODID, "book_elements")), PenguinLib.MODID);

        public static final Registry<Codec<? extends RenderElement>> ELEMENT =
                ELEMENT_TYPES.makeRegistry(b -> b.sync(true));

        public static final Holder<Codec<? extends RenderElement>> TEXT =
                ELEMENT_TYPES.register("text", () -> TextElement.CODEC);

        public static final Holder<Codec<? extends RenderElement>> TEXTURE =
                ELEMENT_TYPES.register("texture", () -> TextureElement.CODEC);

        public static final Holder<Codec<? extends RenderElement>> BUTTON =
                ELEMENT_TYPES.register("button", () -> ButtonElement.CODEC);

        public static final Holder<Codec<? extends RenderElement>> ICON =
                ELEMENT_TYPES.register("icon", () -> IconElement.CODEC);

        public static final Holder<Codec<? extends RenderElement>> FILL =
                ELEMENT_TYPES.register("fill", () -> FillElement.CODEC);
    }

    /**
     * Register all deferred registries
     */
    public static void register(IEventBus eventBus) {
        Elements.ELEMENT_TYPES.register(eventBus);
    }
}
