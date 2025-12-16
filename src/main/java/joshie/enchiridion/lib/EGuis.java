package joshie.enchiridion.lib;

import joshie.enchiridion.gui.library.ContainerLibrary;
import joshie.enchiridion.items.EItems;
import joshie.enchiridion.library.LibraryHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.event.RegistryEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.ObjectHolder;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(modid = EInfo.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EGuis {
    public static final String BOOK = EInfo.MODID + ":" + "book";
    public static final String LIBRARY = EInfo.MODID + ":" + "library";
    public static final String BOOK_FORCE = EInfo.MODID + ":" + "book_force";
    public static final String WRITABLE = EInfo.MODID + ":" + "writable";
    public static final String WRITTEN = EInfo.MODID + ":" + "written";

    @ObjectHolder(EGuis.LIBRARY)
    public static ContainerType<ContainerLibrary> LIBRARY_CONTAINER;

    @SubscribeEvent
    public static void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().register(new ContainerType<>(EGuis::createLibraryContainer).setRegistryName(LIBRARY));
    }

    public static INamedContainerProvider getLibraryProvider() {
        return new INamedContainerProvider() {
            @Override
            @Nonnull
            public Component getDisplayName() {
                return Component.translatable(EInfo.MODID + ".container.library");
            }

            @Override
            public Container createMenu(int windowId, @Nonnull PlayerInventory playerInventory, @Nonnull Player playerEntity) {
                return createLibraryContainer(windowId, playerInventory);
            }
        };
    }

    private static ContainerLibrary createLibraryContainer(int windowId, PlayerInventory playerInventory) {
        InteractionHand hand = playerInventory.player.getHeldItemMainhand().getItem() == EItems.LIBRARY ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        return new ContainerLibrary(windowId, playerInventory, LibraryHelper.getLibraryContents(playerInventory.player), hand);
    }
}