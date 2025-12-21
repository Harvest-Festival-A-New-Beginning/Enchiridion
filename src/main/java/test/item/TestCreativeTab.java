package test.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.test.data.book.BookRegistries;

/**
 * Creative tab for JSON book system test items
 */
public class TestCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PenguinLib.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BOOK_TAB = CREATIVE_MODE_TABS.register("books",
            () -> CreativeModeTab.builder()
                    .title(Component.literal("Penguin Books"))
                    .icon(() -> new ItemStack(Items.WRITABLE_BOOK))
                    .displayItems((parameters, output) -> {
                        // Add book creator item
                        output.accept(TestItems.BOOK_CREATOR.get());

                        // Debug: Log how many books are registered
                        int bookCount = BookRegistries.BOOKS.registry().size();
                        PenguinLib.LOGGER.info("Creative tab populating - Found {} books in registry", bookCount);

                        // Add all registered JSON books as items
                        BookRegistries.BOOKS.registry().forEach((id, bookDef) -> {
                            PenguinLib.LOGGER.info("  Adding book item: {}", id);
                            output.accept(JsonBookItem.createBookItem(id));
                        });
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
