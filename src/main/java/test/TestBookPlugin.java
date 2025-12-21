package test;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.test.data.book.BookRegistries;
import uk.joshiejack.penguinlib.test.item.TestCreativeTab;
import uk.joshiejack.penguinlib.test.item.TestItems;
import uk.joshiejack.penguinlib.test.item.TestMenus;
import uk.joshiejack.penguinlib.util.IModPlugin;
import uk.joshiejack.penguinlib.util.registry.Plugin;

/**
 * Test plugin for JSON book system
 * Only loads if PenguinLib is in development mode
 */
@Plugin(PenguinLib.MODID)
public class TestBookPlugin implements IModPlugin {
    public static final String MODID = PenguinLib.MODID;

    @Override
    public void construct() {
        IEventBus modBus = ModList.get().getModContainerById(MODID)
                .map(container -> container.getEventBus())
                .orElseThrow(() -> new IllegalStateException("Could not find mod event bus for " + MODID));

        // Register the book element registry
        BookRegistries.register(modBus);

        // Register test items
        TestItems.register(modBus);
        // Register test items and menus
        TestMenus.register(modBus);

        // Register creative tab
        TestCreativeTab.register(modBus);

        PenguinLib.LOGGER.info("Test Book Plugin constructed - JSON book system initialized");
    }

    @Override
    public void setup() {
        PenguinLib.LOGGER.info("Test Book Plugin setup complete");
    }
}
