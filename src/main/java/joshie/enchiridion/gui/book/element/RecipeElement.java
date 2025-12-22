package joshie.enchiridion.gui.book.element;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.api.book.Page;
import joshie.enchiridion.api.recipe.IRecipeHandler;
import joshie.enchiridion.gui.book.GuiBook;
import joshie.enchiridion.helpers.StackHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Recipe display element
 * Uses IRecipeHandler system to render crafting recipes
 */
public class RecipeElement implements FeatureElement {
    public static final Codec<RecipeElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.optionalFieldOf("item_string", "").forGetter(f -> f.itemString),
        Codec.BOOL.optionalFieldOf("hide_tooltip", false).forGetter(f -> f.hideTooltip),
        Codec.STRING.optionalFieldOf("ingredients", "plankWood:plankWood:plankWood:cobblestone:ingotAluminum:cobblestone:cobblestone:dustRedstone:cobblestone").forGetter(f -> f.ingredients),
        Codec.STRING.optionalFieldOf("recipe_type", "ShapedOreRecipe").forGetter(f -> f.recipeType)
    ).apply(instance, RecipeElement::new));

    public static final ArrayList<IRecipeHandler> HANDLERS = new ArrayList<>();

    private String itemString;
    private boolean hideTooltip;
    private String ingredients;
    private String recipeType;

    private transient ItemStack stack = ItemStack.EMPTY;
    private transient int index = 0;
    private transient IRecipeHandler handler;
    private transient float size = 1.0f;
    private transient int cachedHeight = 0;

    public RecipeElement(String itemString, boolean hideTooltip, String ingredients, String recipeType) {
        this.itemString = itemString;
        this.hideTooltip = hideTooltip;
        this.ingredients = ingredients;
        this.recipeType = recipeType;
    }

    public RecipeElement(@Nonnull ItemStack stack) {
        this("", false, "plankWood:plankWood:plankWood:cobblestone:ingotAluminum:cobblestone:cobblestone:dustRedstone:cobblestone", "ShapedOreRecipe");
        setItemStack(stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, int width, int height, float scale, int mouseX, int mouseY, float partialTicks) {
        if (stack.isEmpty() && itemString != null && !itemString.isEmpty()) {
            stack = StackHelper.getStackFromString(itemString);
        }

        if (handler != null) {
            // TODO: Recipe handlers need refactoring to accept position/size parameters
            handler.draw(null); // Passing null since handler doesn't use it properly yet
        } else {
            buildRecipe(true);
        }
    }

    @Override
    public void addTooltip(List<String> tooltip, int mouseX, int mouseY) {
        if (!hideTooltip && handler != null) {
            // TODO: Recipe handlers need refactoring to accept position parameters
            handler.addTooltip(tooltip, null);
        }
    }

    @Override
    public void onUpdate(@Nullable Page page) {
        if (handler != null && cachedHeight == 0) {
            // Calculate height based on handler - FeatureProvider will need to query this
            cachedHeight = handler.getHeight(200); // Default width assumption
            size = handler.getSize(200);
        }
    }

    private boolean buildRecipe(boolean isLoading) {
        ArrayList<IRecipeHandler> recipes = new ArrayList<>();
        for (IRecipeHandler handler : HANDLERS) {
            handler.addRecipes(stack, recipes, ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD));
        }

        if (isLoading) {
            // Loop 1: Exact Match
            for (IRecipeHandler handler : recipes) {
                if (recipeType.equals(handler.getRecipeName())) {
                    if (!ingredients.isEmpty() && ingredients.equals(handler.getUniqueName())) {
                        this.handler = handler;
                        return true;
                    }
                }
            }

            // Loop 2: Fuzzy Match
            for (IRecipeHandler handler : recipes) {
                if (recipeType.equals(handler.getRecipeName())) {
                    this.handler = handler;
                    return true;
                }
            }
        }

        // General Search
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

    public void setItemStack(@Nonnull ItemStack stack) {
        if (ItemStack.isSameItem(stack, this.stack)) {
            index++;
        } else {
            index = 0;
        }

        IRecipeHandler previous = handler;
        this.stack = stack;
        buildRecipe(false);
        if (previous == handler) {
            index = 0;
            buildRecipe(false);
        }

        itemString = StackHelper.getStringFromStack(stack);
    }

    @Override
    public FeatureElement copy() {
        return new RecipeElement(itemString, hideTooltip, ingredients, recipeType);
    }

    @Override
    public String getName() {
        return "Recipe";
    }

    @Override
    public Codec<? extends FeatureElement> codec() {
        return CODEC;
    }

    // Getters for handler info
    public int getCachedHeight() {
        return cachedHeight;
    }

    public float getSize() {
        return size;
    }

    public ItemStack getStack() {
        return stack;
    }

    public String getItemString() {
        return itemString;
    }
}
