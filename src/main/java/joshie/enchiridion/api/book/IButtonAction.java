package joshie.enchiridion.api.book;

import joshie.enchiridion.api.gui.ISimpleEditorFieldProvider;
import net.minecraft.resources.ResourceLocation;

public interface IButtonAction extends ISimpleEditorFieldProvider {
    /** Create a copy of this action **/
    IButtonAction copy();
    IButtonAction create(GuiBook guiBook);
    String getName();

    /** Perform the action, Return true if it was successful **/
    boolean performAction(GuiBook guiBook);

    /** Whether the button this action is attached to, should be visible **/
    boolean isVisible();

    /** @return the resource location to display for this action **/
    ResourceLocation getResource();
}