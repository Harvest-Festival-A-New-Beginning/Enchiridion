package joshie.enchiridion.items;

import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.api.book.IBookHandler;
import joshie.enchiridion.lib.EGuis;
import joshie.enchiridion.library.LibraryHelper;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemLibrary extends Item {

    public ItemLibrary(Properties properties) {
        super(properties);
    }

    @Override
    public int getItemStackLimit(@Nonnull ItemStack stack) {
        return 1;
    }

    @Override
    @Nonnull
    public Component getName(@Nonnull ItemStack stack) {
        return Component.translatable(this.getDescriptionId(stack)).withStyle(ChatFormatting.GOLD);
    }

    @Override
    @Nonnull
    public InteractionResultHolder<ItemStack> use(Level world, Player player, @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (world.isClientSide) return InteractionResultHolder.fail(stack);

        if (player.isShiftKeyDown()) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.openMenu(EGuis.getLibraryProvider(), buf -> buf.writeInt(hand.ordinal()));
                return InteractionResultHolder.success(stack);
            }
        } else {
            int currentBook = LibraryHelper.getLibraryContents(player).getCurrentBook();
            ItemStack book = LibraryHelper.getLibraryContents(player).getStackInSlot(currentBook);
            if (!book.isEmpty()) {
                IBookHandler handler = EnchiridionAPI.library.getBookHandlerForStack(book);
                if (handler != null && player instanceof ServerPlayer) {
                    handler.handle(book, (ServerPlayer) player, hand, currentBook, player.isShiftKeyDown());
                }
            } else {
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.openMenu(EGuis.getLibraryProvider(), buf -> buf.writeInt(hand.ordinal()));
                    return InteractionResultHolder.success(stack);
                }
            }
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        /*int currentBook = LibraryHelper.getClientLibraryContents().getCurrentBook(); //TODO Crashes on client startup
        ItemStack internal = LibraryHelper.getClientLibraryContents().getStackInSlot(currentBook);
        if (!internal.isEmpty()) {
            tooltip.addAll(internal.getTooltip(Minecraft.getInstance().player, flag));
        }*/
    }
}