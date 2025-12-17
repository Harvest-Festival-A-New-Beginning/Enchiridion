package joshie.enchiridion.items;

import joshie.enchiridion.EClientHandler;
import joshie.enchiridion.Enchiridion;
import joshie.enchiridion.api.book.IBook;
import joshie.enchiridion.data.book.BookRegistry;
import joshie.enchiridion.gui.book.GuiBook;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.ChatFormatting.DARK_GREEN;
import static net.minecraft.ChatFormatting.RESET;

public class ItemBook extends Item {

    public ItemBook(Properties properties) {
        super(properties);
    }

    // getItemStackLimit removed in 1.20.4 - set in Item.Properties instead

    @Override
    @Nonnull
    public Component getName(@Nonnull ItemStack stack) {
        IBook book = BookRegistry.INSTANCE.getBook(stack);
        return book == null ? Component.translatable(Enchiridion.format("new", DARK_GREEN, RESET)) : Component.literal(book.getHoverName());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        IBook book = BookRegistry.INSTANCE.getBook(stack);
        if (book != null) {
            book.addInformation(tooltip);
        }
    }

    @Override
    @Nonnull
    public InteractionResultHolder<ItemStack> use(Level world, Player player, @Nonnull InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);

        if (!held.isEmpty()) {
            IBook book = BookRegistry.INSTANCE.getBook(held);
            if (book != null) {
                if (world.isClientSide) {
                    GuiBook.INSTANCE.setBook(book, player.isShiftKeyDown());
                    EClientHandler.openGuiBook();
                }
            } else {
                if (world.isClientSide) {
                    EClientHandler.openGuiBookCreate();
                }
            }
        }
        return InteractionResultHolder.success(held);
    }

    // TODO: fillItemCategory was removed in 1.20.4
    // Need to register creative tab items via CreativeModeTabEvent.BuildContents event
    // For now, commenting out to fix compilation
    /*@Override
    public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> list) {
        if (this.allowedIn(group)) {
            super.fillItemCategory(group, list);
            for (String uniqueName : BookRegistry.INSTANCE.getUniqueNames()) {
                ItemStack stack = new ItemStack(this);
                stack.setTag(new CompoundTag());
                if (stack.getTag() != null) {
                    stack.getTag().putString("identifier", uniqueName);
                }
                list.add(stack);
            }
        }
    }*/
}