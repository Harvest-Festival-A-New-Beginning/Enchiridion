package joshie.enchiridion.gui.book.buttons;

import joshie.enchiridion.gui.book.GuiBook;
import joshie.enchiridion.gui.book.GuiSimpleEditor;
import joshie.enchiridion.gui.book.GuiSimpleEditorTemplate;

public class ButtonInsertTemplate extends ButtonAbstract {
    public ButtonInsertTemplate() {
        super("template.load");
    }

    @Override
    public boolean isLeftAligned() {
        return false;
    }

    @Override
    public void performAction(GuiBook guiBook) {
        guiBook.getSimpleEditor().setEditor(GuiSimpleEditorTemplate.INSTANCE);
    }
}