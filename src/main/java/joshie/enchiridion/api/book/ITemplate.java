package joshie.enchiridion.api.book;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public interface ITemplate {
    /** Returns the unique name **/
    String getUniqueName();

    /** Returns the name of the template **/
    String getTemplateName();

    /** Returns a resource location for this templates icon **/
    ResourceLocation getIcon();

    /** Returns a list of all the features in this template **/
    List<joshie.enchiridion.data.book.FeatureProvider> getFeatures();
}