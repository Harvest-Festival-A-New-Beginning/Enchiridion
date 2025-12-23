package joshie.enchiridion.helpers;

import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.api.edit.IEditHelper;
import joshie.enchiridion.api.gui.ISimpleEditorFieldProvider;
import joshie.enchiridion.gui.book.GuiBook;
import joshie.enchiridion.gui.book.GuiSimpleEditor;
import joshie.enchiridion.gui.book.GuiSimpleEditorGeneric;
import joshie.enchiridion.gui.book.buttons.actions.ActionJumpPage;
import joshie.enchiridion.gui.book.element.ActionButtonElement;

public class EditHelper implements IEditHelper {
    @Override
    public void setSimpleEditorFeature(ISimpleEditorFieldProvider feature, Object gui) {
        GuiBook guiBook = (GuiBook) gui;
        guiBook.getSimpleEditor().setEditor(GuiSimpleEditorGeneric.INSTANCE.setFeature(feature));
    }

    @Override
    public ActionButtonElement getJumpPageButton(int page) {
        return new ActionButtonElement(new ActionJumpPage(page));
    }
}