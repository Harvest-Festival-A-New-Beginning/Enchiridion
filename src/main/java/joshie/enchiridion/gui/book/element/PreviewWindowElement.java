package joshie.enchiridion.gui.book.element;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.api.book.IBook;
import joshie.enchiridion.data.book.Page;
import joshie.enchiridion.api.gui.ISimpleEditorFieldProvider;
import joshie.enchiridion.data.book.FeatureProvider;
import joshie.enchiridion.gui.book.GuiBook;
import joshie.enchiridion.gui.book.GuiSimpleEditorAbstract;
import joshie.enchiridion.gui.book.GuiSimpleEditorGeneric;
import joshie.enchiridion.helpers.JumpHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Preview window element - renders another page in a scrollable window
 */
public class PreviewWindowElement implements FeatureElement, ISimpleEditorFieldProvider {
    public static final Codec<PreviewWindowElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.optionalFieldOf("page_number", 0).forGetter(f -> f.pageNumber)
    ).apply(instance, PreviewWindowElement::new));

    public int pageNumber;
    private transient Page page;
    private transient IBook book;
    private transient Page thisPage;
    private transient boolean isDragging;
    private transient int startY;

    // Store current bounds for interaction
    private transient int currentX;
    private transient int currentY;
    private transient int currentWidth;
    private transient int currentHeight;

    public PreviewWindowElement(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    @Override
    public void onFieldsSet(String field) {
        page = JumpHelper.getPageByNumber(book, pageNumber - 1);
    }

    @Override
    public void onUpdate(@Nullable Page containingPage) {
        thisPage = containingPage;
        if (containingPage != null) {
            book = containingPage.getBook();
            if (book != null && book.getPages() != null) {
                page = JumpHelper.getPageByNumber(book, pageNumber);
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics guiGraphics, int x, int y, int width, int height, float scale, int mouseX, int mouseY, float partialTicks) {
        // Cache bounds for interaction
        currentX = x;
        currentY = y;
        currentWidth = width;
        currentHeight = height;

        GuiBook guiBook = Minecraft.getInstance().screen instanceof GuiBook ? (GuiBook) Minecraft.getInstance().screen : null;
        if (guiBook == null) return;

        // Draw border in edit mode
        if (guiBook.isEditMode()) {
            int colorI = 0x00000000;
            int colorB = 0xFF48453C;
            guiGraphics.fill(x, y, x + width, y + height, colorI);
            guiGraphics.fill(x, y, x + width, y + 1, colorB);
            guiGraphics.fill(x, y + height - 1, x + width, y + height, colorB);
            guiGraphics.fill(x, y, x + 1, y + height, colorB);
            guiGraphics.fill(x + width - 1, y, x + width, y + height, colorB);
        }

        if (page != null && page != thisPage) {
            int scrollMax = page.getScrollbarMax(y + height - 5);
            if (isDragging) {
                if (startY != mouseY) {
                    int scrollPosition = (int) (((mouseY - y) * (scrollMax)) / height);
                    page.updateMaximumScroll(y + height - 5);
                    page.setScrollPosition(scrollPosition);
                }
                startY = mouseY;
            }

            // Render page content with scissor clipping
            PoseStack poseStack = new PoseStack();
            poseStack.pushPose();
            int scaleInt = (int) Minecraft.getInstance().getWindow().getGuiScale();
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT, false);
            GL11.glScissor((guiBook.getLeftPos() + x) * scaleInt,
                (int) (guiBook.getTopPos() + 217 - y - height) * scaleInt,
                width * scaleInt, height * scaleInt);

            for (FeatureProvider feature : Lists.reverse(page.getFeatures())) {
                if (feature.element instanceof PreviewWindowElement) continue; // No cascading

                boolean isMouseHovering = isOverFeature(mouseX, mouseY);
                int adjustedMouseX = isMouseHovering ? mouseX : Short.MAX_VALUE;
                int adjustedMouseY = isMouseHovering ? mouseY + page.getScroll() : Short.MAX_VALUE;

                feature.draw(adjustedMouseX, adjustedMouseY);
                feature.addTooltip(guiBook.TOOLTIP, adjustedMouseX, adjustedMouseY);

                RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT, false);
            }

            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            poseStack.popPose();

            // Draw scrollbar
            RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT, false);
            int minY = Short.MAX_VALUE;
            for (FeatureProvider provider : page.getFeatures()) {
                if (provider.getY() < minY) {
                    minY = provider.getY();
                }
            }

            if (height < (scrollMax + y + height - 5 - minY)) {
                // Draw scrollbar background
                int colorI = 0xFFB0A483;
                int colorB = 0xFF362C24;
                guiGraphics.fill(x + width - 10, y, x + width, y + height, colorI);
                guiGraphics.fill(x + width - 10, y, x + width, y + 1, colorB);
                guiGraphics.fill(x + width - 10, y + height - 1, x + width, y + height, colorB);
                guiGraphics.fill(x + width - 10, y, x + width - 9, y + height, colorB);
                guiGraphics.fill(x + width - 1, y, x + width, y + height, colorB);

                // Draw scrollbar thumb
                int pos = (int) ((page.getScroll() * (height - 10)) / scrollMax);
                int thumbColorI = 0xFF2F271F;
                int thumbColorB = 0xFF191511;
                guiGraphics.fill(x + width - 10, y + pos, x + width, y + pos + 10, thumbColorI);
                guiGraphics.fill(x + width - 10, y + pos, x + width, y + pos + 1, thumbColorB);
                guiGraphics.fill(x + width - 10, y + pos + 9, x + width, y + pos + 10, thumbColorB);
                guiGraphics.fill(x + width - 10, y + pos, x + width - 9, y + pos + 10, thumbColorB);
                guiGraphics.fill(x + width - 1, y + pos, x + width, y + pos + 10, thumbColorB);
            }
        }
    }

    @Override
    public boolean onClick(GuiBook guiBook, int mouseX, int mouseY, int button) {
        if (page != null && page != thisPage) {
            // Check features inside preview window
            for (FeatureProvider feature : Lists.reverse(page.getFeatures())) {
                if (feature.element instanceof PreviewWindowElement) continue; // No cascading
                int scrollAdjustedMouseY = mouseY + page.getScroll();
                if (feature.mouseClicked(mouseX, scrollAdjustedMouseY, button, guiBook)) {
                    return true;
                }
            }

            // Check scrollbar drag
            int scrollMax = page.getScrollbarMax(currentY + currentHeight - 5);
            int pos = (int) ((page.getScroll() * (currentHeight - 10)) / scrollMax);
            if (isOverScrollY(pos, mouseX, mouseY)) {
                isDragging = true;
                startY = mouseY;
                return true;
            }
        }
        return false;
    }

    private boolean isOverFeature(int x, int y) {
        return x >= currentX && x <= currentX + currentWidth && y >= currentY && y <= currentY + currentHeight;
    }

    private boolean isOverScrollY(int yCheck, int x, int y) {
        return x >= currentX + currentWidth - 10 && x <= currentX + currentWidth &&
            y >= currentY + yCheck && y <= currentY + yCheck + 10;
    }

    // Note: onScroll removed - PreviewWindow handles its own scrolling via onClick drag
    // Mouse wheel scrolling can be added later if needed

    @Override
    public void onDeselected() {
        if (isDragging) {
            isDragging = false;
        }
    }

    @Override
    public GuiSimpleEditorAbstract getEditor(GuiBook guiBook) {
        return (GuiSimpleEditorAbstract) GuiSimpleEditorGeneric.INSTANCE.setFeature(this);
    }

    @Override
    public FeatureElement copy() {
        return new PreviewWindowElement(pageNumber);
    }

    @Override
    public String getName() {
        return "Scroll: " + (page != null ? page.getPageNumber() : pageNumber);
    }

    @Override
    public Codec<? extends FeatureElement> codec() {
        return CODEC;
    }
}
