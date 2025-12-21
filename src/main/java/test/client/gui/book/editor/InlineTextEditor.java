package test.client.gui.book.editor;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

/**
 * Inline text editor that displays directly in the widget's position
 * Similar to Minecraft's book editing, with cursor and multiline support
 * Supports auto-scaling to fit text within bounding box
 */
@OnlyIn(Dist.CLIENT)
public class InlineTextEditor {
    private static final String BLACK_CURSOR = ChatFormatting.BLACK + "_";
    private static final String GRAY_CURSOR = ChatFormatting.GRAY + "_";
    private static final float MIN_SCALE = 0.1f;
    private static final float MAX_SCALE = 5.0f;

    private final Font font;
    private String text;
    private int cursorPosition = 0;
    private int frameTick = 0;
    private int maxLength = 1000;
    private java.util.function.BiConsumer<Integer, Integer> onSizeChanged = null;
    private boolean autoResize = false;

    public InlineTextEditor(Font font, String initialText) {
        this.font = font;
        this.text = initialText == null ? "" : initialText;
        this.cursorPosition = text.length();
    }

    public void tick() {
        frameTick++;
    }

    public String getText() {
        return text;
    }

    /**
     * Set callback for when size needs to be changed
     */
    public void setOnSizeChanged(java.util.function.BiConsumer<Integer, Integer> callback) {
        this.onSizeChanged = callback;
    }

    /**
     * Enable or disable auto-resizing (adjust width/height to fit text)
     */
    public void setAutoResize(boolean autoResize) {
        this.autoResize = autoResize;
        if (autoResize) {
            updateAutoSize();
        }
    }

    /**
     * Calculate and update the bounding box to fit text dimensions
     */
    private void updateAutoSize() {
        if (!autoResize || onSizeChanged == null) {
            return;
        }

        // Calculate text dimensions without cursor
        String[] lines = text.split("\n", -1);
        int maxWidth = 0;
        for (String line : lines) {
            int lineWidth = font.width(line);
            if (lineWidth > maxWidth) {
                maxWidth = lineWidth;
            }
        }

        int lineHeight = font.lineHeight + 2;
        int totalHeight = lines.length * lineHeight;

        // If text is empty, use a minimum size
        if (maxWidth == 0) {
            maxWidth = 20;
        }
        if (totalHeight == 0) {
            totalHeight = lineHeight;
        }

        // Notify the callback with new dimensions
        onSizeChanged.accept(maxWidth, totalHeight);
    }

    public void render(GuiGraphics graphics, int x, int y, int width, float scale, int color, boolean shadow) {
        // Build display text with cursor
        String displayText = text;
        boolean showCursor = frameTick / 6 % 2 == 0;

        if (showCursor) {
            displayText = text.substring(0, cursorPosition) + BLACK_CURSOR + ChatFormatting.RESET
                    + text.substring(cursorPosition);
        } else {
            displayText = text.substring(0, cursorPosition) + GRAY_CURSOR + ChatFormatting.RESET
                    + text.substring(cursorPosition);
        }

        // Render text with cursor
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(scale, scale, 1.0f);

        // Draw multiline text with shadow matching the TextElement's setting
        int lineHeight = font.lineHeight + 2;
        int currentY = 0;
        String[] lines = displayText.split("\n", -1);

        for (String line : lines) {
            graphics.drawString(font, line, 0, currentY, color, shadow);
            currentY += lineHeight;
        }

        graphics.pose().popPose();
    }

    public boolean charTyped(char character, int modifiers) {
        if (Character.isISOControl(character)) {
            return false;
        }

        if (text.length() < maxLength) {
            text = text.substring(0, cursorPosition) + character + text.substring(cursorPosition);
            cursorPosition++;
            updateAutoSize();
            return true;
        }

        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean shiftHeld = (modifiers & GLFW.GLFW_MOD_SHIFT) != 0;
        boolean ctrlHeld = (modifiers & GLFW.GLFW_MOD_CONTROL) != 0;

        switch (keyCode) {
            case GLFW.GLFW_KEY_BACKSPACE:
                if (cursorPosition > 0) {
                    text = text.substring(0, cursorPosition - 1) + text.substring(cursorPosition);
                    cursorPosition--;
                    updateAutoSize();
                }
                return true;

            case GLFW.GLFW_KEY_DELETE:
                if (cursorPosition < text.length()) {
                    text = text.substring(0, cursorPosition) + text.substring(cursorPosition + 1);
                    updateAutoSize();
                }
                return true;

            case GLFW.GLFW_KEY_LEFT:
                if (cursorPosition > 0) {
                    if (ctrlHeld) {
                        // Move to start of previous word
                        cursorPosition = getPreviousWordPosition();
                    } else {
                        cursorPosition--;
                    }
                }
                return true;

            case GLFW.GLFW_KEY_RIGHT:
                if (cursorPosition < text.length()) {
                    if (ctrlHeld) {
                        // Move to start of next word
                        cursorPosition = getNextWordPosition();
                    } else {
                        cursorPosition++;
                    }
                }
                return true;

            case GLFW.GLFW_KEY_HOME:
                cursorPosition = 0;
                return true;

            case GLFW.GLFW_KEY_END:
                cursorPosition = text.length();
                return true;

            case GLFW.GLFW_KEY_ENTER:
            case GLFW.GLFW_KEY_KP_ENTER:
                if (shiftHeld) {
                    // Shift+Enter: Insert newline
                    if (text.length() < maxLength) {
                        text = text.substring(0, cursorPosition) + "\n" + text.substring(cursorPosition);
                        cursorPosition++;
                        updateAutoSize();
                    }
                    return true;
                }
                // Regular Enter is handled by the editor to exit edit mode
                return false;
        }

        return false;
    }

    private int getPreviousWordPosition() {
        int pos = cursorPosition - 1;
        // Skip whitespace
        while (pos > 0 && Character.isWhitespace(text.charAt(pos))) {
            pos--;
        }
        // Skip word
        while (pos > 0 && !Character.isWhitespace(text.charAt(pos - 1))) {
            pos--;
        }
        return pos;
    }

    private int getNextWordPosition() {
        int pos = cursorPosition;
        // Skip current word
        while (pos < text.length() && !Character.isWhitespace(text.charAt(pos))) {
            pos++;
        }
        // Skip whitespace
        while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
            pos++;
        }
        return pos;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }
}
