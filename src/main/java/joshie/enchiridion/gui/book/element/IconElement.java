package joshie.enchiridion.gui.book.element;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.util.icon.Icon;

public class IconElement implements FeatureElement {
    public static final Codec<IconElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Icon.CODEC.fieldOf("icon").forGetter(f -> f.icon)
    ).apply(instance, IconElement::new));

    private final Icon icon;

    public IconElement(Icon icon) {
        this.icon = icon;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, int width, int height, float scale, int mouseX, int mouseY, float partialTicks) {
        if (icon != null) {
            icon.render(Minecraft.getInstance(), graphics, x, y);
        }
    }

    @Override
    public Codec<? extends FeatureElement> codec() {
        return CODEC;
    }

    @Override
    public FeatureElement copy() {
        return new IconElement(icon);
    }

    public Icon getIcon() {
        return icon;
    }
}
