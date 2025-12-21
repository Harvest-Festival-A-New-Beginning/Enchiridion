package test.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import uk.joshiejack.penguinlib.PenguinLib;

/**
 * Menu types for JSON book system
 */
public class TestMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, PenguinLib.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<JsonBookMenu>> JSON_BOOK =
            MENUS.register("json_book", () -> IMenuTypeExtension.create(JsonBookMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
