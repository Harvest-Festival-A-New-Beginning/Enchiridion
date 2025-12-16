package joshie.enchiridion.helpers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.inventory.EquipmentSlotType;
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
            return player.getHeldItemMainhand();
        if (hand == InteractionHand.OFF_HAND)
            return player.getHeldItemOffhand();

        return ItemStack.EMPTY;
    }

    public static EquipmentSlotType getSlotFromHand(InteractionHand hand) {
        return hand == InteractionHand.MAIN_HAND ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND;
    }

    public static InteractionHand getHandFromOrdinal(int id) {
        return InteractionHand.values()[id];
    }
}