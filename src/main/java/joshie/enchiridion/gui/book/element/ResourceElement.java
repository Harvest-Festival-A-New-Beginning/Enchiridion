package joshie.enchiridion.gui.book.element;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.Enchiridion;
import joshie.enchiridion.data.book.Page;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

/**
 * Resource/image loading element with advanced scaling
 * Similar to ImageElement but with split support and auto-sizing
 */
public class ResourceElement implements FeatureElement {
    public static final Codec<ResourceElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.optionalFieldOf("path", "").forGetter(f -> f.path)
    ).apply(instance, ResourceElement::new));

    private String path;
    private transient ResourceLocation resource;
    private transient int imgWidth;
    private transient int imgHeight;
    private transient boolean attempted;

    public ResourceElement(String path) {
        this.path = path;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, int width, int height, float scale, int mouseX, int mouseY, float partialTicks) {
        if (resource != null) {
            PoseStack poseStack = graphics.pose();
            poseStack.pushPose();
            float scaleX = (float) width / 250F;
            float scaleY = (float) height / 250F;
            poseStack.scale(scaleX, scaleY, 1.0F);
            graphics.blit(resource, (int)(x / scaleX), (int)(y / scaleY), 0, 0, imgWidth, imgHeight, imgWidth, imgHeight);
            poseStack.popPose();
        } else if (!attempted) {
            loadResource();
        }
    }

    @Override
    public void onUpdate(@Nullable Page page) {
        attempted = loadResource();
    }

    private boolean loadResource() {
        if (path == null) return false;
        String[] split = path.split(":");
        if (split.length < 2) return false;
        
        resource = new ResourceLocation(split[0], split[1]);
        
        try {
            double splitX = split.length >= 3 ? Double.parseDouble(split[2]) : 1D;
            double splitY = split.length == 4 ? Double.parseDouble(split[3]) : 1D;
            
            var resourceOpt = Minecraft.getInstance().getResourceManager().getResource(resource);
            if (resourceOpt.isPresent()) {
                BufferedImage image = ImageIO.read(resourceOpt.get().open());
                imgWidth = (int)(image.getWidth() / splitX);
                imgHeight = (int)(image.getHeight() / splitY);
            }
        } catch (Exception e) {
            Enchiridion.log(Level.ERROR, "Failed to read image: " + path);
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public FeatureElement copy() {
        return new ResourceElement(path);
    }

    @Override
    public String getName() {
        return "Resource: " + (path != null ? path : "unknown");
    }

    @Override
    public Codec<? extends FeatureElement> codec() {
        return CODEC;
    }

    public void setResource(ResourceLocation resource) {
        this.resource = resource;
        this.path = resource.toString();
    }

    public ResourceLocation getResource() {
        return resource;
    }
}
