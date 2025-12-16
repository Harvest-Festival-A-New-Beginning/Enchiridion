package joshie.enchiridion.lib;

import joshie.enchiridion.gui.library.ContainerLibrary;
import joshie.enchiridion.items.EItems;
import joshie.enchiridion.library.LibraryHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryObject;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.minecraft.core.registries.Registries;

import javax.annotation.Nonnull;

public class EGuis {
    public static final String BOOK = EInfo.MODID + ":" + "book";
    public static final String LIBRARY = "library";
    public static final String BOOK_FORCE = EInfo.MODID + ":" + "book_force";
    public static final String WRITABLE = EInfo.MODID + ":" + "writable";
    public static final String WRITTEN = EInfo.MODID + ":" + "written";

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, EInfo.MODID);

    public static final RegistryObject<MenuType<ContainerLibrary>> LIBRARY_CONTAINER = MENUS.register(LIBRARY,
            () -> new MenuType<>(EGuis::createLibraryContainer, net.minecraft.world.flag.FeatureFlags.VANILLA_SET));

    public static MenuProvider getLibraryProvider() {
        return new MenuProvider() {
            @Override
            @Nonnull
            public Component getDisplayName() {
                return Component.translatable(EInfo.MODID + ".container.library");
            }

            @Override
            public AbstractContainerMenu createMenu(int windowId, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
                return createLibraryContainer(windowId, playerInventory);
            }
        };
    }

    private static ContainerLibrary createLibraryContainer(int windowId, Inventory playerInventory) {
        InteractionHand hand = playerInventory.player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == EItems.LIBRARY ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        return new ContainerLibrary(windowId, playerInventory, LibraryHelper.getLibraryContents(playerInventory.player), hand);
    }
}