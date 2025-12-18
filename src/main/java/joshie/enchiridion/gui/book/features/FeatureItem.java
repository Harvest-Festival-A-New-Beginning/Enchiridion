package joshie.enchiridion.gui.book.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.data.book.FeatureProvider;
import joshie.enchiridion.gui.book.GuiSimpleEditor;
import joshie.enchiridion.gui.book.GuiSimpleEditorItem;
import joshie.enchiridion.helpers.StackHelper;
import joshie.enchiridion.util.IItemSelectable;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

public class FeatureItem extends joshie.enchiridion.data.book.FeatureProvider implements IItemSelectable {
    public static final Codec<FeatureItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.optionalFieldOf("item_string", "").forGetter(f -> f.itemString),
        Codec.BOOL.optionalFieldOf("hide_tooltip", false).forGetter(f -> f.hideTooltip)
    ).apply(instance, (itemString, hideTooltip) -> {
        FeatureItem feature = new FeatureItem();
        feature.itemString = itemString;
        feature.hideTooltip = hideTooltip;
        return feature;
    }));

    public String itemString;
    public boolean hideTooltip;
    public transient float size;

    public FeatureItem() {
        super(0, 0, 0, 0);
    }

    public FeatureItem(@Nonnull ItemStack stack) {
        super(0, 0, 0, 0);
        setItemStack(stack);
    }
    @Nonnull
    public transient ItemStack stack = ItemStack.EMPTY;

    @Override
    public FeatureProvider copy() {
        FeatureItem item = new FeatureItem(stack);
        item.hideTooltip = hideTooltip;
        return item;
    }

    @Override
    public String getName() {
        return stack.isEmpty() ? super.getName() : stack.getHoverName().getString();
    }

    @Override
    public void update(joshie.enchiridion.api.book.IPage page) {
        super.update(page);
        int width = getWidth();
        setHeight(width);
        size = (float) (width / 16D);
    }

    @Override
    protected void drawFeature(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (stack.isEmpty() && itemString != null) {
            stack = StackHelper.getStackFromString(itemString);
        }

        if (!stack.isEmpty()) {
            // Use GuiGraphics with scaling via PoseStack
            com.mojang.blaze3d.vertex.PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();
            poseStack.scale(size, size, size);
            guiGraphics.renderItem(stack, (int)(getLeft() / size), (int)(getTop() / size));
            poseStack.popPose();
        }
    }

    @Override
    public void setItemStack(@Nonnull ItemStack stack) {
        this.stack = stack;
        this.itemString = StackHelper.getStringFromStack(stack);
    }

    @Override
    public void addTooltip(List<String> list, int mouseX, int mouseY) {
        if (!hideTooltip && !this.stack.isEmpty()) {
            // TODO: TooltipContext API changed in 1.20.4 - needs proper Item.TooltipContext
            // For now, just use the display name as a simple fallback
            list.add(stack.getHoverName().getString());
        }
    }

    @Override
    public boolean getAndSetEditMode() {
        GuiSimpleEditor.INSTANCE.setEditor(GuiSimpleEditorItem.INSTANCE.setItem(this));
        return false;
    }

    @Override
    public boolean getTooltipsEnabled() {
        return !hideTooltip;
    }

    @Override
    public void setTooltips(boolean value) {
        hideTooltip = !value;
    }

    @Override
    public Codec<? extends FeatureProvider> codec() {
        return CODEC;
    }
}