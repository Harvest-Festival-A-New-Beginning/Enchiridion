package joshie.enchiridion.library.handlers;

import joshie.enchiridion.api.book.IBookHandler;
import joshie.enchiridion.lib.EGuis;
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
        player.openMenu(EGuis.getBookProvider(stack, isShiftPressed));
    }
}