package joshie.enchiridion.gui.book.buttons;

import joshie.enchiridion.EConfig;
import joshie.enchiridion.gui.book.GuiBook;
import joshie.enchiridion.helpers.CodecHelper;
import joshie.enchiridion.helpers.FileCopier;
import joshie.enchiridion.helpers.FileHelper;
import joshie.enchiridion.json.BookIconTemplate;
import joshie.enchiridion.json.BookIconTemplate.Icons;
import net.minecraft.client.Minecraft;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class ButtonChangeIcon extends ButtonAbstract {
    public ButtonChangeIcon() {
        super("book");
    }

    public static void refreshResources() {
        if (EConfig.SETTINGS.resourceReload.get()) Minecraft.getInstance().getResourcePackRepository().reload();
    }

    @Override
    public void performAction(GuiBook guiBook) {
        
        File file = FileCopier.copyFileFromUser(FileHelper.getIconsDirectory());
        if (file != null) {
            try {
                File iconJson = FileHelper.getIconsJSONForBook(guiBook.getBook());
                BookIconTemplate template = new BookIconTemplate();
                template.parent = "enchiridion:item/book";
                template.textures = new Icons();
                template.textures.layer0 = "enchiridion:items/" + FilenameUtils.removeExtension(file.getName());
                Writer writer = new OutputStreamWriter(new FileOutputStream(iconJson), StandardCharsets.UTF_8);
                writer.write(CodecHelper.toJson(BookIconTemplate.CODEC, template));
                writer.close();

                //Reload the icons
                refreshResources();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isLeftAligned() {
        return false;
    }
}