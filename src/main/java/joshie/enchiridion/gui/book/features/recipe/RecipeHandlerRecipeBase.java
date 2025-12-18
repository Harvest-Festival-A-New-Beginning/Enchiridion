package joshie.enchiridion.gui.book.features.recipe;

import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.api.recipe.IRecipeHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.core.registries.BuiltInRegistries;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class RecipeHandlerRecipeBase extends RecipeHandlerBase {

    protected void init(@Nonnull ItemStack output, List<Object> input, int width) {
        int length = input.size();
        stackList.add(new WrappedStack(output, 115D, 31D, 1.75F));
        if (length == 1) {
            stackList.add(new WrappedStack(getObject(input, 0), 29D, 38D, 1F));
        } else if (length == 2) {
            if (width == 1) {
                stackList.add(new WrappedStack(getObject(input, 0), 29D, 2D, 1F));
                stackList.add(new WrappedStack(getObject(input, 1), 29D, 38D, 1F));
            } else {
                stackList.add(new WrappedStack(getObject(input, 0), 29D, 38D, 1F));
                stackList.add(new WrappedStack(getObject(input, 1), 56D, 38D, 1F));
            }
        } else if (length == 3) {
            if (width == 1) {
                stackList.add(new WrappedStack(getObject(input, 0), 29D, 2D, 1F));
                stackList.add(new WrappedStack(getObject(input, 1), 29D, 38D, 1F));
                stackList.add(new WrappedStack(getObject(input, 2), 29D, 75D, 1F));
            } else {
                stackList.add(new WrappedStack(getObject(input, 0), 1D, 38D, 1F));
                stackList.add(new WrappedStack(getObject(input, 1), 29D, 38D, 1F));
                stackList.add(new WrappedStack(getObject(input, 2), 56D, 38D, 1F));
            }
        } else if (length == 4) {
            stackList.add(new WrappedStack(getObject(input, 0), 1D, 2D, 1F));
            stackList.add(new WrappedStack(getObject(input, 1), 29D, 2D, 1F));
            stackList.add(new WrappedStack(getObject(input, 2), 1D, 38D, 1F));
            stackList.add(new WrappedStack(getObject(input, 3), 29D, 38D, 1F));
        } else if (length == 6) {
            if (width == 2) {
                stackList.add(new WrappedStack(getObject(input, 0), 1D, 2D, 1F));
                stackList.add(new WrappedStack(getObject(input, 1), 29D, 2D, 1F));
                stackList.add(new WrappedStack(getObject(input, 2), 1D, 38D, 1F));
                stackList.add(new WrappedStack(getObject(input, 3), 29D, 38D, 1F));
                stackList.add(new WrappedStack(getObject(input, 4), 1D, 75D, 1F));
                stackList.add(new WrappedStack(getObject(input, 5), 29D, 75D, 1F));
            } else {
                stackList.add(new WrappedStack(getObject(input, 0), 1D, 2D, 1F));
                stackList.add(new WrappedStack(getObject(input, 1), 29D, 2D, 1F));
                stackList.add(new WrappedStack(getObject(input, 2), 56D, 2D, 1F));

                stackList.add(new WrappedStack(getObject(input, 3), 1D, 38D, 1F));
                stackList.add(new WrappedStack(getObject(input, 4), 29D, 38D, 1F));
                stackList.add(new WrappedStack(getObject(input, 5), 56D, 38D, 1F));
            }
        } else {
            stackList.add(new WrappedStack(getObject(input, 0), 1D, 2D, 1F));
            stackList.add(new WrappedStack(getObject(input, 1), 29D, 2D, 1F));
            stackList.add(new WrappedStack(getObject(input, 2), 56D, 2D, 1F));

            stackList.add(new WrappedStack(getObject(input, 3), 1D, 38D, 1F));
            stackList.add(new WrappedStack(getObject(input, 4), 29D, 38D, 1F));
            stackList.add(new WrappedStack(getObject(input, 5), 56D, 38D, 1F));

            stackList.add(new WrappedStack(getObject(input, 6), 1D, 75D, 1F));
            stackList.add(new WrappedStack(getObject(input, 7), 29D, 75D, 1F));
            stackList.add(new WrappedStack(getObject(input, 8), 56D, 75D, 1F));
        }

        for (Object o : input) {
            if (o instanceof ItemStack) {
                addToUnique(BuiltInRegistries.ITEM.getKey(((ItemStack) o).getItem()));
            }
        }
    }

    @Override
    public void addRecipes(@Nonnull ItemStack output, List<IRecipeHandler> list, Level world) {
        for (RecipeHolder<?> holder : world.getRecipeManager().getRecipes()) {
            Recipe<?> check = holder.value();
            ItemStack stack = check.getResultItem(world.registryAccess());
            //CHECK -- > EXTENDS the class
            if (stack.isEmpty() || (!getRecipeClass().isAssignableFrom(check.getClass()))) continue;
            if (ItemStack.isSameItemSameTags(stack, output)) {
                try {
                    list.add((IRecipeHandler) Class.forName(getHandlerClass().getName()).getConstructor(Recipe.class).newInstance(check));
                } catch (Exception ignored) {
                }
            }
        }
    }

    @Override
    public String getRecipeName() {
        return getRecipeClass().getSimpleName();
    }

    protected abstract Class getRecipeClass();

    protected abstract Class getHandlerClass();

    @Override
    public int getHeight(int width) {
        return width / 2;
    }

    @Override
    public int getWidth(int width) {
        return width;
    }

    @Override
    public float getSize(double width) {
        return (float) (width / 110D);
    }

    @Override
    protected void drawBackground() {
        com.mojang.blaze3d.systems.RenderSystem.setShaderTexture(0, LOCATION);
        EnchiridionAPI.draw.drawTexturedRectangle(0D, 0D, 0, 0, 58, 58, 1F);
        EnchiridionAPI.draw.drawTexturedRectangle(84D, 42D, 1, 63, 20, 14, 1F);
    }

    /*@Nonnull
    protected ItemStack toStack(IItemStack iStack) { //TODO CraftTweaker support
        if (iStack == null) return ItemStack.EMPTY;
        return (ItemStack) iStack.getInternal();
    }*/
}