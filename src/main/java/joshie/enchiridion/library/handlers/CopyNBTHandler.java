package joshie.enchiridion.library.handlers;

import joshie.enchiridion.library.LibraryHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;

import javax.annotation.Nonnull;

public class CopyNBTHandler extends TemporarySwitchHandler {
    @Override
    public String getName() {
        return "copynbt";
    }

    @Override
    public void handle(@Nonnull ItemStack stack, Player player, InteractionHand hand, int slotID, boolean isShiftPressed) {
        ItemStack library = player.getItemInHand(hand);
        if (library.hasTag()) { //Copy the current configs for the library item to the item itself, to be saved
            library.setTag(library.getTag());
            LibraryHelper.markDirty();
        } else library.setTag(new CompoundTag());

        super.handle(stack, player, hand, slotID, isShiftPressed);
    }
}