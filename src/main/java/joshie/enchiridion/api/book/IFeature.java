package joshie.enchiridion.api.book;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;

import java.util.List;

public interface IFeature {
    /** @return a duplicate of this feature **/
    IFeatureProvider copy();

    void update(IPage page);
    void draw(int mouseX, int mouseY);
    void addTooltip(List<String> tooltip, int mouseX, int mouseY);
    boolean keyTyped(char character, int key);

    /** @return true if this feature should display yellow squares instead of blue
     *  Should also open any edit menus required **/
    boolean getAndSetEditMode();

    /* Called when not in edit mode, or shift is clicked, on mouseClick **/
    boolean performClick(int mouseX, int mouseY, int button);
    void performRelease(int mouseX, int mouseY, int button);
    void follow(int mouseX, int mouseY);
    void scroll(boolean down, int amount);
    void onDeselected();
    void readFromJson(JsonObject json);
    void writeToJson(JsonObject json);

    /** @return the name of this feature **/
    String getName();

    /** @return the codec for this feature type **/
    Codec<? extends IFeature> codec();
}