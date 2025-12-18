package joshie.enchiridion.gui.book.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.api.book.IFeature;
import joshie.enchiridion.api.book.FeatureProvider;
import joshie.enchiridion.api.book.IPage;
import joshie.enchiridion.api.recipe.IRecipeHandler;
import joshie.enchiridion.helpers.StackHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class FeatureRecipe extends FeatureItem {
    public static final Codec<FeatureRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.optionalFieldOf("item_string", "").forGetter(f -> f.itemString),
        Codec.BOOL.optionalFieldOf("hide_tooltip", false).forGetter(f -> f.hideTooltip),
        Codec.STRING.optionalFieldOf("ingredients", "plankWood:plankWood:plankWood:cobblestone:ingotAluminum:cobblestone:cobblestone:dustRedstone:cobblestone").forGetter(f -> f.ingredients),
        Codec.STRING.optionalFieldOf("recipe_type", "ShapedOreRecipe").forGetter(f -> f.recipeType)
    ).apply(instance, (itemString, hideTooltip, ingredients, recipeType) -> {
        FeatureRecipe feature = new FeatureRecipe();
        feature.itemString = itemString;
        feature.hideTooltip = hideTooltip;
        feature.ingredients = ingredients;
        feature.recipeType = recipeType;
        return feature;
    }));

    public transient static final ArrayList<IRecipeHandler> HANDLERS = new ArrayList<>();

    protected String ingredients = "plankWood:plankWood:plankWood:cobblestone:ingotAluminum:cobblestone:cobblestone:dustRedstone:cobblestone";
    protected String recipeType = "ShapedOreRecipe";
    protected transient int index = 0;
    protected transient IRecipeHandler handler;

    public FeatureRecipe() {
    }

    public FeatureRecipe(@Nonnull ItemStack stack) {
        setItemStack(stack);
    }

    @Override
    public FeatureProvider copy() {
        FeatureRecipe recipe = new FeatureRecipe();
        recipe.ingredients = ingredients;
        recipe.recipeType = recipeType;
        recipe.itemString = itemString;
        recipe.hideTooltip = hideTooltip;
        return recipe;
    }

    private boolean buildRecipe(boolean isLoading) {
        ArrayList<IRecipeHandler> recipes = new ArrayList<>();
        for (IRecipeHandler handler : HANDLERS) {
            handler.addRecipes(stack, recipes, ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD)); //TODO Test
        }

        //Basic loop checking type and recipe
        if (isLoading) {
            //Loop 1, Exact Match
            for (IRecipeHandler handler : recipes) {
                if (recipeType.equals(handler.getRecipeName())) {
                    if (!ingredients.isEmpty() && ingredients.equals(handler.getUniqueName())) {
                        this.handler = handler;
                        return true;
                    }
                }
            }

            //Loop 2, Fuzzy Match
            for (IRecipeHandler handler : recipes) {
                if (recipeType.equals(handler.getRecipeName())) {
                    this.handler = handler;
                    return true;
                }
            }
        }

        //General Search
        int number = -1;
        for (IRecipeHandler handler : recipes) {
            number++;
            if (number == index) {
                this.handler = handler;
                this.recipeType = handler.getRecipeName();
                this.ingredients = handler.getUniqueName();
                return true;
            }
        }

        index = 0;
        return true;
    }

    @Override
    public void setItemStack(@Nonnull ItemStack stack) {
        if (ItemStack.isSameItem(stack, this.stack)) {
            index++;
        } else index = 0;

        IRecipeHandler previous = handler;
        super.setItemStack(stack);
        buildRecipe(false);
        if (previous == handler) {
            index = 0;
            buildRecipe(false);
        }

        //Update the provider
        if (getPage() != null) {
            update(getPage());
        }
    }

    @Override
    public void update(IPage page) {
        super.update(page);

        if (handler != null) {
            int width = getWidth();
            setHeight(handler.getHeight(width));
            size = handler.getSize(width);
        }
    }

    @Override
    protected void drawFeature(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (stack.isEmpty() && itemString != null) stack = StackHelper.getStackFromString(itemString);
        if (handler != null) {
            EnchiridionAPI.draw.setRenderData(getLeft(), getTop(), getWidth(), getHeight(), size);
            handler.draw();
        } else {
            buildRecipe(true);
            update(getPage()); //Initiate the provider
        }
    }

    @Override
    public void addTooltip(List<String> list, int mouseX, int mouseY) {
        if (!hideTooltip && handler != null) {
            EnchiridionAPI.draw.setRenderData(getLeft(), getTop(), getWidth(), getHeight(), size);
            handler.addTooltip(list);
        }
    }

    @Override
    public Codec<? extends joshie.enchiridion.api.book.IFeature> codec() {
        return CODEC;
    }
}