package joshie.enchiridion.api.book;

import com.mojang.serialization.Codec;

import java.util.List;

public interface IFeature {
    /** @return a duplicate of this feature **/
    joshie.enchiridion.data.book.FeatureProvider copy();

    void update(IPage page);
    void draw(int mouseX, int mouseY);
    void addTooltip(List<String> tooltip, int mouseX, int mouseY);

    /** Handle key typed
     * @param character the character typed
     * @param key the key code
     * @param gui the current book GUI instance
     * @return true if the key was handled **/
    boolean keyTyped(char character, int key, Object gui);

    /** @return true if this feature should display yellow squares instead of blue
     *  Should also open any edit menus required
     * @param gui the current book GUI instance **/
    boolean getAndSetEditMode(Object gui);

    /* Called when not in edit mode, or shift is clicked, on mouseClick **/
    boolean performClick(int mouseX, int mouseY, int button);
    void performRelease(int mouseX, int mouseY, int button);

    /** Follow the mouse
     * @param mouseX the mouse X position
     * @param mouseY the mouse Y position
     * @param gui the current book GUI instance **/
    void follow(int mouseX, int mouseY, Object gui);
    void scroll(boolean down, int amount);
    void onDeselected();

    /** @return the name of this feature **/
    String getName();

    /** @return the codec for this feature type **/
    Codec<? extends IFeature> codec();
}