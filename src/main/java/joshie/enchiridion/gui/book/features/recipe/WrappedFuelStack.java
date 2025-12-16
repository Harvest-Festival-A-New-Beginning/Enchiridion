package joshie.enchiridion.gui.book.features.recipe;

import joshie.enchiridion.helpers.ItemListHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

import java.util.stream.Collectors;

public class WrappedFuelStack extends WrappedStack {
    public WrappedFuelStack(double x, double y, float scale) {
        super(null, x, y, scale);

        permutations.addAll(ItemListHelper.items().stream().filter(stack -> getBurnTime(stack) > 0).collect(Collectors.toList()));

        hasPermutations = permutations.size() > 1;
        stack = permutations.get(RAND.nextInt(permutations.size()));
    }

    private int getBurnTime(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        } else {
            Item item = stack.getItem();
            int ret = stack.getBurnTime(null);
            return net.neoforged.neoforge.event.EventHooks.getItemBurnTime(stack, ret == -1 ? AbstractFurnaceBlockEntity.getFuel().getOrDefault(item, 0) : ret, RecipeType.SMELTING);
        }
    }
}