package test.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import uk.joshiejack.penguinlib.PenguinLib;

/**
 * Test items for the JSON book system
 */
public class TestItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PenguinLib.MODID);

    public static final DeferredItem<Item> BOOK_CREATOR = ITEMS.register("book_creator",
            () -> new BookCreatorItem(new Item.Properties()
                    .stacksTo(1)));

    public static final DeferredItem<Item> JSON_BOOK = ITEMS.register("json_book",
            JsonBookItem::new);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
