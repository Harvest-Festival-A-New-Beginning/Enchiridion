package joshie.enchiridion.gui.book.buttons;

import joshie.enchiridion.api.book.IPage;
import joshie.enchiridion.gui.book.GuiBook;
import joshie.enchiridion.gui.book.features.FeatureImage;
import joshie.enchiridion.helpers.FileCopier;
import joshie.enchiridion.helpers.FileHelper;
import joshie.enchiridion.lib.EInfo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class ButtonInsertImage extends ButtonAbstract {
    public ButtonInsertImage() {
        super("picture");
    }

    @Override
    public void performAction(Object gui) {
        GuiBook guiBook = (GuiBook) gui;
        File file = FileCopier.copyFileFromUser(FileHelper.getImageSaveDirectory(guiBook.getBook()));
        if (file != null) {
            try {
                String folderName = guiBook.getBook().getSaveName();
                String modid = guiBook.getBook().getModID();
                IPage current = guiBook.getPage();
                String path = "";
                if (modid == null || modid.equals("")) {
                    path = EInfo.MODID + ":images/" + folderName + "/" + file.getName();
                } else {
                    path = modid + ":textures/books/images/" + file.getName();
                }

                FeatureImage feature = new FeatureImage(EInfo.MODID + ":images/" + folderName + "/" + file.getName());
                BufferedImage buffered = ImageIO.read(file);
                int width = buffered.getWidth();
                int height = buffered.getHeight();
                guiBook.getPage().addFeature(feature, 0, current.getScroll(), width, height, false, false, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}