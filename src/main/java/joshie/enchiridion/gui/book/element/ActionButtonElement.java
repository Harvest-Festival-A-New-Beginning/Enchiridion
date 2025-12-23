package joshie.enchiridion.gui.book.element;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.api.book.IButtonAction;
import joshie.enchiridion.gui.book.GuiBook;
import joshie.enchiridion.gui.book.GuiSimpleEditorAbstract;
import joshie.enchiridion.gui.book.GuiSimpleEditorButton;
import joshie.enchiridion.helpers.MCClientHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

/**
 * Button element with action support
 * Handles button rendering, text overlays, tooltips, and click actions
 */
public class ActionButtonElement implements FeatureElement {
    // TODO: Complete codec implementation with IButtonAction support
    public static final Codec<ActionButtonElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.optionalFieldOf("texture_normal", new ResourceLocation("textures/gui/widgets.png")).forGetter(f -> f.textureNormal),
        ResourceLocation.CODEC.optionalFieldOf("texture_hover", new ResourceLocation("textures/gui/widgets.png")).forGetter(f -> f.textureHover),
        Codec.FLOAT.optionalFieldOf("size", 1F).forGetter(f -> f.size),
        Codec.BOOL.optionalFieldOf("left_click", true).forGetter(f -> f.leftClick),
        Codec.BOOL.optionalFieldOf("right_click", true).forGetter(f -> f.rightClick),
        Codec.BOOL.optionalFieldOf("other_click", false).forGetter(f -> f.otherClick),
        Codec.STRING.optionalFieldOf("tooltip", "").forGetter(f -> f.tooltip),
        Codec.STRING.optionalFieldOf("hover_text", "").forGetter(f -> f.hoverText),
        Codec.INT.optionalFieldOf("hover_x_offset", 0).forGetter(f -> f.hoverXOffset),
        Codec.INT.optionalFieldOf("hover_y_offset", 0).forGetter(f -> f.hoverYOffset),
        Codec.STRING.optionalFieldOf("unhovered_text", "").forGetter(f -> f.unhoveredText),
        Codec.INT.optionalFieldOf("unhovered_x_offset", 0).forGetter(f -> f.unhoveredXOffset),
        Codec.INT.optionalFieldOf("unhovered_y_offset", 0).forGetter(f -> f.unhoveredYOffset)
        // action field will be added when IButtonAction codec system is complete
    ).apply(instance, ActionButtonElement::new));

    private ResourceLocation textureNormal;
    private ResourceLocation textureHover;
    private float size;
    private boolean leftClick;
    private boolean rightClick;
    private boolean otherClick;
    private String tooltip;
    private String hoverText;
    private int hoverXOffset;
    private int hoverYOffset;
    private String unhoveredText;
    private int unhoveredXOffset;
    private int unhoveredYOffset;

    private transient IButtonAction action;
    private transient boolean isInit = false;

    public ActionButtonElement(ResourceLocation textureNormal, ResourceLocation textureHover, float size,
                                boolean leftClick, boolean rightClick, boolean otherClick,
                                String tooltip, String hoverText, int hoverXOffset, int hoverYOffset,
                                String unhoveredText, int unhoveredXOffset, int unhoveredYOffset) {
        this.textureNormal = textureNormal;
        this.textureHover = textureHover;
        this.size = size;
        this.leftClick = leftClick;
        this.rightClick = rightClick;
        this.otherClick = otherClick;
        this.tooltip = tooltip;
        this.hoverText = hoverText;
        this.hoverXOffset = hoverXOffset;
        this.hoverYOffset = hoverYOffset;
        this.unhoveredText = unhoveredText;
        this.unhoveredXOffset = unhoveredXOffset;
        this.unhoveredYOffset = unhoveredYOffset;
    }

    public ActionButtonElement(IButtonAction action) {
        this(
            new ResourceLocation("enchiridion:textures/books/arrow_left_off.png"),
            new ResourceLocation("enchiridion:textures/books/arrow_left_on.png"),
            1F, true, true, false, "", "", 0, 0, "", 0, 0
        );
        this.action = action;
    }

    public void setAction(IButtonAction action) {
        this.action = action;
    }

    public IButtonAction getAction() {
        return action;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, int width, int height, float scale, int mouseX, int mouseY, float partialTicks) {
        if (action == null || !action.isVisible()) return;

        // Initialize action if needed
        if (!isInit && action != null) {
            action.onFieldsSet("");
            isInit = true;
        }

        boolean isHovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
        ResourceLocation texture = isHovered ? textureHover : textureNormal;

        // Draw button texture
        if (texture != null) {
            graphics.blit(texture, x, y, 0, 0, width, height, width, height);
        }

        // Draw button text with scaling
        String text = isHovered ? hoverText : unhoveredText;
        if (text != null && !text.isEmpty()) {
            PoseStack poseStack = graphics.pose();
            poseStack.pushPose();
            poseStack.scale(size, size, size);
            Font font = Minecraft.getInstance().font;
            int textX = (int)((x + (isHovered ? hoverXOffset : unhoveredXOffset)) / size);
            int textY = (int)((y + (isHovered ? hoverYOffset : unhoveredYOffset)) / size);
            graphics.drawWordWrap(font, Component.literal(text), textX, textY, 200, 0x555555);
            poseStack.popPose();
        }
    }

    @Override
    public void addTooltip(List<String> tooltip, int mouseX, int mouseY) {
        if (action != null && action.isVisible() && this.tooltip != null && !this.tooltip.isEmpty()) {
            String[] lines = this.tooltip.split("\n");
            boolean first = false;
            for (String line : lines) {
                if (first || !line.equals("")) {
                    tooltip.add(line);
                } else first = true;
            }
        }
    }

    @Override
    public boolean onClick(GuiBook guiBook, int mouseX, int mouseY, int button) {
        if (action == null || !action.isVisible()) return false;

        // Check if this button processes this click type
        boolean processes = button == 0 ? leftClick : button == 1 ? rightClick : otherClick;
        if (!processes) return false;

        return action.performAction(guiBook);
    }

    @Override
    public boolean onKeyPress(GuiBook guiBook, char character, int key) {
        if (MCClientHelper.isShiftPressed()) {
            if (key == 78) { // N key - increase size
                size = Math.min(15F, Math.max(0.5F, size + 0.1F));
                return true;
            } else if (key == 74) { // J key - decrease size
                size = Math.min(15F, Math.max(0.5F, size - 0.1F));
                return true;
            }
        }
        return false;
    }

    @Override
    public GuiSimpleEditorAbstract getEditor(GuiBook guiBook) {
        return GuiSimpleEditorButton.INSTANCE.setButton(this);
    }

    @Override
    public FeatureElement copy() {
        ActionButtonElement copy = new ActionButtonElement(
            textureNormal, textureHover, size, leftClick, rightClick, otherClick,
            tooltip, hoverText, hoverXOffset, hoverYOffset,
            unhoveredText, unhoveredXOffset, unhoveredYOffset
        );
        copy.action = action != null ? action.copy() : null;
        return copy;
    }

    @Override
    public String getName() {
        return action != null && action.isVisible() ? action.getName().replace(" ", "") : "button";
    }

    @Override
    public Codec<? extends FeatureElement> codec() {
        return CODEC;
    }

    // Getters for editor
    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public ResourceLocation getTextureNormal() {
        return textureNormal;
    }

    public ResourceLocation getTextureHover() {
        return textureHover;
    }

    public String getTooltip() {
        return tooltip;
    }

    public String getHoverText() {
        return hoverText;
    }

    public String getUnhoveredText() {
        return unhoveredText;
    }

    // Setters for editor
    public void setResourceLocation(boolean isHovered, ResourceLocation resource) {
        if (isHovered) {
            textureHover = resource;
        } else {
            textureNormal = resource;
        }
    }

    public ResourceLocation getResource(boolean isHovered) {
        return isHovered ? textureHover : textureNormal;
    }
}
