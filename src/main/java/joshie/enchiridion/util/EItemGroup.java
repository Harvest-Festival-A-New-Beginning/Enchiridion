package joshie.enchiridion.util;

import joshie.enchiridion.api.book.IBook;
import joshie.enchiridion.data.book.BookRegistry;
import joshie.enchiridion.items.EItems;
import joshie.enchiridion.lib.EInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EItemGroup {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EInfo.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ENCHIRIDION_TAB =
        CREATIVE_MODE_TABS.register(EInfo.MODID, () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + EInfo.MODID))
            .icon(() -> {
                // Create an Enchiridion book as the icon
                ItemStack book = new ItemStack(EItems.BOOK);
                CompoundTag tag = new CompoundTag();
                tag.putString("identifier", "enchiridion");
                book.setTag(tag);
                return book;
            })
            .displayItems((params, output) -> {
                // Add the library item
                if (EItems.LIBRARY != null) {
                    output.accept(new ItemStack(EItems.LIBRARY));
                }

                // Add all registered books
                if (EItems.BOOK != null) {
                    for (String bookName : BookRegistry.INSTANCE.getUniqueNames()) {
                        IBook book = BookRegistry.INSTANCE.getBookByName(bookName);
                        if (book != null) {
                            ItemStack stack = new ItemStack(EItems.BOOK);
                            CompoundTag tag = new CompoundTag();
                            tag.putString("identifier", bookName);
                            stack.setTag(tag);
                            output.accept(stack);
                        }
                    }
                }
            })
            .build()
        );
}
