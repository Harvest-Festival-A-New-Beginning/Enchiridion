package test.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.test.data.book.JsonBookSaver;

/**
 * Screen for entering the name of a new book to create
 */
@OnlyIn(Dist.CLIENT)
public class BookNameInputScreen extends Screen {
    private EditBox nameField;
    private Button createButton;
    private Button cancelButton;

    public BookNameInputScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Create text field for book name
        nameField = new EditBox(this.font, centerX - 100, centerY - 10, 200, 20, Component.literal("Book Name"));
        nameField.setMaxLength(50);
        nameField.setValue("");
        addRenderableWidget(nameField);

        // Create button
        createButton = Button.builder(Component.literal("Create"), button -> createBook())
                .bounds(centerX - 100, centerY + 20, 95, 20)
                .build();
        addRenderableWidget(createButton);

        // Cancel button
        cancelButton = Button.builder(Component.literal("Cancel"), button -> onClose())
                .bounds(centerX + 5, centerY + 20, 95, 20)
                .build();
        addRenderableWidget(cancelButton);

        setInitialFocus(nameField);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics, mouseX, mouseY, partialTicks);

        // Draw title
        graphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 40, 0xFFFFFF);

        // Draw label
        graphics.drawCenteredString(this.font, "Enter book name:", this.width / 2, this.height / 2 - 25, 0xAAAAAA);

        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    private void createBook() {
        String bookName = nameField.getValue().trim();

        if (bookName.isEmpty()) {
            Minecraft.getInstance().player.displayClientMessage(
                    Component.literal("Book name cannot be empty!"), true);
            return;
        }

        // Create the new book
        ResourceLocation bookId = JsonBookSaver.createNewBook(PenguinLib.MODID, bookName);

        if (bookId != null) {
            Minecraft.getInstance().player.displayClientMessage(
                    Component.literal("Created book: " + bookId), false);
            PenguinLib.LOGGER.info("Created new book: {}", bookId);
        } else {
            Minecraft.getInstance().player.displayClientMessage(
                    Component.literal("Failed to create book!"), true);
        }

        onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
