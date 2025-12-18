package joshie.enchiridion.gui.book.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.Enchiridion;
import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.api.book.IFeature;
import joshie.enchiridion.api.book.IFeatureProvider;
import net.minecraft.client.Minecraft;
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
    public IFeatureProvider copy() {
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
    protected void drawFeature(int mouseX, int mouseY) {
        if (resource != null) {
            drawResource(getLeft(), getTop(), getWidth(), getHeight());
        } else if (!attempted) attempted = loadResource();
    }

    protected void drawResource(int xPos, int yPos, double width, double height) {
        EnchiridionAPI.draw.drawResource(resource, xPos, yPos, img_width, img_height, (float) width / 250F, (float) height / 250F);
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
    public Codec<? extends joshie.enchiridion.api.book.IFeature> codec() {
        return CODEC;
    }
}