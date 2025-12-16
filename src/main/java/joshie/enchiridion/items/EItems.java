package joshie.enchiridion.items;

import joshie.enchiridion.data.book.BookRegistry;
import joshie.enchiridion.lib.EInfo;
import joshie.enchiridion.util.EItemGroup;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(modid = EInfo.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EItems {
    public static Item LIBRARY;
    public static Item BOOK;

    @SubscribeEvent
    public static void registerItem(RegisterEvent event) {
        event.register(net.minecraft.core.registries.Registries.ITEM, helper -> {
            BookRegistry.INSTANCE.loadBooksFromConfig(); //Needs to be called before items get registered
            LIBRARY = new ItemLibrary(new Item.Properties());
            helper.register(new ResourceLocation(EInfo.MODID, "library"), LIBRARY);
            BOOK = new ItemBook(new Item.Properties());
            helper.register(new ResourceLocation(EInfo.MODID, "book"), BOOK);
        });
    }
}