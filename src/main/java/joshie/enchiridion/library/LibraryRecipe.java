package joshie.enchiridion.library;

import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.items.EItems;
import joshie.enchiridion.lib.EInfo;
import joshie.enchiridion.util.SafeStack;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class LibraryRecipe extends CustomRecipe {
    public static final Set<SafeStack> VALID_WOODS = new HashSet<>();
    public static final SimpleCraftingRecipeSerializer<LibraryRecipe> LIBRARY_SERIALIZER = new SimpleCraftingRecipeSerializer<>(LibraryRecipe::new);

    public LibraryRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return LIBRARY_SERIALIZER;
    }

    private boolean isWood(@Nonnull ItemStack stack) {
        for (SafeStack safe : SafeStack.allInstances(stack)) {
            if (VALID_WOODS.contains(safe)) return true;
        }
        return false;
    }

    @Override
    public boolean matches(@Nonnull CraftingContainer inv, @Nonnull Level world) {
        for (int i = 0; i < 3; i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty() || !isWood(stack)) return false;
        }

        for (int i = 0; i < 3; i++) {
            ItemStack stack = inv.getItem(i + 3);
            if (stack.isEmpty()) return false;
            else {
                if (EnchiridionAPI.library.getBookHandlerForStack(stack) == null) return false;
            }
        }

        for (int i = 0; i < 3; i++) {
            ItemStack stack = inv.getItem(i + 6);
            if (stack.isEmpty()) return false;
            if (!isWood(stack)) return false;
        }

        return true;
    }

    @Override
    @Nonnull
    public ItemStack assemble(@Nonnull CraftingContainer inv, @Nonnull RegistryAccess registries) {
        return getResultItem(registries);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 3;
    }

    @Override
    @Nonnull
    public ItemStack getResultItem(@Nonnull RegistryAccess registries) {
        return new ItemStack(EItems.LIBRARY);
    }

    @Override
    @Nonnull
    public NonNullList<ItemStack> getRemainingItems(@Nonnull CraftingContainer inv) {
        NonNullList<ItemStack> list = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

        list.set(3, getStackOfOne(inv, 3));
        list.set(4, getStackOfOne(inv, 4));
        list.set(5, getStackOfOne(inv, 5));

        return list;
    }

    @Nonnull
    private ItemStack getStackOfOne(CraftingContainer inv, int index) {
        ItemStack ret = inv.getItem(index).copy();
        ret.setCount(1);
        return ret;
    }
}