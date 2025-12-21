package joshie.enchiridion.gui.book;

import joshie.enchiridion.EConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.List;

public class GuiSimpleEditor extends AbstractGuiOverlay {
    private final GuiBook guiBook;
    private EditBox textField;
    private String text = "";
    private AbstractGuiOverlay editor = null;

    public GuiSimpleEditor(GuiBook guiBook) {
        this.guiBook = guiBook;
    }

    public String getText() {
        return textField != null ? textField.getValue() : "";
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
    public void draw(GuiGraphics guiGraphics, int mouseX, int mouseY, GuiBook guiBookParam) {
        if (editor != null) {
            /* Draw the Background */
            // Draw SIDEBAR image
            int left = EConfig.SETTINGS.editorXPos - 3;
            int top = EConfig.SETTINGS.toolbarYPos.get() - 7;
            int right = EConfig.SETTINGS.editorXPos + 87;
            int bottom = EConfig.SETTINGS.timelineYPos.get() + 13;
            int w = right - left;
            int h = bottom - top;
            int offsetX = guiBook.x;
            int offsetY = guiBook.y;
            guiGraphics.blit(SIDEBAR, offsetX + left, offsetY + top, 0, 0, w, h, w, h);

            // Draw bordered rectangles
            left = EConfig.SETTINGS.editorXPos;
            top = EConfig.SETTINGS.toolbarYPos.get() + 7;
            right = EConfig.SETTINGS.editorXPos + 85;
            bottom = EConfig.SETTINGS.timelineYPos.get() + 11;
            guiGraphics.fill(offsetX + left, offsetY + top, offsetX + right, offsetY + bottom, 0xFF312921);
            guiGraphics.fill(offsetX + left, offsetY + top, offsetX + right, offsetY + top + 1, 0xFF191511);
            guiGraphics.fill(offsetX + left, offsetY + bottom - 1, offsetX + right, offsetY + bottom, 0xFF191511);
            guiGraphics.fill(offsetX + left, offsetY + top, offsetX + left + 1, offsetY + bottom, 0xFF191511);
            guiGraphics.fill(offsetX + right - 1, offsetY + top, offsetX + right, offsetY + bottom, 0xFF191511);

            left = EConfig.SETTINGS.editorXPos + 2;
            top = EConfig.SETTINGS.toolbarYPos.get() + 9;
            right = EConfig.SETTINGS.editorXPos + 83;
            bottom = EConfig.SETTINGS.timelineYPos.get() + 9;
            guiGraphics.fill(offsetX + left, offsetY + top, offsetX + right, offsetY + bottom, 0xFFE4D6AE);
            guiGraphics.fill(offsetX + left, offsetY + top, offsetX + right, offsetY + top + 1, 0x5579725A);
            guiGraphics.fill(offsetX + left, offsetY + bottom - 1, offsetX + right, offsetY + bottom, 0x5579725A);
            guiGraphics.fill(offsetX + left, offsetY + top, offsetX + left + 1, offsetY + bottom, 0x5579725A);
            guiGraphics.fill(offsetX + right - 1, offsetY + top, offsetX + right, offsetY + bottom, 0x5579725A);

            left = EConfig.SETTINGS.editorXPos;
            top = EConfig.SETTINGS.toolbarYPos.get() - 3;
            right = EConfig.SETTINGS.editorXPos + 84;
            bottom = EConfig.SETTINGS.toolbarYPos.get() + 7;
            guiGraphics.fill(offsetX + left, offsetY + top, offsetX + right, offsetY + bottom, 0xFF312921);
            guiGraphics.fill(offsetX + left, offsetY + top, offsetX + right, offsetY + top + 1, 0xFF191511);
            guiGraphics.fill(offsetX + left, offsetY + bottom - 1, offsetX + right, offsetY + bottom, 0xFF191511);
            guiGraphics.fill(offsetX + left, offsetY + top, offsetX + left + 1, offsetY + bottom, 0xFF191511);
            guiGraphics.fill(offsetX + right - 1, offsetY + top, offsetX + right, offsetY + bottom, 0xFF191511);

            editor.draw(guiGraphics, mouseX, mouseY, guiBookParam);

            if (textField.isFocused()) {
                textField.render(guiGraphics, mouseX, mouseY, 0);
            }
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
    public boolean mouseClicked(int mouseX, int mouseY, GuiBook guiBook) {
        if (editor != null) {
            if (mouseX >= EConfig.SETTINGS.editorXPos && mouseX <= EConfig.SETTINGS.editorXPos + 84 && mouseY >= EConfig.SETTINGS.toolbarYPos.get() - 3 && mouseY <= EConfig.SETTINGS.toolbarYPos.get() + 7) {
                this.textField.mouseClicked(mouseX, mouseY, 0);
                this.textField.setFocused(true);
                return true;
            } else {
                this.textField.setFocused(false);
                return editor.mouseClicked(mouseX, mouseY, guiBook);

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
}