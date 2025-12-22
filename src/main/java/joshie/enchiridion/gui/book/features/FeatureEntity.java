package joshie.enchiridion.gui.book.features;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.data.book.FeatureProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;

public class FeatureEntity extends FeatureProvider {
    public static final Codec<FeatureEntity> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("entity_type").forGetter(f -> f.entityTypeId),
        Codec.FLOAT.optionalFieldOf("scale", 1.0F).forGetter(f -> f.scale),
        Codec.FLOAT.optionalFieldOf("rotation_x", 0.0F).forGetter(f -> f.rotationX),
        Codec.FLOAT.optionalFieldOf("rotation_y", 0.0F).forGetter(f -> f.rotationY),
        Codec.BOOL.optionalFieldOf("follow_mouse", true).forGetter(f -> f.followMouse),
        Codec.BOOL.optionalFieldOf("show_name", false).forGetter(f -> f.showName)
    ).apply(instance, (entityType, scale, rotX, rotY, followMouse, showName) -> {
        FeatureEntity feature = new FeatureEntity();
        feature.entityTypeId = entityType;
        feature.scale = scale;
        feature.rotationX = rotX;
        feature.rotationY = rotY;
        feature.followMouse = followMouse;
        feature.showName = showName;
        return feature;
    }));

    public ResourceLocation entityTypeId;
    public float scale = 1.0F;
    public float rotationX = 0.0F; // Pitch
    public float rotationY = 0.0F; // Yaw
    public boolean followMouse = true;
    public boolean showName = false;

    private transient Entity cachedEntity;
    private transient boolean failedToLoad = false;

    public FeatureEntity() {
        super(0, 0, 0, 0);
    }

    public FeatureEntity(ResourceLocation entityType) {
        super(0, 0, 0, 0);
        this.entityTypeId = entityType;
    }

    @Override
    public FeatureProvider copy() {
        FeatureEntity entity = new FeatureEntity(entityTypeId);
        entity.scale = scale;
        entity.rotationX = rotationX;
        entity.rotationY = rotationY;
        entity.followMouse = followMouse;
        entity.showName = showName;
        return entity;
    }

    @Override
    public String getName() {
        if (entityTypeId != null) {
            return "Entity: " + entityTypeId.getPath();
        }
        return "Entity: unknown";
    }

    @Override
    public void update(joshie.enchiridion.api.book.Page page) {
        super.update(page);
        // Clear cached entity when updating (in case entity type changed)
        cachedEntity = null;
        failedToLoad = false;
    }

    private Entity getOrCreateEntity() {
        if (failedToLoad) return null;
        if (cachedEntity != null) return cachedEntity;

        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return null;

            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(entityTypeId);
            if (type == null) {
                failedToLoad = true;
                return null;
            }

            cachedEntity = type.create(mc.level);
            if (cachedEntity == null) {
                failedToLoad = true;
                return null;
            }

            return cachedEntity;
        } catch (Exception e) {
            failedToLoad = true;
            return null;
        }
    }

    @Override
    protected void drawFeature(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        Entity entity = getOrCreateEntity();
        if (entity == null) return;

        // Calculate render position and size
        int centerX = (getX() + getRight()) / 2;
        int centerY = (getY() + getBottom()) / 2;
        int size = Math.min(getWidth(), getHeight());
        float entityScale = size * scale * 0.5F;

        // Calculate mouse-based rotation if enabled
        float mouseRotX = rotationX;
        float mouseRotY = rotationY;

        if (followMouse && isOverFeature(mouseX, mouseY)) {
            // Calculate rotation based on mouse position relative to center
            float relX = (mouseX - centerX) / (float) getWidth();
            float relY = (mouseY - centerY) / (float) getHeight();
            mouseRotY += relX * 45.0F; // Up to 45 degrees rotation based on mouse X
            mouseRotX += relY * 20.0F; // Up to 20 degrees rotation based on mouse Y
        }

        renderEntity(guiGraphics, entity, centerX, centerY, entityScale, mouseRotX, mouseRotY);
    }

    private void renderEntity(GuiGraphics guiGraphics, Entity entity, int x, int y, float scale, float rotX, float rotY) {
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        // Translate to position
        poseStack.translate(x, y, 50.0);
        poseStack.scale(scale, scale, -scale);

        // Apply rotations
        Quaternionf quaternion = Axis.ZP.rotationDegrees(180.0F);
        Quaternionf quaternionX = Axis.XP.rotationDegrees(rotX);
        Quaternionf quaternionY = Axis.YP.rotationDegrees(rotY);
        quaternion.mul(quaternionX);
        quaternion.mul(quaternionY);
        poseStack.mulPose(quaternion);

        // Set up lighting for entity rendering
        Minecraft minecraft = Minecraft.getInstance();
        EntityRenderDispatcher entityRenderDispatcher = minecraft.getEntityRenderDispatcher();

        // Configure entity for rendering
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.yBodyRot = 180.0F + rotY;
            livingEntity.yHeadRot = 180.0F + rotY;
            livingEntity.yHeadRotO = livingEntity.yHeadRot;
            livingEntity.yBodyRotO = livingEntity.yBodyRot;
        }

        entity.setYRot(180.0F + rotY);
        entity.setXRot(rotX);

        // Set up proper lighting
        RenderSystem.setShaderLights(
            new org.joml.Vector3f(0.2F, 1.0F, -1.0F),
            new org.joml.Vector3f(-0.2F, -1.0F, 0.0F)
        );

        // Render the entity
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        entityRenderDispatcher.render(
            entity,
            0.0,
            0.0,
            0.0,
            0.0F,
            1.0F,
            poseStack,
            bufferSource,
            15728880 // Full brightness
        );

        bufferSource.endBatch();

        poseStack.popPose();

        // Render name tag if enabled
        if (showName && entity.hasCustomName()) {
            poseStack.pushPose();
            poseStack.translate(x, y - (scale * 1.5F), 0);
            guiGraphics.drawCenteredString(
                minecraft.font,
                entity.getCustomName(),
                0,
                0,
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
