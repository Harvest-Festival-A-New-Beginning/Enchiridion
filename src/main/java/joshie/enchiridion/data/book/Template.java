package joshie.enchiridion.data.book;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.api.book.IPage;
import joshie.enchiridion.api.book.ITemplate;
import joshie.enchiridion.lib.EInfo;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Template implements ITemplate {
    public static final Codec<Template> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("templatename").forGetter(t -> t.templatename),
        Page.FEATURE_CODEC.listOf().fieldOf("features").forGetter(t -> t.features),
        Codec.STRING.fieldOf("uniquename").forGetter(t -> t.uniquename)
    ).apply(instance, (templatename, features, uniquename) -> {
        Template template = new Template();
        template.templatename = templatename;
        template.features = new ArrayList<>(features);
        template.uniquename = uniquename;
        return template;
    }));

    private String templatename;
    private List<FeatureProvider> features;
    private String uniquename;

    private transient ResourceLocation location;

    public Template() {
    }

    public Template(String uniquename, String templatename, ResourceLocation location, IPage page) {
        this.uniquename = uniquename;
        this.templatename = templatename;
        this.location = location;
        this.features = new ArrayList<>();
        this.features.addAll(page.getFeatures().stream().map(FeatureProvider::copy).collect(Collectors.toList()));
    }

    public Template(String uniquename, String templatename, IPage page) {
        this.uniquename = uniquename;
        this.templatename = templatename;
        this.features = new ArrayList<>();
        this.features.addAll(page.getFeatures().stream().map(FeatureProvider::copy).collect(Collectors.toList()));
    }

    @Override
    public String getUniqueName() {
        return uniquename;
    }

    @Override
    public String getTemplateName() {
        return templatename;
    }

    @Override
    public ResourceLocation getIcon() {
        if (location == null) {
            location = new ResourceLocation(EInfo.MODID, "templates/" + uniquename + ".png");
        }

        return location;
    }

    @Override
    public List<FeatureProvider> getFeatures() {
        return features;
    }
}