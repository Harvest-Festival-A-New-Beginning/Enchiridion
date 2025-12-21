package joshie.enchiridion.api.edit;

import joshie.enchiridion.gui.book.features.FeatureButton;
import joshie.enchiridion.api.gui.ISimpleEditorFieldProvider;

public interface IEditHelper {
    /** Put this feature in the simple editor
     *  @param feature the feature to edit
     *  @param gui the current book GUI instance **/
    void setSimpleEditorFeature(ISimpleEditorFieldProvider feature, Object gui);

    /** Returns a jump to page button **/
    FeatureButton getJumpPageButton(int page);
}