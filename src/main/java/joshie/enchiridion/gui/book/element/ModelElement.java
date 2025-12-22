package joshie.enchiridion.gui.book.element;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.api.book.Page;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;

import javax.annotation.Nullable;

public class ModelElement implements FeatureElement {
    public static final Codec<ModelElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("block").forGetter(f -> f.blockId),
        Codec.FLOAT.optionalFieldOf("scale", 1.0F).forGetter(f -> f.scale),
        Codec.FLOAT.optionalFieldOf("rotation_x", 30.0F).forGetter(f -> f.rotationX),
        Codec.FLOAT.optionalFieldOf("rotation_y", 45.0F).forGetter(f -> f.rotationY),
        Codec.BOOL.optionalFieldOf("follow_mouse", false).forGetter(f -> f.followMouse)
    ).apply(instance, ModelElement::new));

    private ResourceLocation blockId;
    private float scale;
    private float rotationX;
    private float rotationY;
    private boolean followMouse;
    private transient Block cachedBlock;
    private transient boolean failedToLoad;

    public ModelElement(ResourceLocation blockId, float scale, float rotationX, float rotationY, boolean followMouse) {
        this.blockId = blockId;
        this.scale = scale;
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.followMouse = followMouse;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, int width, int height, float scale, int mouseX, int mouseY, float partialTicks) {
        Block block = getBlock();
        if (block == Blocks.AIR) return;

        int centerX = x + width / 2;
        int centerY = y + height / 2;
        int size = Math.min(width, height);
        float modelScale = size * this.scale * 0.5F;

        float mouseRotX = rotationX;
        float mouseRotY = rotationY;

        if (followMouse && mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height) {
            float relX = (mouseX - centerX) / (float) width;
            float relY = (mouseY - centerY) / (float) height;
            mouseRotY += relX * 45.0F;
            mouseRotX += relY * 20.0F;
        }

        renderBlockModel(graphics, block.defaultBlockState(), centerX, centerY, modelScale, mouseRotX, mouseRotY);
    }

    private Block getBlock() {
        if (failedToLoad) return Blocks.AIR;
        if (cachedBlock != null) return cachedBlock;
        try {
            cachedBlock = BuiltInRegistries.BLOCK.get(blockId);
            if (cachedBlock == null || cachedBlock == Blocks.AIR) {
                failedToLoad = true;
                return Blocks.AIR;
            }
            return cachedBlock;
        } catch (Exception e) {
            failedToLoad = true;
            return Blocks.AIR;
        }
    }

    private void renderBlockModel(GuiGraphics graphics, BlockState state, int x, int y, float scale, float rotX, float rotY) {
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(x, y, 100.0);
        poseStack.scale(scale, -scale, scale);
        
        Quaternionf quaternion = Axis.XP.rotationDegrees(rotX);
        quaternion.mul(Axis.YP.rotationDegrees(rotY));
        poseStack.mulPose(quaternion);

        Minecraft mc = Minecraft.getInstance();
        BlockRenderDispatcher renderer = mc.getBlockRenderer();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        try {
            var model = renderer.getBlockModel(state);
            renderer.getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(net.minecraft.client.renderer.RenderType.solid()), state, model, 1.0F, 1.0F, 1.0F, 15728880, OverlayTexture.NO_OVERLAY);
        } catch (Exception e) {
            try {
                ItemStack itemStack = new ItemStack(state.getBlock());
                mc.getItemRenderer().renderStatic(itemStack, ItemDisplayContext.GUI, 15728880, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, mc.level, 0);
            } catch (Exception ignored) {}
        }

        bufferSource.endBatch();
        poseStack.popPose();
    }

    @Override
    public void onUpdate(@Nullable Page page) {
        cachedBlock = null;
        failedToLoad = false;
    }

    @Override
    public FeatureElement copy() {
        return new ModelElement(blockId, scale, rotationX, rotationY, followMouse);
    }

    @Override
    public String getName() {
        return blockId != null ? "Model: " + blockId.getPath() : "Model: unknown";
    }

    @Override
    public Codec<? extends FeatureElement> codec() {
        return CODEC;
    }
}
