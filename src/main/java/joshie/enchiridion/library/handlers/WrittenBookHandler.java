package joshie.enchiridion.library.handlers;

import joshie.enchiridion.api.book.IBookHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;

import javax.annotation.Nonnull;

public class WrittenBookHandler implements IBookHandler {
    @Override
    public String getName() {
        return "written";
    }

    @Override
    public void handle(@Nonnull ItemStack stack, Player player, InteractionHand hand, int slotID, boolean isShiftPressed) {
        if (player.level().isClientSide) {
            player.openBook(stack, hand);
        }
    }
}