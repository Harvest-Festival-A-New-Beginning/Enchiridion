package joshie.enchiridion.helpers;

import joshie.enchiridion.Enchiridion;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import org.apache.logging.log4j.Level;

import java.util.stream.Collectors;

public class ItemListHelper {
    private static NonNullList<ItemStack> items = NonNullList.withSize(0, ItemStack.EMPTY);
    private static NonNullList<ItemStack> allItems = NonNullList.withSize(0, ItemStack.EMPTY);

    public static NonNullList<ItemStack> init() {
        items = NonNullList.create();
        allItems = NonNullList.create();

        for (Item item : BuiltInRegistries.ITEM) {
            if (item == null) {
                continue;
            }
            try {
                // Note: fillItemGroup was removed in 1.19+. This needs to use CreativeModeTabs differently
                items.add(new ItemStack(item));
            } catch (Exception e) {
                Enchiridion.log(org.apache.logging.log4j.Level.ERROR, "Enchiridion had an issue when trying to load the item: " + item.getClass());
            }
        }
        allItems.addAll(items);
        return items;
    }

    public static NonNullList<ItemStack> items() {
        return !items.isEmpty() ? items : init();
    }

    public static NonNullList<ItemStack> allItems() {
        return !allItems.isEmpty() ? allItems : init();
    }

    public static void addInventory() {
        try {
            allItems.addAll(Minecraft.getInstance().player.getInventory().items.stream().filter(stack -> !stack.isEmpty()).filter(stack -> !allItems().contains(stack)).collect(Collectors.toList()));
        } catch (Exception ignored) {
        }
    }
}