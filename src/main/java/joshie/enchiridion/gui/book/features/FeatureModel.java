package joshie.enchiridion.gui.book.features;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.api.book.IFeature;
import joshie.enchiridion.data.book.FeatureProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;

public class FeatureModel extends FeatureProvider {
    public static final Codec<FeatureModel> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("block").forGetter(f -> f.blockId),
        Codec.FLOAT.optionalFieldOf("scale", 1.0F).forGetter(f -> f.scale),
        Codec.FLOAT.optionalFieldOf("rotation_x", 30.0F).forGetter(f -> f.rotationX),
        Codec.FLOAT.optionalFieldOf("rotation_y", 45.0F).forGetter(f -> f.rotationY),
        Codec.BOOL.optionalFieldOf("follow_mouse", false).forGetter(f -> f.followMouse)
    ).apply(instance, (block, scale, rotX, rotY, followMouse) -> {
        FeatureModel feature = new FeatureModel();
        feature.blockId = block;
        feature.scale = scale;
        feature.rotationX = rotX;
        feature.rotationY = rotY;
        feature.followMouse = followMouse;
        return feature;
    }));

    public ResourceLocation blockId;
    public float scale = 1.0F;
    public float rotationX = 30.0F; // Pitch
    public float rotationY = 45.0F; // Yaw
    public boolean followMouse = false;

    private transient Block cachedBlock = null;
    private transient boolean failedToLoad = false;

    public FeatureModel() {
        super(0, 0, 0, 0);
    }

    public FeatureModel(ResourceLocation blockId) {
        super(0, 0, 0, 0);
        this.blockId = blockId;
    }

    @Override
    public FeatureProvider copy() {
        FeatureModel model = new FeatureModel(blockId);
        model.scale = scale;
        model.rotationX = rotationX;
        model.rotationY = rotationY;
        model.followMouse = followMouse;
        return model;
    }

    @Override
    public String getName() {
        if (blockId != null) {
            return "Model: " + blockId.getPath();
        }
        return "Model: unknown";
    }

    @Override
    public void update(joshie.enchiridion.api.book.IPage page) {
        super.update(page);
        cachedBlock = null;
        failedToLoad = false;
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

    @Override
    protected void drawFeature(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        Block block = getBlock();
        if (block == Blocks.AIR) return;

        BlockState state = block.defaultBlockState();

        // Calculate render position and size
        int centerX = (getLeft() + getRight()) / 2;
        int centerY = (getTop() + getBottom()) / 2;
        int size = Math.min(getWidth(), getHeight());
        float modelScale = size * scale * 0.5F;

        // Calculate mouse-based rotation if enabled
        float mouseRotX = rotationX;
        float mouseRotY = rotationY;

        if (followMouse && isOverFeature(mouseX, mouseY)) {
            float relX = (mouseX - centerX) / (float) getWidth();
            float relY = (mouseY - centerY) / (float) getHeight();
            mouseRotY += relX * 45.0F;
            mouseRotX += relY * 20.0F;
        }

        renderBlockModel(guiGraphics, state, centerX, centerY, modelScale, mouseRotX, mouseRotY);
    }

    private void renderBlockModel(GuiGraphics guiGraphics, BlockState state, int x, int y, float scale, float rotX, float rotY) {
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        // Translate to position
        poseStack.translate(x, y, 100.0);
        poseStack.scale(scale, -scale, scale);

        // Apply rotations
        Quaternionf quaternion = Axis.XP.rotationDegrees(rotX);
        Quaternionf quaternionY = Axis.YP.rotationDegrees(rotY);
        quaternion.mul(quaternionY);
        poseStack.mulPose(quaternion);

        // Render the block model
        Minecraft minecraft = Minecraft.getInstance();
        BlockRenderDispatcher blockRenderer = minecraft.getBlockRenderer();
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();

        try {
            BakedModel model = blockRenderer.getBlockModel(state);
            blockRenderer.getModelRenderer().renderModel(
                poseStack.last(),
                bufferSource.getBuffer(net.minecraft.client.renderer.RenderType.solid()),
                state,
                model,
                1.0F, 1.0F, 1.0F,
                15728880, // Full brightness
                net.minecraft.client.renderer.LevelRenderer.DIRECTIONS_IN_RENDER_ORDER
            );
        } catch (Exception e) {
            // Fallback - try rendering as item
            try {
                ItemStack itemStack = new ItemStack(state.getBlock());
                minecraft.getItemRenderer().renderStatic(
                    itemStack,
                    ItemDisplayContext.GUI,
                    15728880,
                    net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,
                    poseStack,
                    bufferSource,
                    minecraft.level,
                    0
                );
            } catch (Exception ignored) {
            }
        }

        bufferSource.endBatch();
        poseStack.popPose();
    }

    @Override
    public Codec<? extends IFeature> codec() {
        return CODEC;
    }
}
