package joshie.enchiridion.gui.book.element;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.helpers.StackHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ItemElement implements FeatureElement {
    public static final Codec<ItemElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.optionalFieldOf("item_string", "").forGetter(f -> f.itemString)
    ).apply(instance, ItemElement::new));

    private final String itemString;
    private transient ItemStack cachedStack = null;

    public ItemElement(String itemString) {
        this.itemString = itemString;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, int width, int height, float scale, int mouseX, int mouseY, float partialTicks) {
        if (cachedStack == null || cachedStack.isEmpty()) {
            cachedStack = StackHelper.getStackFromString(itemString);
        }

        if (!cachedStack.isEmpty()) {
            PoseStack poseStack = graphics.pose();
            poseStack.pushPose();
            float itemScale = width / 16F;
            poseStack.scale(itemScale, itemScale, itemScale);
            graphics.renderItem(cachedStack, (int)(x / itemScale), (int)(y / itemScale));
            poseStack.popPose();
        }
    }

    @Override
    public Codec<? extends FeatureElement> codec() {
        return CODEC;
    }

    public String getItemString() {
        return itemString;
    }

    public ItemStack getStack() {
        if (cachedStack == null) {
            cachedStack = StackHelper.getStackFromString(itemString);
        }
        return cachedStack;
    }
}
