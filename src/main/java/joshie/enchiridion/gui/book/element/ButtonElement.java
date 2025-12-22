package joshie.enchiridion.gui.book.element;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Button rendering element - renders button background and text
 * Button actions are handled by FeatureButton wrapper
 */
public class ButtonElement implements FeatureElement {
    public static final Codec<ButtonElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.optionalFieldOf("texture_normal", new ResourceLocation("textures/gui/widgets.png")).forGetter(f -> f.textureNormal),
        ResourceLocation.CODEC.optionalFieldOf("texture_hover", new ResourceLocation("textures/gui/widgets.png")).forGetter(f -> f.textureHover)
    ).apply(instance, ButtonElement::new));

    private final ResourceLocation textureNormal;
    private final ResourceLocation textureHover;

    public ButtonElement(ResourceLocation textureNormal, ResourceLocation textureHover) {
        this.textureNormal = textureNormal;
        this.textureHover = textureHover;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, int width, int height, float scale, int mouseX, int mouseY, float partialTicks) {
        boolean hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
        ResourceLocation texture = hovered ? textureHover : textureNormal;

        if (texture != null) {
            graphics.blit(texture, x, y, 0, 0, width, height, width, height);
        }
    }

    @Override
    public Codec<? extends FeatureElement> codec() {
        return CODEC;
    }

    @Override
    public FeatureElement copy() {
        return new ButtonElement(textureNormal, textureHover);
    }
}
