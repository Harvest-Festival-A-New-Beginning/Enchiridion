package test.item;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.test.client.gui.BookNameInputScreen;

/**
 * Creative-only item that opens a screen to create a new book
 */
public class BookCreatorItem extends Item {
    public BookCreatorItem(Properties properties) {
        super(properties);
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (level.isClientSide && player.isCreative()) {
            openNameInputScreen();
        }

        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @OnlyIn(Dist.CLIENT)
    private void openNameInputScreen() {
        Minecraft.getInstance().setScreen(new BookNameInputScreen(Component.literal("Create New Book")));
    }
}
