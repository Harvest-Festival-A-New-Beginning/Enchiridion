package joshie.enchiridion.gui.book.element;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;

public class FluidElement implements FeatureElement {
    public static final Codec<FluidElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("fluid").forGetter(f -> f.fluidId),
        Codec.INT.optionalFieldOf("amount", 1000).forGetter(f -> f.amount)
    ).apply(instance, FluidElement::new));

    private final ResourceLocation fluidId;
    private final int amount;
    private transient Fluid cachedFluid = null;

    public FluidElement(ResourceLocation fluidId, int amount) {
        this.fluidId = fluidId;
        this.amount = amount;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, int width, int height, float scale, int mouseX, int mouseY, float partialTicks) {
        Fluid fluid = getFluid();
        if (fluid == Fluids.EMPTY) return;

        IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluid);
        ResourceLocation stillTexture = extensions.getStillTexture();
        if (stillTexture == null) return;

        TextureAtlasSprite sprite = Minecraft.getInstance()
            .getTextureAtlas(net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS)
            .apply(stillTexture);

        if (sprite != null) {
            int tint = extensions.getTintColor();
            float r = ((tint >> 16) & 0xFF) / 255f;
            float g = ((tint >> 8) & 0xFF) / 255f;
            float b = (tint & 0xFF) / 255f;

            // Simple fluid rendering - tile the texture
            graphics.setColor(r, g, b, 1.0f);
            graphics.blit(x, y, 0, width, height, sprite);
            graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    private Fluid getFluid() {
        if (cachedFluid == null) {
            cachedFluid = BuiltInRegistries.FLUID.get(fluidId);
            if (cachedFluid == null) cachedFluid = Fluids.EMPTY;
        }
        return cachedFluid;
    }

    @Override
    public Codec<? extends FeatureElement> codec() {
        return CODEC;
    }
}
