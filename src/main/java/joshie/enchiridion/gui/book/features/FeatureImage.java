package joshie.enchiridion.gui.book.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.data.book.FeatureProvider;
import joshie.enchiridion.api.book.IPage;
import joshie.enchiridion.lib.EInfo;

import java.io.IOException;

public class FeatureImage extends FeatureResource {
    public static final Codec<FeatureImage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.optionalFieldOf("path", "").forGetter(f -> f.path)
    ).apply(instance, (path) -> {
        FeatureImage feature = new FeatureImage();
        feature.path = path;
        return feature;
    }));

    public transient String name;

    public FeatureImage() {
    }

    public FeatureImage(String path) {
        this.path = path;
    }

    @Override
    public FeatureProvider copy() {
        return new FeatureImage(path);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void update(IPage page) {
        super.update(page);
        name = path.replace(EInfo.MODID + ":images/", "").replace(".png", "").split("/")[1];
    }

    @Override
    protected String getResourcePath() {
        //If the path was created in 1.7, update it to use the new format
        if (!path.contains(":")) {
            path = EInfo.MODID + ":images/" + path;
        }

        return path;
    }

    @Override
    protected void drawResource(net.minecraft.client.gui.GuiGraphics guiGraphics, int xPos, int yPos, double width, double height) {
        if (resource != null) {
            int w = getRenderRight() - getRenderX();
            int h = getRenderBottom() - getRenderY();
            guiGraphics.blit(resource, getRenderX(), getRenderY(), 0, 0, w, h, w, h);
        }
    }

    @Override
    protected void readImage(String[] split) throws IOException {
    }

    @Override
    public Codec<? extends FeatureProvider> codec() {
        return CODEC;
    }
}