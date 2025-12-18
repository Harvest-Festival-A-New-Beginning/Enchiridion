package joshie.enchiridion.gui.book.features;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.data.book.FeatureProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

public class FeatureFluid extends FeatureProvider {
    public static final Codec<FeatureFluid> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("fluid").forGetter(f -> f.fluidId),
        Codec.INT.optionalFieldOf("amount", 1000).forGetter(f -> f.amount),
        Codec.BOOL.optionalFieldOf("show_amount", true).forGetter(f -> f.showAmount)
    ).apply(instance, (fluid, amount, showAmount) -> {
        FeatureFluid feature = new FeatureFluid();
        feature.fluidId = fluid;
        feature.amount = amount;
        feature.showAmount = showAmount;
        return feature;
    }));

    public ResourceLocation fluidId;
    public int amount = 1000; // Amount in millibuckets (1000 = 1 bucket)
    public boolean showAmount = true; // Show amount text overlay

    private transient Fluid cachedFluid = null;
    private transient boolean failedToLoad = false;

    public FeatureFluid() {
        super(0, 0, 0, 0);
    }

    public FeatureFluid(ResourceLocation fluidId) {
        super(0, 0, 0, 0);
        this.fluidId = fluidId;
    }

    @Override
    public FeatureProvider copy() {
        FeatureFluid fluid = new FeatureFluid(fluidId);
        fluid.amount = amount;
        fluid.showAmount = showAmount;
        return fluid;
    }

    @Override
    public String getName() {
        if (fluidId != null) {
            return "Fluid: " + fluidId.getPath();
        }
        return "Fluid: unknown";
    }

    @Override
    public void update(joshie.enchiridion.api.book.IPage page) {
        super.update(page);
        cachedFluid = null;
        failedToLoad = false;
    }

    private Fluid getFluid() {
        if (failedToLoad) return Fluids.EMPTY;
        if (cachedFluid != null) return cachedFluid;

        try {
            cachedFluid = BuiltInRegistries.FLUID.get(fluidId);
            if (cachedFluid == null || cachedFluid == Fluids.EMPTY) {
                failedToLoad = true;
                return Fluids.EMPTY;
            }
            return cachedFluid;
        } catch (Exception e) {
            failedToLoad = true;
            return Fluids.EMPTY;
        }
    }

    @Override
    protected void drawFeature(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        Fluid fluid = getFluid();
        if (fluid == Fluids.EMPTY) return;

        FluidStack fluidStack = new FluidStack(fluid, amount);
        if (fluidStack.isEmpty()) return;

        // Get fluid rendering properties
        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluid);
        ResourceLocation stillTexture = fluidTypeExtensions.getStillTexture();

        if (stillTexture == null) return;

        // Get the texture sprite
        TextureAtlasSprite sprite = Minecraft.getInstance()
            .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
            .apply(stillTexture);

        // Get fluid color (tint)
        int color = fluidTypeExtensions.getTintColor(fluidStack);
        float red = ((color >> 16) & 0xFF) / 255.0F;
        float green = ((color >> 8) & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        float alpha = ((color >> 24) & 0xFF) / 255.0F;

        // Render the fluid
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        RenderSystem.setShaderColor(red, green, blue, alpha);
        RenderSystem.enableBlend();

        int left = getLeft();
        int top = getTop();
        int width = getWidth();
        int height = getHeight();

        // Draw fluid texture tiled to fill the area
        int texWidth = 16;
        int texHeight = 16;

        for (int y = 0; y < height; y += texHeight) {
            for (int x = 0; x < width; x += texWidth) {
                int drawWidth = Math.min(texWidth, width - x);
                int drawHeight = Math.min(texHeight, height - y);

                // Use blitSprite to render the texture atlas sprite
                guiGraphics.blitSprite(
                    stillTexture,
                    left + x,
                    top + y,
                    drawWidth,
                    drawHeight
                );
            }
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();

        poseStack.popPose();

        // Draw amount overlay if enabled
        if (showAmount) {
            String amountText = (amount / 1000) + "." + (amount % 1000 / 100) + "B";
            int centerX = left + width / 2;
            int centerY = top + height / 2;

            poseStack.pushPose();
            // Draw text with shadow for visibility
            guiGraphics.drawCenteredString(
                Minecraft.getInstance().font,
                amountText,
                centerX,
                centerY,
                0xFFFFFF
            );
            poseStack.popPose();
        }
    }

    @Override
    public Codec<? extends FeatureProvider> codec() {
        return CODEC;
    }
}
