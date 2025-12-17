package joshie.enchiridion.api.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.List;

public interface IRecipeHandler {
    /** Add recipes that are valid for this output item **/
    void addRecipes(@Nonnull ItemStack output, List<IRecipeHandler> list, Level world);

    /** Draw this recipe in the book, You can make use of the
     *  the helper functions in @IDrawHelper with access to an instance
     *  from @EnchiridionAPI **/
    void draw();

    /** @return the height of this recipe handler, based on the width **/
    int getHeight(int width);
    
    /** @return an adjusted width, based on the original **/
    int getWidth(int width);

    /** @return the scale size, based on the width, for this recipe handler **/
    float getSize(double width);

    /** @return Returns a unique name for this recipe, saved in the json **/
    String getUniqueName();
    
    /** The name of this recipe type **/
    String getRecipeName();

    /** Add Tooltip **/
    void addTooltip(List<String> list);
}