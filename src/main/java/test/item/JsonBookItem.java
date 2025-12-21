package test.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.test.data.book.BookDefinition;
import uk.joshiejack.penguinlib.test.data.book.BookRegistries;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Item that opens a specific JSON-defined book
 * Uses NBT to store which book to open
 */
public class JsonBookItem extends Item {
    private static final String BOOK_ID_TAG = "BookId";

    public JsonBookItem() {
        super(new Properties().stacksTo(1));
    }

    /**
     * Create a book item for a specific book definition
     */
    public static ItemStack createBookItem(ResourceLocation bookId) {
        ItemStack stack = new ItemStack(TestItems.JSON_BOOK.get());
        CompoundTag tag = new CompoundTag();
        tag.putString(BOOK_ID_TAG, bookId.toString());
        stack.setTag(tag);
        return stack;
    }

    /**
     * Get the book ID from an item stack
     */
    @Nullable
    private static ResourceLocation getBookId(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(BOOK_ID_TAG)) {
            return new ResourceLocation(tag.getString(BOOK_ID_TAG));
        }
        return null;
    }

    @NotNull
    @Override
    public Component getName(@NotNull ItemStack stack) {
        ResourceLocation bookId = getBookId(stack);
        if (bookId != null) {
            BookDefinition book = BookRegistries.BOOKS.get(bookId);
            if (book != null && !book.getTitle().isEmpty()) {
                return Component.translatable("book." + bookId.getNamespace() + "." + bookId.getPath());
            }
            return Component.literal(bookId.getPath().replace('_', ' '));
        }
        return Component.literal("Unknown Book");
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        ResourceLocation bookId = getBookId(stack);
        if (bookId != null) {
            tooltip.add(Component.literal("Book ID: " + bookId).withStyle(ChatFormatting.GRAY));
        }
        if (level != null && level.isClientSide) {
            Player player = Minecraft.getInstance().player;
            if (player != null && player.isCreative()) {
                tooltip.add(Component.literal("Hold Shift to edit").withStyle(ChatFormatting.GOLD));
            }
        }
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        ResourceLocation bookId = getBookId(stack);

        if (bookId != null) {
            if (!level.isClientSide) {
                // Check if player is in creative and holding shift for edit mode
                boolean editMode = player.isCreative() && player.isShiftKeyDown();

                // Open menu on server side
                player.openMenu(new net.minecraft.world.MenuProvider() {
                    @NotNull
                    @Override
                    public Component getDisplayName() {
                        return Component.translatable("book." + bookId.getNamespace() + "." + bookId.getPath());
                    }

                    @Override
                    public @NotNull net.minecraft.world.inventory.AbstractContainerMenu createMenu(int windowId, @NotNull net.minecraft.world.entity.player.Inventory inventory, @NotNull Player player) {
                        return new JsonBookMenu(windowId, bookId, editMode);
                    }
                }, buf -> {
                    buf.writeResourceLocation(bookId);
                    buf.writeBoolean(editMode);
                });
            }
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }
}
