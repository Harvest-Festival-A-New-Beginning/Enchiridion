package joshie.enchiridion.gui.book.features.recipe;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.util.ArrayList;

public class RecipeHandlerShapedVanilla extends RecipeHandlerRecipeBase {
    public RecipeHandlerShapedVanilla() {
    }

    public RecipeHandlerShapedVanilla(RecipeHolder<?> recipeHolder) {
        try {
            Recipe<?> recipe = recipeHolder.value();
            if (recipe instanceof ShapedRecipe shaped) {
                init(recipe.getResultItem(null), new ArrayList<>(shaped.getIngredients()), shaped.getWidth());
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
        return ShapedRecipe.class;
    }
}