package joshie.enchiridion.gui.book;

import com.google.common.base.CaseFormat;
import joshie.enchiridion.data.book.Book;
import joshie.enchiridion.data.book.BookRegistry;
import joshie.enchiridion.gui.book.buttons.ButtonChangeIcon;
import joshie.enchiridion.lib.EInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GuiBookCreate extends GuiBase {
    public static final GuiBookCreate INSTANCE = new GuiBookCreate();
    private EditBox textField;
    private String text;

    private GuiBookCreate() {
        super(EInfo.MODID, null, null, Component.translatable("enchiridion.bookCreate.title"));
    }

    public GuiBookCreate setStack(@Nonnull ItemStack stack) {
        this.text = "";
        return this;
    }

    @Override
    public void containerTick() {
        // textField.tick() was removed in 1.20.4
    }

    @Nullable
    @Override
    public GuiEventListener getFocused() {
        return this.textField;
    }

    @Override
    public void init() {
        // TODO: setRepeatEvents() removed in 1.20.4 - need to find alternative API
        // Minecraft.getInstance().keyboardHandler.setRepeatEvents(true);

        this.textField = new EditBox(this.font, this.width / 2 - 101, height / 2 - 57, 202, 20, Component.literal("enchiridion.bookCreate.title"));
        this.textField.setMaxLength(32767);
        this.textField.setFocused(true);
        this.textField.setCanLoseFocus(false);
        this.textField.setValue(text != null && !text.isEmpty() ? text : "");
    }

    @Override
    public void removed() {
        // TODO: setRepeatEvents() removed in 1.20.4 - need to find alternative API
        // Minecraft.getInstance().keyboardHandler.setRepeatEvents(false);
        ButtonChangeIcon.refreshResources();
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (p_keyPressed_1_ == GLFW.GLFW_KEY_ENTER || p_keyPressed_1_ == GLFW.GLFW_KEY_KP_ENTER) {
            if (!text.isEmpty()) {
                String sanitized = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, text).replaceAll("[^A-Za-z0-9]",".").replace(".", "_");
                ResourceLocation bookId = new ResourceLocation(EInfo.MODID, sanitized);
                Book book = Book.create(bookId, text); //Create the book
                BookRegistry.INSTANCE.register(book); //Register the book
                GuiBook.INSTANCE.setBook(book, true);
                GuiBook.INSTANCE.removed(); //Save the data to json
                this.onClose();
                return true;
            }
            return false;
        } else {
            return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
        }
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    public boolean charTyped(char key, int keycode) {
        if (this.textField.charTyped(key, keycode)) {
            text = textField.getValue().trim();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        this.textField.mouseClicked(mx, my, button);
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
        super.render(guiGraphics, mouseX, mouseY, partial);

        this.textField.render(guiGraphics, mouseX, mouseY, partial);
    }
}