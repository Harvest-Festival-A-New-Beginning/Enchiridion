package test.data.book.element;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.util.icon.Icon;

/**
 * Renders an icon from the existing icon system
 * Position and size are stored in BookWidget, not here
 */
public class IconElement extends RenderElement {
    public static final Codec<IconElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Icon.CODEC.fieldOf("icon").forGetter(IconElement::getIcon)
    ).apply(instance, IconElement::new));

    private final Icon icon;

    public IconElement(Icon icon) {
        this.icon = icon;
    }

    @Override
    public Codec<? extends RenderElement> codec() {
        return CODEC;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, int width, int height, float scale, int mouseX, int mouseY, float partialTicks) {
        // Apply scale if not 1.0
        if (scale != 1.0f) {
            graphics.pose().pushPose();
            graphics.pose().translate(x, y, 0);
            graphics.pose().scale(scale, scale, 1.0f);
            icon.render(Minecraft.getInstance(), graphics, 0, 0);
            graphics.pose().popPose();
        } else {
            icon.render(Minecraft.getInstance(), graphics, x, y);
        }
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeByte(getType().ordinal());
        buf.writeByte(icon.getType().ordinal());
        icon.toNetwork(buf);
    }

    public static IconElement fromNetwork(FriendlyByteBuf buf) {
        Icon icon = Icon.fromNetwork(buf);
        return new IconElement(icon);
    }

    @Override
    public Type getType() {
        return Type.ICON;
    }

    public Icon getIcon() {
        return icon;
    }
}
