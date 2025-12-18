package joshie.enchiridion.data.book;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.api.book.IPage;
import joshie.enchiridion.api.book.ITemplate;
import joshie.enchiridion.lib.EInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import uk.joshiejack.penguinlib.util.registry.ReloadableRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Template implements ReloadableRegistry.PenguinRegistry<Template>, ITemplate {
    public static final Codec<Template> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("id").forGetter(Template::id),
        Codec.STRING.fieldOf("template_name").forGetter(t -> t.templatename),
        Codec.STRING.fieldOf("unique_name").forGetter(t -> t.uniquename),
        ResourceLocation.CODEC.optionalFieldOf("icon", new ResourceLocation(EInfo.MODID, "templates/default.png")).forGetter(Template::getIcon),
        Page.FEATURE_CODEC.listOf().fieldOf("features").forGetter(t -> t.features)
    ).apply(instance, (id, templatename, uniquename, icon, features) -> {
        Template template = new Template();
        template.templateId = id;
        template.templatename = templatename;
        template.uniquename = uniquename;
        template.location = icon;
        template.features = new ArrayList<>(features);
        return template;
    }));

    private ResourceLocation templateId;
    private String templatename;
    private List<FeatureProvider> features;
    private String uniquename;

    private transient ResourceLocation location;

    public Template() {
    }

    public Template(ResourceLocation id, String uniquename, String templatename, ResourceLocation location, IPage page) {
        this.templateId = id;
        this.uniquename = uniquename;
        this.templatename = templatename;
        this.location = location;
        this.features = new ArrayList<>();
        this.features.addAll(page.getFeatures().stream().map(FeatureProvider::copy).collect(Collectors.toList()));
    }

    public Template(ResourceLocation id, String uniquename, String templatename, IPage page) {
        this.templateId = id;
        this.uniquename = uniquename;
        this.templatename = templatename;
        this.features = new ArrayList<>();
        this.features.addAll(page.getFeatures().stream().map(FeatureProvider::copy).collect(Collectors.toList()));
    }

    // ===== PenguinRegistry Implementation =====

    @Override
    public ResourceLocation id() {
        return templateId != null ? templateId : new ResourceLocation(EInfo.MODID, uniquename != null ? uniquename : "default");
    }

    @Override
    public Template fromNetwork(FriendlyByteBuf buf) {
        // Network serialization if needed
        return this;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf) {
        // Network serialization if needed
    }

    // ===== ITemplate Implementation =====

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