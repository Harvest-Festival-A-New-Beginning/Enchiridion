package joshie.enchiridion.gui.book;

import joshie.enchiridion.EConfig;
import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.api.gui.IBookEditorOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.List;

public class GuiSimpleEditor extends AbstractGuiOverlay {
    public static final GuiSimpleEditor INSTANCE = new GuiSimpleEditor();
    private EditBox textField;
    private String text = "";
    private IBookEditorOverlay editor = null;

    private GuiSimpleEditor() {
    }

    public void setEditor(IBookEditorOverlay editor) {
        this.editor = editor;
        this.text = "";
    }

    @Override
    public void init() {
        Minecraft mc = Minecraft.getInstance();
        Screen currentScreen = mc.screen;
        if (currentScreen != null) {
            this.textField = new EditBox(mc.font, mc.getWindow().getGuiScaledWidth() / 4 + (EConfig.SETTINGS.editorXPos + 27), mc.getWindow().getGuiScaledHeight() / 4 + (EConfig.SETTINGS.toolbarYPos.get() + 17), 80, 7, Component.literal("enchiridion.simpleEditor.search"));
            this.textField.setMaxLength(32);
            this.textField.setBordered(false); //TODO Set to false when done
            this.textField.setValue(text != null && !text.isEmpty() ? text : "");
        }
    }

    @Override
    public void tick() {
        if (editor != null) {
            // textField.tick() was removed in 1.20.4
            this.editor.updateSearch(this.getText());
        }
    }

    @Override
    public GuiEventListener getFocused() {
        if (editor != null) {
            return this.textField;
        }
        return super.getFocused();
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        if (editor != null) {
            /* Draw the Background */
            EnchiridionAPI.draw.drawImage(SIDEBAR, EConfig.SETTINGS.editorXPos - 3, EConfig.SETTINGS.toolbarYPos.get() - 7, EConfig.SETTINGS.editorXPos + 87, EConfig.SETTINGS.timelineYPos.get() + 13);
            EnchiridionAPI.draw.drawBorderedRectangle(EConfig.SETTINGS.editorXPos, EConfig.SETTINGS.toolbarYPos.get() + 7, EConfig.SETTINGS.editorXPos + 85, EConfig.SETTINGS.timelineYPos.get() + 11, 0xFF312921, 0xFF191511);
            EnchiridionAPI.draw.drawBorderedRectangle(EConfig.SETTINGS.editorXPos + 2, EConfig.SETTINGS.toolbarYPos.get() + 9, EConfig.SETTINGS.editorXPos + 83, EConfig.SETTINGS.timelineYPos.get() + 9, 0xFFE4D6AE, 0x5579725A);
            EnchiridionAPI.draw.drawBorderedRectangle(EConfig.SETTINGS.editorXPos, EConfig.SETTINGS.toolbarYPos.get() - 3, EConfig.SETTINGS.editorXPos + 84, EConfig.SETTINGS.toolbarYPos.get() + 7, 0xFF312921, 0xFF191511);
            editor.draw(mouseX, mouseY);
            // TODO: textField.render() needs GuiGraphics parameter passed from parent
            // For now, textField rendering is disabled until proper GuiGraphics is available
            /*if (textField.isFocused()) {
                textField.render(guiGraphics, mouseX, mouseY, 0);
            }*/
        }
    }

    @Override
    public void charTyped(char character, int key) {
        if (editor != null) {
            if (this.textField.charTyped(character, key)) {
                text = textField.getValue().trim();
            }
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY) {
        if (editor != null) {
            if (mouseX >= EConfig.SETTINGS.editorXPos && mouseX <= EConfig.SETTINGS.editorXPos + 84 && mouseY >= EConfig.SETTINGS.toolbarYPos.get() - 3 && mouseY <= EConfig.SETTINGS.toolbarYPos.get() + 7) {
                this.textField.mouseClicked(mouseX, mouseY, 0);
                this.textField.setFocused(true);
                return true;
            } else {
                this.textField.setFocused(false);
                return editor.mouseClicked(mouseX, mouseY);

            }

        }
        return false;
    }

    @Override
    public void addToolTip(List<String> tooltip, int mouseX, int mouseY) {
        if (editor != null) {
            editor.addToolTip(tooltip, mouseX, mouseY);
        }
    }

    @Override
    public void scroll(boolean down, int mouseX, int mouseY) {
        if (editor != null) {
            editor.scroll(down, mouseX, mouseY);
        }
    }

    public String getText() {
        return textField.getValue();
    }
}