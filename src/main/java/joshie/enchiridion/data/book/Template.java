package joshie.enchiridion.data.book;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.api.book.Page;
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
        ResourceLocation.CODEC.optionalFieldOf("icon", new ResourceLocation(EInfo.MODID, "templates/default.png")).forGetter(Template::getIcon),
            FeatureProvider.FEATURE.listOf().fieldOf("features").forGetter(t -> t.features)
    ).apply(instance, (id, templatename, icon, features) -> {
        Template template = new Template();
        template.templateId = id;
        template.templatename = templatename;
        template.location = icon;
        template.features = new ArrayList<>(features);
        return template;
    }));

    private ResourceLocation templateId;
    private String templatename;
    private List<FeatureProvider> features;

    private transient ResourceLocation location;

    public Template() {
    }

    public Template(ResourceLocation id, String templatename, ResourceLocation location, Page page) {
        this.templateId = id;
        this.templatename = templatename;
        this.location = location;
        this.features = new ArrayList<>();
        this.features.addAll(page.getFeatures().stream().map(FeatureProvider::copy).collect(Collectors.toList()));
    }

    public Template(ResourceLocation id, String templatename, Page page) {
        this.templateId = id;
        this.templatename = templatename;
        this.features = new ArrayList<>();
        this.features.addAll(page.getFeatures().stream().map(FeatureProvider::copy).collect(Collectors.toList()));
    }

    // ===== PenguinRegistry Implementation =====

    @Override
    public ResourceLocation id() {
        return templateId != null ? templateId : new ResourceLocation(EInfo.MODID, "default");
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
        return templateId != null ? templateId.getPath() : "default";
    }

    @Override
    public String getTemplateName() {
        return templatename;
    }

    @Override
    public ResourceLocation getIcon() {
        if (location == null && templateId != null) {
            location = new ResourceLocation(templateId.getNamespace(), "templates/" + templateId.getPath() + ".png");
        }

        return location != null ? location : new ResourceLocation(EInfo.MODID, "templates/default.png");
    }

    @Override
    public List<FeatureProvider> getFeatures() {
        return features;
    }
}