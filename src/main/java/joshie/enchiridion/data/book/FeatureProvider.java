package joshie.enchiridion.data.book;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.api.book.Page;
import joshie.enchiridion.gui.book.GuiBook;
import joshie.enchiridion.gui.book.GuiGrid;
import joshie.enchiridion.gui.book.GuiSimpleEditor;
import joshie.enchiridion.gui.book.element.FeatureElement;
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
import uk.joshiejack.penguinlib.data.PenguinRegistries;
import uk.joshiejack.penguinlib.util.icon.Icon;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

/**
 * Concrete widget wrapper for FeatureElement (similar to PenguinLib's MoveableWidget)
 * Handles position, size, layer, selection, dragging, editing
 * Delegates rendering to the wrapped FeatureElement
 */
public class FeatureProvider extends AbstractWidget {
    // TODO: Update to use element-based codec once all features are converted
    public static final Codec<FeatureProvider> CODEC = EnchiridionRegistries.Features.FEATURES.byNameCodec().dispatchStable(FeatureProvider::codec, Function.identity());
    public static final Codec<FeatureProvider> FEATURE = RecordCodecBuilder.create(instance -> instance.group(
            FeatureProvider.CODEC.fieldOf("feature").forGetter(f -> f),
            Codec.BOOL.optionalFieldOf("is_locked", true).forGetter(f -> f.isLocked),
            Codec.BOOL.optionalFieldOf("is_hidden", false).forGetter(f -> !f.visible), // is_hidden = !visible for backwards compat
            Codec.BOOL.optionalFieldOf("is_from_template", false).forGetter(f -> f.isFromTemplate),
            Codec.INT.optionalFieldOf("layer_index", 0).forGetter(f -> f.layerIndex),
            Codec.INT.optionalFieldOf("relative_x", 0).forGetter(f -> f.relativeX),
            Codec.INT.optionalFieldOf("relative_y", 0).forGetter(f -> f.relativeY),
            Codec.INT.fieldOf("width").forGetter(FeatureProvider::getWidth),
            Codec.INT.fieldOf("height").forGetter(FeatureProvider::getHeight)
    ).apply(instance, (feature, isLocked, isHidden, isFromTemplate, layerIndex,
                       relativeX, relativeY, width, height) -> {
        feature.isLocked = isLocked;
        feature.visible = !isHidden; // Convert is_hidden to visible
        feature.isFromTemplate = isFromTemplate;
        feature.layerIndex = layerIndex;
        feature.relativeX = relativeX;
        feature.relativeY = relativeY;
        // Set width/height directly on AbstractWidget's protected fields
        feature.width = width;
        feature.height = height;
        return feature;
    }));

    // Rendering element - null for features not yet converted to element pattern
    @Nullable
    public FeatureElement element;

    // Widget properties
    public boolean isLocked;
    public boolean isFromTemplate;
    public int layerIndex;
    public int relativeX;
    public int relativeY;

    // Transient editing/interaction state
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
    private transient Page pageContainer;
    private transient GuiBook currentGui;

    public FeatureProvider(int x, int y, int width, int height) {
        this(null, x, y, width, height);
    }

    public FeatureProvider(@Nullable FeatureElement element, int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.element = element;
        this.isLocked = true;
        this.isFromTemplate = false;
        this.visible = true; // Features visible by default
    }

    public FeatureProvider init(GuiBook guiBook) {
        this.currentGui = guiBook;
        return this;
    }

    public Page getPage() {
        return pageContainer;
    }

    /** Gets the current GUI context set during init */
    protected GuiBook getCurrentGui() {
        return currentGui;
    }

    public void update(Page page) {
        this.pageContainer = page;
        this.pageContainer.sort();
        // Delegate to element for element-specific update logic
        if (element != null) {
            element.onUpdate(page);
        }
    }

    /** Create a copy of this feature - delegates to element if present */
    public FeatureProvider copy() {
        FeatureElement copiedElement = element != null ? element.copy() : null;
        FeatureProvider copy = new FeatureProvider(copiedElement, 0, 0, getWidth(), getHeight());
        copy.isLocked = this.isLocked;
        copy.visible = this.visible;
        copy.isFromTemplate = this.isFromTemplate;
        copy.layerIndex = this.layerIndex;
        copy.relativeX = this.relativeX;
        copy.relativeY = this.relativeY;
        return copy;
    }

    
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

    public void draw(int mouseX, int mouseY) {
        // Legacy method - redirects to renderWidget for compatibility
        // This may be called from GuiBook during the transition
        renderWidget(null, mouseX, mouseY, 0);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (EventHelper.isFeatureVisible(getPage(), isVisible(), layerIndex)) {
            // Delegate to element for rendering if present, otherwise call drawFeature for legacy features
            if (element != null) {
                element.render(guiGraphics, getX(), getY(), getWidth(), getHeight(), 1.0f, mouseX, mouseY, partialTicks);
            } else {
                drawFeature(guiGraphics, mouseX, mouseY, partialTicks);
            }

            // Draw selection visual
            if (isSelected && guiGraphics != null) {
                int color = isEditing ? 0xCCFFFF00 : 0xCC007FFF;
                guiGraphics.fill(getRight() - 2, getY(), getRight(), getY() + 2, color);
                guiGraphics.fill(getX(), getY(), getX() + 2, getY() + 2, color);
                guiGraphics.fill(getRight() - 2, getBottom() - 2, getRight(), getBottom(), color);
                guiGraphics.fill(getX(), getBottom() - 2, getX() + 2, getBottom(), color);
            }
        }
    }

    /** Legacy rendering method - override for features not yet converted to elements */
    protected void drawFeature(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        // Default: no rendering (element-based features use element.render() instead)
    }

    public void addTooltip(List<String> tooltip, int mouseX, int mouseY) {
        // Delegate to element if present
        if (element != null) {
            element.addTooltip(tooltip, mouseX, mouseY);
        }
    }

    public boolean keyTyped(char character, int key, GuiBook guiBook) {
        
        if (isEditing) {
            // Delegate to element if present
            if (element != null && element.onKeyPress(character, key, guiBook)) {
                return true;
            }
        } else if (isSelected && key == 211 && !TextEditor.INSTANCE.isEditing()) {
            guiBook.getSimpleEditor().setEditor(null); //Reset the editor
            TextEditor.INSTANCE.clearEditable();
            return true;
        }
        return false;
    }


    public boolean mouseClicked(int mouseX, int mouseY, int button, GuiBook guiBook) {
        if (guiBook.isEditMode()) {
            guiBook.getSimpleEditor().setEditor(null); //Reset the editor
            TextEditor.INSTANCE.clearEditable();
        }

        if (!EventHelper.isFeatureVisible(getPage(), isVisible(), layerIndex)) return false;
        if (isOverFeature(mouseX, mouseY)) {
            if (button == 0 && guiBook.isEditMode() && !isLocked()) {
                // Delegate to element for edit mode handling
                isEditing = element != null ? element.enterEditMode(guiBook) : false;
            }

            //Perform clicks - delegate to element
            if (!guiBook.isEditMode() || button != 0) {
                if (element != null && element.onClick(mouseX, mouseY, button, guiBook)) {
                    return true;
                }
            }

            return !isLocked;
        }
        return false;
    }

    
    public void mouseReleased(int mouseX, int mouseY, int button) {
        isHeld = false;
        isDragging = false;
        dragTopLeft = false;
        dragTopRight = false;
        dragBottomLeft = false;
        dragBottomRight = false;

        if (!EventHelper.isFeatureVisible(getPage(), isVisible(), layerIndex)) return;
        // Delegate to element
        if (element != null) {
            element.onRelease(mouseX, mouseY, button);
        }
    }

    
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

    
    public void deselect() {
        isEditing = false;
        isSelected = false;
        isHeld = false;
        isDragging = false;
        dragTopLeft = false;
        dragTopRight = false;
        dragBottomLeft = false;
        dragBottomRight = false;
        // Delegate to element
        if (element != null) {
            element.onDeselected();
        }
    }


    public void scroll(int mouseX, int mouseY, boolean down) {
        if (isOverFeature(mouseX, mouseY)) {
            // Delegate to element
            if (element != null) {
                element.onScroll(down, 10);
            }
        }
    }


    public void follow(int mouseX, int mouseY, boolean force, GuiBook guiBook) {
        if (isHeld || force) {
            if (force) {
                isSelected = true;
            }

            int changeX = (mouseX - prevX);
            int changeY = (mouseY - prevY);
            setX(getX() + changeX);
            setY(getY() + changeY);

            GuiGrid grid = guiBook.getGrid();
            if (grid.isActivated()) {
                int large = grid.getGridSize();
                int small = large - 1;

                if (changeX < 0) {
                    setX(((getX() - small) / large * large) - (grid.isPixelGrid() ? 1 : 0));
                } else if (changeX > 0) {
                    setX(((getX() + small) / large * large) - (grid.isPixelGrid() ? 1 : 0));
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


    public FeatureProvider getFeature() {
        return this;
    }


    public int getLeft() {
        return getX();
    }


    public int getRight() {
        return getX() + getWidth();
    }


    public int getTop() {
        return getY();
    }


    public int getBottom() {
        return getY() + getHeight();
    }

    
    public int getWidth() {
        return width;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput p_259858_) {

    }

    
    public int getHeight() {
        return height;
    }


    public boolean isVisible() {
        return this.visible;
    }

    
    public boolean isLocked() {
        return isLocked;
    }

    
    public boolean isFromTemplate() {
        return isFromTemplate;
    }

    
    public int getLayerIndex() {
        return layerIndex;
    }

    
    public long getTimeChanged() {
        return timestamp;
    }

    
    public void setWidth(int w) {
        width = w;
    }

    
    public void setHeight(int h) {
        height = h;
    }


    public void setVisible(boolean v) {
        this.visible = v;
    }

    
    public void setLocked(boolean b) {
        isLocked = b;
    }

    
    public void setLayerIndex(int i) {
        layerIndex = i;
        timestamp = System.currentTimeMillis();
    }

    
    public void setFromTemplate(boolean b) {
        this.isFromTemplate = b;
    }

    /**
     * Get the display name for this feature (for layers panel)
     * Delegates to element if present
     */
    public String getName() {
        return element != null ? element.getName() : getClass().getSimpleName();
    }

    /**
     * Get codec for this feature
     * For legacy features that extend FeatureProvider, subclasses must override
     * For element-based features, this will be handled by the registry dispatch codec
     */
    public Codec<? extends FeatureProvider> codec() {
        throw new UnsupportedOperationException("Feature must provide codec: " + getClass().getName());
    }
}