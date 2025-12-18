package joshie.enchiridion.gui.book.buttons;

import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.gui.book.GuiBook;
import joshie.enchiridion.gui.book.GuiSimpleEditor;
import joshie.enchiridion.gui.book.GuiSimpleEditorTemplateSave;

public class ButtonSaveTemplate extends ButtonAbstract {
    public ButtonSaveTemplate() {
        super("template.save");
    }

    @Override
    public boolean isLeftAligned() {
        return false;
    }

    @Override
    public void performAction() {
        ((joshie.enchiridion.gui.book.GuiBook) EnchiridionAPI.book).getSimpleEditor().setEditor(GuiSimpleEditorTemplateSave.INSTANCE);
    }
}