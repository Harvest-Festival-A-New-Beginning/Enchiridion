package joshie.enchiridion.library.handlers;

import joshie.enchiridion.EClientHandler;
import joshie.enchiridion.api.book.IBookHandler;
import joshie.enchiridion.data.book.BookRegistry;
import joshie.enchiridion.gui.book.GuiBook;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;

import javax.annotation.Nonnull;

public class EnchiridionBookHandler implements IBookHandler {
    @Override
    public String getName() {
        return "enchiridion";
    }

    @Override
    public void handle(@Nonnull ItemStack stack, Player player, InteractionHand hand, int slotID, boolean isShiftPressed) {
        if (player.level().isClientSide) {
            GuiBook.INSTANCE.setBook(BookRegistry.INSTANCE.getBook(stack), isShiftPressed);
            EClientHandler.openGuiBook();
        }
    }
}