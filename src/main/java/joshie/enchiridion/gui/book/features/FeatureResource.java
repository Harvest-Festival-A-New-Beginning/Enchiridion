package joshie.enchiridion.gui.book.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.Enchiridion;
import joshie.enchiridion.data.book.FeatureProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Level;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class FeatureResource extends joshie.enchiridion.data.book.FeatureProvider {
    public static final Codec<FeatureResource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.optionalFieldOf("path", "").forGetter(f -> f.path)
    ).apply(instance, (path) -> {
        FeatureResource feature = new FeatureResource();
        feature.path = path;
        return feature;
    }));

    public FeatureResource() {
        super(0, 0, 0, 0);
    }

    public String path;

    protected transient ResourceLocation resource;
    public transient int img_width;
    public transient int img_height;
    public transient boolean attempted;

    @Override
    public FeatureProvider copy() {
        FeatureResource resource = new FeatureResource();
        resource.path = path;
        return resource;
    }

    @Override
    public void update(joshie.enchiridion.api.book.IPage page) { //Preload the resource
        super.update(page);
        attempted = loadResource();
    }

    public void setResource(ResourceLocation resource) {
        this.resource = new ResourceLocation(resource.getPath(), resource.getPath());
        this.path = resource.toString();
    }

    public ResourceLocation getResource() {
        return this.resource;
    }

    @Override
    protected void drawFeature(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (resource != null) {
            drawResource(guiGraphics, getX(), getY(), getWidth(), getHeight());
        } else if (!attempted) attempted = loadResource();
    }

    protected void drawResource(net.minecraft.client.gui.GuiGraphics guiGraphics, int xPos, int yPos, double width, double height) {
        // Use GuiGraphics with scaling via PoseStack
        com.mojang.blaze3d.vertex.PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        float scaleX = (float) width / 250F;
        float scaleY = (float) height / 250F;
        poseStack.scale(scaleX, scaleY, 1.0F);
        guiGraphics.blit(resource, (int) (xPos / scaleX), (int) (yPos / scaleY), 0, 0, img_width, img_height, img_width, img_height);
        poseStack.popPose();
    }

    protected String getResourcePath() {
        return path;
    }

    protected void readImage(String[] split) throws IOException {
        double splitX = split.length >= 3 ? Double.parseDouble(split[2]) : 1D;
        double splitY = split.length == 4 ? Double.parseDouble(split[3]) : 1D;
        BufferedImage image = ImageIO.read(Minecraft.getInstance().getResourceManager().getResource(resource).get().open());
        img_width = (int) (image.getWidth() / splitX);
        img_height = (int) (image.getHeight() / splitY);
    }

    private boolean loadResource() {
        String path = getResourcePath();
        if (path == null) return false;
        String[] split = path.split(":");
        if (split.length == 2 || split.length == 3 || split.length == 4)
            resource = new ResourceLocation(split[0], split[1]);
        try {
            readImage(split);
        } catch (Exception e) {
            Enchiridion.log(Level.ERROR, "Enchiridion 2 failed to read in the image at the following resource: ");
            Enchiridion.log(Level.ERROR, path);
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public Codec<? extends FeatureProvider> codec() {
        return CODEC;
    }
}