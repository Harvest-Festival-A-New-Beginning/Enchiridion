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

public class FeatureProvider extends AbstractWidget implements IFeatureProvider {
    // Helper method to get codec for a feature instance
    private static com.mojang.serialization.Codec<? extends IFeature> getCodecForFeature(IFeature feature) {
        // Map feature class to codec - this will be populated as we add more features
        if (feature instanceof joshie.enchiridion.gui.book.features.FeatureText) {
            return joshie.enchiridion.gui.book.features.FeatureText.CODEC;
        } else if (feature instanceof joshie.enchiridion.gui.book.features.FeatureImage) {
            return joshie.enchiridion.gui.book.features.FeatureImage.CODEC;
        } else if (feature instanceof joshie.enchiridion.gui.book.features.FeatureRecipe) {
            return joshie.enchiridion.gui.book.features.FeatureRecipe.CODEC;
        } else if (feature instanceof joshie.enchiridion.gui.book.features.FeatureItem) {
            return joshie.enchiridion.gui.book.features.FeatureItem.CODEC;
        } else if (feature instanceof joshie.enchiridion.gui.book.features.FeatureButton) {
            return joshie.enchiridion.gui.book.features.FeatureButton.CODEC;
        } else if (feature instanceof joshie.enchiridion.gui.book.features.FeatureBox) {
            return joshie.enchiridion.gui.book.features.FeatureBox.CODEC;
        } else if (feature instanceof joshie.enchiridion.gui.book.features.FeaturePreviewWindow) {
            return joshie.enchiridion.gui.book.features.FeaturePreviewWindow.CODEC;
        } else if (feature instanceof joshie.enchiridion.gui.book.features.FeatureError) {
            return joshie.enchiridion.gui.book.features.FeatureError.CODEC;
        }
        // Fallback to error codec
        return joshie.enchiridion.gui.book.features.FeatureError.CODEC;
    }

    // Codec with dispatch for polymorphic IFeature
    public static final Codec<FeatureProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("x").forGetter(AbstractWidget::getX),
        Codec.INT.fieldOf("y").forGetter(AbstractWidget::getY),
        Codec.INT.fieldOf("width").forGetter(p -> p.width),
        Codec.INT.fieldOf("height").forGetter(p -> p.height),
        Codec.BOOL.optionalFieldOf("isLocked", true).forGetter(p -> p.isLocked),
        Codec.BOOL.optionalFieldOf("isHidden", false).forGetter(p -> p.isHidden),
        Codec.BOOL.optionalFieldOf("isFromTemplate", false).forGetter(p -> p.isFromTemplate),
        Codec.INT.optionalFieldOf("layerIndex", 0).forGetter(p -> p.layerIndex),
        EnchiridionRegistries.Features.FEATURE.byNameCodec().dispatch(
            feature -> {
                com.mojang.serialization.Codec<? extends IFeature> codec = getCodecForFeature(feature);
                return EnchiridionRegistries.Features.FEATURE.getKey(codec);
            },
            codec -> (com.mojang.serialization.Codec<IFeature>) codec
        ).fieldOf("feature").forGetter(p -> p.feature)
    ).apply(instance, (x, y, width, height, isLocked, isHidden, isFromTemplate, layerIndex, feature) -> {
        FeatureProvider provider = new FeatureProvider(feature, x, y, width, height);
        provider.isLocked = isLocked;
        provider.isHidden = isHidden;
        provider.isFromTemplate = isFromTemplate;
        provider.layerIndex = layerIndex;
        return provider;
    }));

    public IFeature feature;
    public boolean isLocked;
    public boolean isHidden;
    public boolean isFromTemplate;
    public int layerIndex;

    public transient int right;
    public transient int bottom;

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

    public FeatureProvider(IFeature feature, int x, int y, double width, double height) {
        super(x, y, (int) width, (int) height, Component.empty());
        this.feature = feature;
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
        feature.update(this);
    }

    @Override
    public IFeatureProvider copy() {
        IFeatureProvider copy = new FeatureProvider(feature.copy(), getX(), getY(), width, height);
        copy.setLocked(isLocked);
        copy.setVisible(!isHidden);
        copy.setLayerIndex(layerIndex);
        copy.setFromTemplate(isFromTemplate);
        return copy;
    }

    @Override
    public boolean isOverFeature(int x, int y) {
        return x >= getX() && x <= right && y >= getY() && y <= bottom;
    }

    private boolean isOverTopLeftCorner(int x, int y) {
        return x >= getX() && x <= getX() + 2 && y >= getY() && y <= getY() + 2;
    }

    private boolean isOverTopRightCorner(int x, int y) {
        return x >= right - 2 && x <= right && y >= getY() && y <= getY() + 2;
    }

    private boolean isOverBottomRightCorner(int x, int y) {
        return x >= right - 2 && x <= right && y >= bottom - 2 && y <= bottom;
    }

    private boolean isOverBottomLeftCorner(int x, int y) {
        return x >= getX() && x <= getX() + 2 && y >= bottom - 2 && y <= bottom;
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        right = (int) (getX() + width);
        bottom = (int) (getY() + height);
        if (EventHelper.isFeatureVisible(getPage(), isVisible(), layerIndex)) {
            feature.draw(mouseX, mouseY);
            if (isSelected) {
                int color = isEditing ? 0xCCFFFF00 : 0xCC007FFF;
                EnchiridionAPI.draw.drawRectangle(right - 2, getY(), right, getY() + 2, color);
                EnchiridionAPI.draw.drawRectangle(getX(), getY(), getX() + 2, getY() + 2, color);
                EnchiridionAPI.draw.drawRectangle(right - 2, bottom - 2, right, bottom, color);
                EnchiridionAPI.draw.drawRectangle(getX(), bottom - 2, getX() + 2, bottom, color);
            }
        }
    }

    @Override
    public void addTooltip(List<String> tooltip, int mouseX, int mouseY) {
        if (isOverFeature(mouseX, mouseY)) {
            feature.addTooltip(tooltip, mouseX, mouseY);
        }
    }

    @Override
    public boolean keyTyped(char character, int key) {
        if (isEditing) {
            feature.keyTyped(character, key);
        } else if (isSelected && key == 211 && !TextEditor.INSTANCE.isEditing()) {
            GuiSimpleEditor.INSTANCE.setEditor(null); //Reset the editor
            TextEditor.INSTANCE.clearEditable();
            return true;
        }
        return false;
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
                isEditing = feature.getAndSetEditMode();
            }

            //Perform clicks
            if (!EnchiridionAPI.book.isEditMode() || button != 0) {
                if (feature.performClick(mouseX, mouseY, button)) return true;
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
        feature.performRelease(mouseX, mouseY, button);
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
        feature.onDeselected();
    }

    @Override
    public void scroll(int mouseX, int mouseY, boolean down) {
        if (isOverFeature(mouseX, mouseY)) {
            feature.scroll(down, 10);
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
        return feature;
    }

    @Override
    public int getLeft() {
        return getX();
    }

    @Override
    public int getRight() {
        return right;
    }

    @Override
    public int getTop() {
        return getY();
    }

    @Override
    public int getBottom() {
        return bottom;
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
}