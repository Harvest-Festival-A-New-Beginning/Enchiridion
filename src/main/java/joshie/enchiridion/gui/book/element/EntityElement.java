package joshie.enchiridion.gui.book.element;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.data.book.Page;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;

import javax.annotation.Nullable;

public class EntityElement implements FeatureElement {
    public static final Codec<EntityElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("entity_type").forGetter(f -> f.entityTypeId),
        Codec.FLOAT.optionalFieldOf("scale", 1.0F).forGetter(f -> f.scale),
        Codec.FLOAT.optionalFieldOf("rotation_x", 0.0F).forGetter(f -> f.rotationX),
        Codec.FLOAT.optionalFieldOf("rotation_y", 0.0F).forGetter(f -> f.rotationY),
        Codec.BOOL.optionalFieldOf("follow_mouse", true).forGetter(f -> f.followMouse),
        Codec.BOOL.optionalFieldOf("show_name", false).forGetter(f -> f.showName)
    ).apply(instance, EntityElement::new));

    private ResourceLocation entityTypeId;
    private float scale;
    private float rotationX;
    private float rotationY;
    private boolean followMouse;
    private boolean showName;
    private transient Entity cachedEntity;
    private transient boolean failedToLoad = false;

    public EntityElement(ResourceLocation entityTypeId, float scale, float rotationX, float rotationY, boolean followMouse, boolean showName) {
        this.entityTypeId = entityTypeId;
        this.scale = scale;
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.followMouse = followMouse;
        this.showName = showName;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, int width, int height, float scale, int mouseX, int mouseY, float partialTicks) {
        Entity entity = getOrCreateEntity();
        if (entity == null) return;

        int centerX = x + width / 2;
        int centerY = y + height / 2;
        int size = Math.min(width, height);
        float entityScale = size * this.scale * 0.5F;

        float mouseRotX = rotationX;
        float mouseRotY = rotationY;

        if (followMouse && mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height) {
            float relX = (mouseX - centerX) / (float) width;
            float relY = (mouseY - centerY) / (float) height;
            mouseRotY += relX * 45.0F;
            mouseRotX += relY * 20.0F;
        }

        renderEntity(graphics, entity, centerX, centerY, entityScale, mouseRotX, mouseRotY);
    }

    private Entity getOrCreateEntity() {
        if (failedToLoad) return null;
        if (cachedEntity != null) return cachedEntity;
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return null;
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(entityTypeId);
            if (type == null) { failedToLoad = true; return null; }
            cachedEntity = type.create(mc.level);
            if (cachedEntity == null) { failedToLoad = true; return null; }
            return cachedEntity;
        } catch (Exception e) {
            failedToLoad = true;
            return null;
        }
    }

    private void renderEntity(GuiGraphics graphics, Entity entity, int x, int y, float scale, float rotX, float rotY) {
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(x, y, 50.0);
        poseStack.scale(scale, scale, -scale);
        
        Quaternionf quaternion = Axis.ZP.rotationDegrees(180.0F);
        quaternion.mul(Axis.XP.rotationDegrees(rotX));
        quaternion.mul(Axis.YP.rotationDegrees(rotY));
        poseStack.mulPose(quaternion);

        Minecraft minecraft = Minecraft.getInstance();
        EntityRenderDispatcher dispatcher = minecraft.getEntityRenderDispatcher();
        
        if (entity instanceof LivingEntity living) {
            living.yBodyRot = 180.0F + rotY;
            living.yHeadRot = 180.0F + rotY;
            living.yHeadRotO = living.yHeadRot;
            living.yBodyRotO = living.yBodyRot;
        }
        
        entity.setYRot(180.0F + rotY);
        entity.setXRot(rotX);
        
        RenderSystem.setShaderLights(new org.joml.Vector3f(0.2F, 1.0F, -1.0F), new org.joml.Vector3f(-0.2F, -1.0F, 0.0F));
        
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        dispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, poseStack, bufferSource, 15728880);
        bufferSource.endBatch();
        poseStack.popPose();
    }

    @Override
    public void onUpdate(@Nullable Page page) {
        cachedEntity = null;
        failedToLoad = false;
    }

    @Override
    public FeatureElement copy() {
        return new EntityElement(entityTypeId, scale, rotationX, rotationY, followMouse, showName);
    }

    @Override
    public String getName() {
        return entityTypeId != null ? "Entity: " + entityTypeId.getPath() : "Entity: unknown";
    }

    @Override
    public Codec<? extends FeatureElement> codec() {
        return CODEC;
    }
}
