package joshie.enchiridion.data.book;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.api.book.IFeature;
import joshie.enchiridion.api.book.IFeatureProvider;
import joshie.enchiridion.api.book.IPage;
import joshie.enchiridion.gui.book.GuiGrid;
import joshie.enchiridion.gui.book.GuiSimpleEditor;
import joshie.enchiridion.helpers.EventHelper;
import joshie.enchiridion.lib.EnchiridionRegistries;
import joshie.enchiridion.util.TextEditor;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public abstract class FeatureProvider extends AbstractWidget implements IFeatureProvider, IFeature {
    // Base codec fields - subclasses should extend this
    // Note: This codec is not directly used since FeatureProvider is abstract
    // Each concrete feature class creates its own codec that includes these fields
    public boolean isLocked;
    public boolean isHidden;
    public boolean isFromTemplate;
    public int layerIndex;

    private transient boolean isSelected;
    private transient boolean isEditing;
    private transient boolean isHeld;
    private transient int prevX;
    private transient int prevY;
    private transient boolean isDragging;
    private transient boolean dragTopLeft;
    private transient boolean dragTopRight;
    private transient boolean dragBottomLeft;
    private transient boolean dragBottomRight;
    private transient long timestamp;
    private transient IPage pageContainer;

    public FeatureProvider(int x, int y, double width, double height) {
        super(x, y, (int) width, (int) height, Component.empty());
        this.isLocked = true;
        this.isHidden = false;
        this.isFromTemplate = false;
    }

    @Override
    public IPage getPage() {
        return pageContainer;
    }

    @Override
    public void update(IPage page) {
        this.pageContainer = page;
        this.pageContainer.sort();
        // Subclasses can override if they need custom update logic
    }

    // Abstract method - each feature type must implement its own copy logic
    public abstract IFeatureProvider copy();

    @Override
    public boolean isOverFeature(int x, int y) {
        return x >= getX() && x <= getRight() && y >= getY() && y <= getBottom();
    }

    private boolean isOverTopLeftCorner(int x, int y) {
        return x >= getX() && x <= getX() + 2 && y >= getY() && y <= getY() + 2;
    }

    private boolean isOverTopRightCorner(int x, int y) {
        return x >= getRight() - 2 && x <= getRight() && y >= getY() && y <= getY() + 2;
    }

    private boolean isOverBottomRightCorner(int x, int y) {
        return x >= getRight() - 2 && x <= getRight() && y >= getBottom() - 2 && y <= getBottom();
    }

    private boolean isOverBottomLeftCorner(int x, int y) {
        return x >= getX() && x <= getX() + 2 && y >= getBottom() - 2 && y <= getBottom();
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        if (EventHelper.isFeatureVisible(getPage(), isVisible(), layerIndex)) {
            drawFeature(mouseX, mouseY);
            if (isSelected) {
                int color = isEditing ? 0xCCFFFF00 : 0xCC007FFF;
                EnchiridionAPI.draw.drawRectangle(getRight() - 2, getY(), getRight(), getY() + 2, color);
                EnchiridionAPI.draw.drawRectangle(getX(), getY(), getX() + 2, getY() + 2, color);
                EnchiridionAPI.draw.drawRectangle(getRight() - 2, getBottom() - 2, getRight(), getBottom(), color);
                EnchiridionAPI.draw.drawRectangle(getX(), getBottom() - 2, getX() + 2, getBottom(), color);
            }
        }
    }

    // Abstract method - each feature type must implement its own drawing logic
    protected abstract void drawFeature(int mouseX, int mouseY);

    @Override
    public void addTooltip(List<String> tooltip, int mouseX, int mouseY) {
        // Default implementation - subclasses can override
    }

    @Override
    public boolean keyTyped(char character, int key) {
        if (isEditing) {
            handleKeyTyped(character, key);
        } else if (isSelected && key == 211 && !TextEditor.INSTANCE.isEditing()) {
            GuiSimpleEditor.INSTANCE.setEditor(null); //Reset the editor
            TextEditor.INSTANCE.clearEditable();
            return true;
        }
        return false;
    }

    // Hook for subclasses to handle key input when editing
    protected void handleKeyTyped(char character, int key) {
        // Default implementation - subclasses can override
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (EnchiridionAPI.book.isEditMode()) {
            GuiSimpleEditor.INSTANCE.setEditor(null); //Reset the editor
            TextEditor.INSTANCE.clearEditable();
        }

        if (!EventHelper.isFeatureVisible(getPage(), isVisible(), layerIndex)) return false;
        if (isOverFeature(mouseX, mouseY)) {
            if (button == 0 && EnchiridionAPI.book.isEditMode() && !isLocked()) {
                isEditing = getAndSetEditMode();
            }

            //Perform clicks
            if (!EnchiridionAPI.book.isEditMode() || button != 0) {
                if (performClick(mouseX, mouseY, button)) return true;
            }

            return !isLocked;
        }
        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button) {
        isHeld = false;
        isDragging = false;
        dragTopLeft = false;
        dragTopRight = false;
        dragBottomLeft = false;
        dragBottomRight = false;

        if (!EventHelper.isFeatureVisible(getPage(), isVisible(), layerIndex)) return;
        performRelease(mouseX, mouseY, button);
    }

    @Override
    public void select(int mouseX, int mouseY) {
        isSelected = true;
        prevX = mouseX;
        prevY = mouseY;

        if (isOverTopLeftCorner(mouseX, mouseY)) {
            isDragging = true;
            dragTopLeft = true;
        } else if (isOverTopRightCorner(mouseX, mouseY)) {
            isDragging = true;
            dragTopRight = true;
        } else if (isOverBottomRightCorner(mouseX, mouseY)) {
            isDragging = true;
            dragBottomRight = true;
        } else if (isOverBottomLeftCorner(mouseX, mouseY)) {
            isDragging = true;
            dragBottomLeft = true;
        } else isHeld = true;
    }

    @Override
    public void deselect() {
        isEditing = false;
        isSelected = false;
        isHeld = false;
        isDragging = false;
        dragTopLeft = false;
        dragTopRight = false;
        dragBottomLeft = false;
        dragBottomRight = false;
        onDeselected();
    }

    @Override
    public void scroll(int mouseX, int mouseY, boolean down) {
        if (isOverFeature(mouseX, mouseY)) {
            scroll(down, 10);
        }
    }

    @Override
    public void follow(int mouseX, int mouseY, boolean force) {
        if (isHeld || force) {
            if (force) {
                isSelected = true;
            }

            int changeX = (mouseX - prevX);
            int changeY = (mouseY - prevY);
            setX(getX() + changeX);
            setY(getY() + changeY);

            if (GuiGrid.INSTANCE.isActivated()) {
                int large = GuiGrid.INSTANCE.getGridSize();
                int small = large - 1;

                if (changeX < 0) {
                    setX(((getX() - small) / large * large) - (GuiGrid.INSTANCE.isPixelGrid() ? 1 : 0));
                } else if (changeX > 0) {
                    setX(((getX() + small) / large * large) - (GuiGrid.INSTANCE.isPixelGrid() ? 1 : 0));
                }

                if (changeY < 0) {
                    setY((getY() - small) / large * large);
                } else if (changeY > 0) {
                    setY((getY() + small) / large * large);
                }
            }
        } else if (isDragging) {
            int changeX = (mouseX - prevX);
            int changeY = (mouseY - prevY);
            double originalHeight = height;
            double originalWidth = width;
            int originalY = getY();

            if (dragTopLeft) {
                setX(getX() + changeX);
                updateWidth(-changeX);
                setY(getY() + changeY);
                updateHeight(-changeY);
            } else if (dragTopRight) {
                updateWidth(changeX);
                setY(getY() + changeY);
                updateHeight(-changeY);
            } else if (dragBottomRight) {
                updateWidth(changeX);
                updateHeight(changeY);
            } else if (dragBottomLeft) {
                setX(getX() + changeX);
                updateWidth(-changeX);
                updateHeight(changeY);
            }

            if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT)) {
                if (dragBottomLeft || dragBottomRight) height = (int) ((width * originalHeight) / originalWidth);
                else if (dragTopRight) width = (int) ((height * originalWidth) / originalHeight);
                else if (dragTopLeft) {
                    //TODO: FIX DRAGGING FROM TOP LEFT WHEN KEEPING RATIO
                    height = (int) ((width * originalHeight) / originalWidth);
                    setY(originalY);
                }
            }
        }

        if (isHeld || isDragging || force) {
            prevX = mouseX;
            prevY = mouseY;
            update(pageContainer);
        }
    }

    public void updateWidth(int change) {
        width += change;
        if (width <= 2) {
            width = 2;
        }
    }

    public void updateHeight(int change) {
        height += change;
        if (height <= 1) {
            height = 1;
        }
    }

    @Override
    public IFeature getFeature() {
        return this;
    }

    @Override
    public int getLeft() {
        return getX();
    }

    @Override
    public int getRight() {
        return getX() + getWidth();
    }

    @Override
    public int getTop() {
        return getY();
    }

    @Override
    public int getBottom() {
        return getY() + getHeight();
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput p_259858_) {

    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    protected void renderWidget(GuiGraphics p_282139_, int p_268034_, int p_268009_, float p_268085_) {

    }

    @Override
    public boolean isVisible() {
        return !isHidden;
    }

    @Override
    public boolean isLocked() {
        return isLocked;
    }

    @Override
    public boolean isFromTemplate() {
        return isFromTemplate;
    }

    @Override
    public int getLayerIndex() {
        return layerIndex;
    }

    @Override
    public long getTimeChanged() {
        return timestamp;
    }

    @Override
    public void setWidth(int w) {
        width = w;
    }

    @Override
    public void setHeight(int h) {
        height = h;
    }

    @Override
    public void setVisible(boolean v) {
        isHidden = !v;
    }

    @Override
    public void setLocked(boolean b) {
        isLocked = b;
    }

    @Override
    public void setLayerIndex(int i) {
        layerIndex = i;
        timestamp = System.currentTimeMillis();
    }

    @Override
    public void setFromTemplate(boolean b) {
        this.isFromTemplate = b;
    }

    // ===== IFeature default implementations =====

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
    public void readFromJson(com.google.gson.JsonObject json) {
    }

    @Override
    public void writeToJson(com.google.gson.JsonObject json) {
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public abstract Codec<? extends IFeature> getCodec();
}