package joshie.enchiridion.api.edit;

import joshie.enchiridion.api.book.IButtonActionProvider;
import joshie.enchiridion.api.gui.ISimpleEditorFieldProvider;

public interface IEditHelper {
    /** Put this feature in the simple editor
     *  @param feature the feature to edit
     *  @param gui the current book GUI instance **/
    void setSimpleEditorFeature(ISimpleEditorFieldProvider feature, Object gui);

    /** Returns a jump to page button **/
    IButtonActionProvider getJumpPageButton(int page);
}