package joshie.enchiridion.gui.book.features;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import joshie.enchiridion.api.book.IFeature;
import joshie.enchiridion.api.book.IFeatureProvider;
import joshie.enchiridion.data.book.FeatureProvider;
import net.minecraft.network.chat.Component;

import java.util.List;

public abstract class FeatureAbstract extends FeatureProvider implements IFeature {
    public FeatureAbstract() {
        super(null, 0, 0, 0, 0);  // Default constructor - feature field will be null since we ARE the feature
    }

    @Override
    public void update(IFeatureProvider position) {
        // Override IFeature.update() - no-op since we are the provider
        // Subclasses can override if they need custom update logic
    }

    @Override
    public void draw(int mouseX, int mouseY) {
    }

    @Override
    public void addTooltip(List<String> tooltip, int mouseX, int mouseY) {
    }

    @Override
    public void keyTyped(char character, int key) {
    }

    @Override
    public boolean getAndSetEditMode() {
        return false;
    }

    @Override
    public boolean performClick(int mouseX, int mouseY, int button) {
        return false;
    }

    @Override
    public void performRelease(int mouseX, int mouseY, int button) {
    }

    @Override
    public void follow(int mouseX, int mouseY) {
    }

    @Override
    public void scroll(boolean down, int amount) {
    }

    @Override
    public void onDeselected() {
    }

    @Override
    public void readFromJson(JsonObject json) {
    }

    @Override
    public void writeToJson(JsonObject json) {
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public Codec<? extends IFeature> getCodec() {
        return FeatureError.CODEC; // Default fallback
    }
}
