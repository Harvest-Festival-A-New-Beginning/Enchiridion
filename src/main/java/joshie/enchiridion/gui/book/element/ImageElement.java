package joshie.enchiridion.gui.book.element;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ImageElement implements FeatureElement {
    public static final Codec<ImageElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("resource").forGetter(f -> f.resource)
    ).apply(instance, ImageElement::new));

    private final ResourceLocation resource;

    public ImageElement(ResourceLocation resource) {
        this.resource = resource;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, int width, int height, float scale, int mouseX, int mouseY, float partialTicks) {
        if (resource != null) {
            graphics.blit(resource, x, y, 0, 0, width, height, width, height);
        }
    }

    @Override
    public Codec<? extends FeatureElement> codec() {
        return CODEC;
    }

    @Override
    public FeatureElement copy() {
        return new ImageElement(resource);
    }

    public ResourceLocation getResource() {
        return resource;
    }
}
