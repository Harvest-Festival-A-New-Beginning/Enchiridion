package joshie.enchiridion.gui.book.buttons;

import joshie.enchiridion.gui.book.GuiBook;
import joshie.enchiridion.helpers.FileCopier;
import joshie.enchiridion.helpers.FileHelper;
import joshie.enchiridion.lib.EInfo;

import java.io.File;

public class ButtonChangeBackground extends ButtonAbstract {
    public ButtonChangeBackground() {
        super("background");
    }

    @Override
    public void performAction(Object gui) {
        GuiBook guiBook = (GuiBook) gui;
        File file = FileCopier.copyFileFromUser(FileHelper.getImageSaveDirectory());
        if (file != null) {
            guiBook.getBook().setBackgroundResource(EInfo.MODID + ":images/" + guiBook.getBook().getSaveName() + "/" + file.getName());
        }
    }

    @Override
    public boolean isLeftAligned() {
        return false;
    }
}