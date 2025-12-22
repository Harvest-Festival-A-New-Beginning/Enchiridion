package joshie.enchiridion.api.gui;

import joshie.enchiridion.gui.book.GuiBook;
import net.minecraft.resources.ResourceLocation;

public abstract class IToolbarButton {
    /** @return the resource location for this button
     *  @param gui the current book GUI instance **/
    public abstract ResourceLocation getResource(GuiBook gui);

    /** @return the resource location for when this button is hovered over
     *  @param gui the current book GUI instance **/
    public abstract ResourceLocation getHoverResource(GuiBook gui);

    /** On Click
     *  @param gui the current book GUI instance **/
    public abstract void performAction(GuiBook gui);

    /** @return the the tooltip text for hovering over this button
     *  @param gui the current book GUI instance **/
    public abstract String getTooltipText(GuiBook gui);

    /** If this button is left aligned,
     *  otherwise it'll be assigned to the right instead  */
    public abstract boolean isLeftAligned();
}