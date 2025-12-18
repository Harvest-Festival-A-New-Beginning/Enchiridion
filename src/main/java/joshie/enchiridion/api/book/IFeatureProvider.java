package joshie.enchiridion.api.book;

import java.util.List;

public interface FeatureProvider {
    //Getters
    IFeature getFeature();
    int getLeft();
    int getRight();
    int getTop();
    int getBottom();
    int getWidth();
    int getHeight();
    boolean isVisible();
    boolean isLocked();
    int getLayerIndex();
    long getTimeChanged();
    boolean isFromTemplate();

    //Setters
    void setX(int x);
    void setY(int y);
    void setWidth(int w);
    void setHeight(int h);
    void setVisible(boolean value);
    void setLocked(boolean value);
    void setLayerIndex(int i);
    void setFromTemplate(boolean b);

    //Rendering and functions
    void update(IPage page);
    void draw(int mouseX, int mouseY);
    void addTooltip(List<String> tooltip, int mouseX, int mouseY);
    boolean mouseClicked(int mouseX, int mouseY, int button);
    void mouseReleased(int mouseX, int mouseY, int button);
    void select(int mouseX, int mouseY);
    void deselect();
    void follow(int mouseX, int mouseY, boolean force);
    boolean keyTyped(char character, int key);
    void scroll(int mouseX, int mouseY, boolean down);
    boolean isOverFeature(int mouseX, int mouseY);

    // Returns a new copy of this provider
    FeatureProvider copy();
    // Returns the page this feature is in
    IPage getPage();
}