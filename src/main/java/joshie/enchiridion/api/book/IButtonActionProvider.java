package joshie.enchiridion.api.book;

import net.minecraft.resources.ResourceLocation;

public interface IButtonActionProvider extends IFeature {
    // copy() inherited from IFeature (returns FeatureProvider)
    IButtonAction getAction();

    /** Reduce this stuff **/
    ResourceLocation getResource(boolean isHovered);
    String getText(boolean isHovered);
    int getTextOffsetX(boolean isHovered);
    int getTextOffsetY(boolean isHovered);

    /** @return the tooltip text **/
    boolean processesClick(int button);
    String getTooltipText();

    /** Setters **/
    IButtonActionProvider setTextOffsetX(boolean isHovered, int x);
    IButtonActionProvider setTextOffsetY(boolean isHovered, int y);
    IButtonActionProvider setResourceLocation(boolean isHovered, ResourceLocation resource);
    IButtonActionProvider setText(boolean isHovered, String text);
    IButtonActionProvider setTooltip(String tooltip);
    IButtonActionProvider setProcessesClick(int button, boolean value);
}