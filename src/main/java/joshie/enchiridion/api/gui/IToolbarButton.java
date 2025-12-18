package joshie.enchiridion.api.gui;

import net.minecraft.resources.ResourceLocation;

public interface IToolbarButton {
    /** @return the resource location for this button **/
    ResourceLocation getResource();

    /** @return the resource location for when this button is hovered over **/
    ResourceLocation getHoverResource();

    /** On Click **/
    void performAction();

    /** @return the the tooltip text for hovering over this button **/
    String getTooltipText();

    /** If this button is left aligned,
     *  otherwise it'll be assigned to the right instead  */
    boolean isLeftAligned();
}