package joshie.enchiridion.gui.book.features.recipe;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.ArrayList;

public class RecipeHandlerShapelessVanilla extends RecipeHandlerRecipeBase {
    public RecipeHandlerShapelessVanilla() {
    }

    public RecipeHandlerShapelessVanilla(RecipeHolder<?> recipeHolder) {
        try {
            Recipe<?> recipe = recipeHolder.value();
            if (recipe instanceof ShapelessRecipe shapeless) {
                init(recipe.getResultItem(null), new ArrayList<>(shapeless.getIngredients()), 3);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Class getHandlerClass() {
        return this.getClass();
    }

    @Override
    protected Class getRecipeClass() {
        return ShapelessRecipe.class;
    }
}