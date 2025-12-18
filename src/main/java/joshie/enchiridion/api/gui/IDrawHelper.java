package joshie.enchiridion.api.gui;

import joshie.enchiridion.api.recipe.IItemStack;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public interface IDrawHelper {

    void drawStack(@Nonnull ItemStack stack, int left, int top, float size);

    /** For use with RecipeHandlers **/
    void setRenderData(int xPos, int yPos, double width, double height, float size); //Called internally to update the internal sizes
    void drawTexturedRectangle(double x, double y, int u, int v, int w, int h, float scale);
    void drawTexturedReversedRectangle(double x, double y, int u, int v, int w, int h, float scale);
    void drawIItemStack(IItemStack stack);
    boolean isMouseOverIItemStack(IItemStack stack);
    boolean isMouseOverArea(double x, double y, int width, int height, float scale);
}