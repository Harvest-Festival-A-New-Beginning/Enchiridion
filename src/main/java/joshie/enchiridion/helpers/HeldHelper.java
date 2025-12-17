package joshie.enchiridion.helpers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;

import javax.annotation.Nonnull;

public class HeldHelper {
    @Nonnull
    public static ItemStack getStackFromOrdinal(Player player, int id) {
        return getStackFromHand(player, getHandFromOrdinal(id));
    }

    @Nonnull
    public static ItemStack getStackFromHand(Player player, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND)
            return player.getMainHandItem();
        if (hand == InteractionHand.OFF_HAND)
            return player.getOffhandItem();

        return ItemStack.EMPTY;
    }

    public static EquipmentSlot getSlotFromHand(InteractionHand hand) {
        return hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
    }

    public static InteractionHand getHandFromOrdinal(int id) {
        return InteractionHand.values()[id];
    }
}