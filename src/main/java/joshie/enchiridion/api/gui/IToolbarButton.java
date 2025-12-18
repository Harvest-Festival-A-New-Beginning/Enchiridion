package joshie.enchiridion.api.gui;

import net.minecraft.resources.ResourceLocation;

public interface IToolbarButton {
    /** @return the resource location for this button
     *  @param gui the current book GUI instance **/
    ResourceLocation getResource(Object gui);

    /** @return the resource location for when this button is hovered over
     *  @param gui the current book GUI instance **/
    ResourceLocation getHoverResource(Object gui);

    /** On Click
     *  @param gui the current book GUI instance **/
    void performAction(Object gui);

    /** @return the the tooltip text for hovering over this button
     *  @param gui the current book GUI instance **/
    String getTooltipText(Object gui);

    /** If this button is left aligned,
     *  otherwise it'll be assigned to the right instead  */
    boolean isLeftAligned();
}