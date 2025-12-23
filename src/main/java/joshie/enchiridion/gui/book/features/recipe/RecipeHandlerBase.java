package joshie.enchiridion.gui.book.features.recipe;

import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.api.recipe.IItemStack;
import joshie.enchiridion.api.recipe.IRecipeHandler;
import joshie.enchiridion.gui.book.GuiBook;
import joshie.enchiridion.util.ELocation;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import java.util.List;

public abstract class RecipeHandlerBase implements IRecipeHandler {
    protected static final ResourceLocation LOCATION = new ELocation("guide_elements");
    protected NonNullList<IItemStack> stackList = NonNullList.create();
    private String unique;

    public RecipeHandlerBase() {
    }

    public void addToUnique(Object o) {
        String string = "" + o;
        if (unique == null) {
            unique = string;
        } else unique += ":" + string;
    }

    @Override
    public void addTooltip(List<String> list, GuiBook guiBook) {
        // TODO: Removed isMouseOverIItemStack - needs refactoring for mouse hit detection
        // Recipe tooltips will not work until this is reimplemented
        /*for (IItemStack stack : stackList) {
            if (stack == null || stack.getItemStack().isEmpty()) continue;
            if (guiBook.isMouseOverIItemStack(stack)) {
                // TODO: TooltipContext API changed in 1.20.4 - needs proper Item.TooltipContext
                // For now, just use the display name as a simple fallback
                list.add(stack.getItemStack().getHoverName().getString());
                break; //Only permit one item to display
            }
        }*/
    }


    protected final Object getObject(List<Object> input, int i) {
        if (i >= input.size()) return ItemStack.EMPTY;
        input.stream().filter(o -> o instanceof ItemStack).forEach(o -> ((ItemStack) o).setCount(1));
        return input.get(i);
    }

    @Override
    public String getUniqueName() {
        return unique;
    }

    @Override
    public void draw(GuiBook guiBook) {
        drawBackground(guiBook);
        // TODO: Removed drawIItemStack - needs refactoring to use GuiGraphics directly
        // Recipe items will not render until this is reimplemented
        /*for (IItemStack stack : stackList) {
            guiBook.drawIItemStack(stack);
        }*/
    }

    protected abstract void drawBackground(GuiBook guiBook);
}