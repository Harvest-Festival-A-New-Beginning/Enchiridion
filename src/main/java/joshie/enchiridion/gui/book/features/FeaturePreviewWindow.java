package joshie.enchiridion.gui.book.features;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.api.book.IBook;
import joshie.enchiridion.api.book.IFeature;
import joshie.enchiridion.data.book.FeatureProvider;
import joshie.enchiridion.api.book.IPage;
import joshie.enchiridion.api.gui.ISimpleEditorFieldProvider;
import joshie.enchiridion.gui.book.GuiBook;
import joshie.enchiridion.gui.book.GuiSimpleEditor;
import joshie.enchiridion.gui.book.GuiSimpleEditorGeneric;
import joshie.enchiridion.helpers.JumpHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.lwjgl.opengl.GL11;

public class FeaturePreviewWindow extends joshie.enchiridion.data.book.FeatureProvider implements ISimpleEditorFieldProvider {
    public static final Codec<FeaturePreviewWindow> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.optionalFieldOf("page_number", 0).forGetter(f -> f.pageNumber)
    ).apply(instance, (pageNumber) -> {
        FeaturePreviewWindow feature = new FeaturePreviewWindow();
        feature.pageNumber = pageNumber;
        return feature;
    }));

    public int pageNumber;
    public transient IPage page;
    public transient IBook book;
    public transient IPage thisPage;
    public transient boolean isDragging;
    public transient int startY;

    public FeaturePreviewWindow() {
        super(0, 0, 0, 0);
    }

    public FeaturePreviewWindow(int page) {
        super(0, 0, 0, 0);
        this.pageNumber = page;
    }

    @Override
    public void onFieldsSet(String field) {
        page = JumpHelper.getPageByNumber(book, pageNumber - 1);
    }

    @Override
    public FeatureProvider copy() {
        return new FeaturePreviewWindow(pageNumber);
    }

    @Override
    public String getName() {
        return "Scroll: " + page.getPageNumber();
    }

    @Override
    public void update(joshie.enchiridion.api.book.IPage page) {
        super.update(page);
        thisPage = getPage();
        book = getPage().getBook();
        if (book != null && book.getPages() != null) {
            this.page = JumpHelper.getPageByNumber(book, pageNumber);
        }
    }

    @Override
    public boolean getAndSetEditMode() {
        GuiSimpleEditor.INSTANCE.setEditor(GuiSimpleEditorGeneric.INSTANCE.setFeature(this));
        return false;
    }

    private boolean isOverScrollY(int yCheck, int x, int y) {
        return x >= getRight() - 10 && x <= getRight() && y >= getTop() + yCheck && y <= getTop() + yCheck + 10;
    }

    @Override
    public boolean performClick(int mouseX, int mouseY, int button) {
        if (page != null && page != thisPage) {
            for (FeatureProvider feature : Lists.reverse(page.getFeatures())) {
                if (feature instanceof FeaturePreviewWindow) continue; //No Cascading
                mouseY = GuiBook.INSTANCE.mouseY + page.getScroll();
                if (feature.mouseClicked(mouseX, mouseY, button)) return true;
            }


            int scrollMax = page.getScrollbarMax(getBottom() - 5);
            int pos = (int) ((page.getScroll() * (getHeight() - 10)) / scrollMax);
            if (isOverScrollY(pos, mouseX, GuiBook.INSTANCE.mouseY)) {
                isDragging = true;
                startY = GuiBook.INSTANCE.mouseY;
                return true;
            }
        }

        return false;
    }

    @Override
    public void performRelease(int mouseX, int mouseY, int button) {
        if (isDragging) {
            isDragging = false;
        }
    }

    @Override
    protected void drawFeature(GuiGraphics guiGraphics, int xMouse, int yMouse, float partialTicks) {
        if (GuiBook.INSTANCE.isEditMode()) {
            // Draw bordered rectangle for edit mode
            int left = getLeft();
            int top = getTop();
            int right = getRight();
            int bottom = getBottom();
            int colorI = 0x00000000;
            int colorB = 0xFF48453C;
            guiGraphics.fill(left, top, right, bottom, colorI);
            guiGraphics.fill(left, top, right, top + 1, colorB);
            guiGraphics.fill(left, bottom - 1, right, bottom, colorB);
            guiGraphics.fill(left, top, left + 1, bottom, colorB);
            guiGraphics.fill(right - 1, top, right, bottom, colorB);
        }

        if (page != null && page != thisPage) {
            int scrollMax = page.getScrollbarMax(getBottom() - 5);
            if (isDragging) {
                if (startY != GuiBook.INSTANCE.mouseY) {
                    int scrollPosition = (int) (((GuiBook.INSTANCE.mouseY - getTop()) * (scrollMax)) / getHeight());
                    page.updateMaximumScroll(getBottom() - 5); //Update the max
                    page.setScrollPosition(scrollPosition);
                }

                startY = GuiBook.INSTANCE.mouseY;
            }

            // TODO: This needs major refactoring for GuiGraphics in 1.20.4
            PoseStack poseStack = new PoseStack();
            poseStack.pushPose();
            int scale = (int) Minecraft.getInstance().getWindow().getGuiScale();
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT, false);
            GL11.glScissor((GuiBook.INSTANCE.x + getLeft()) * scale, (int) (GuiBook.INSTANCE.y + 217 - getTop() - getHeight()) * scale, (int) getWidth() * scale, (int) getHeight() * scale);

            for (FeatureProvider feature : Lists.reverse(page.getFeatures())) {
                if (feature instanceof FeaturePreviewWindow) continue; //No Cascading
                int y = GuiBook.INSTANCE.y;
                if (page.getScroll() > 0) {
                    GuiBook.INSTANCE.y -= page.getScroll();
                }

                boolean isMouseHovering = isOverFeature(xMouse, yMouse);
                int mouseX = isMouseHovering ? GuiBook.INSTANCE.mouseX : Short.MAX_VALUE;
                int mouseY = isMouseHovering ? GuiBook.INSTANCE.mouseY + page.getScroll() : Short.MAX_VALUE;
                int originalY = GuiBook.INSTANCE.mouseY;
                if (isMouseHovering) {
                    GuiBook.INSTANCE.mouseY = GuiBook.INSTANCE.mouseY + page.getScroll();
                }

                feature.draw(mouseX, mouseY);
                feature.addTooltip(GuiBook.INSTANCE.TOOLTIP, mouseX, mouseY);
                if (isMouseHovering) {
                    GuiBook.INSTANCE.mouseY = originalY;
                }

                GuiBook.INSTANCE.y = y;
                RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT, false);
            }


            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            poseStack.popPose();

            RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT, false);
            int minY = Short.MAX_VALUE;
            for (FeatureProvider provider : page.getFeatures()) {
                if (provider.getTop() < minY) {
                    minY = provider.getTop();
                }
            }

            if (getHeight() < (scrollMax + getBottom() - 5 - minY)) {
                // Draw scrollbar background
                int left = getRight() - 10;
                int top = getTop();
                int right = getRight();
                int bottom = getBottom();
                int colorI = 0xFFB0A483;
                int colorB = 0xFF362C24;
                guiGraphics.fill(left, top, right, bottom, colorI);
                guiGraphics.fill(left, top, right, top + 1, colorB);
                guiGraphics.fill(left, bottom - 1, right, bottom, colorB);
                guiGraphics.fill(left, top, left + 1, bottom, colorB);
                guiGraphics.fill(right - 1, top, right, bottom, colorB);

                // Draw scrollbar thumb
                int pos = (int) ((page.getScroll() * (getHeight() - 10)) / scrollMax);
                int thumbLeft = getRight() - 10;
                int thumbTop = getTop() + pos;
                int thumbRight = getRight();
                int thumbBottom = getTop() + pos + 10;
                int thumbColorI = 0xFF2F271F;
                int thumbColorB = 0xFF191511;
                guiGraphics.fill(thumbLeft, thumbTop, thumbRight, thumbBottom, thumbColorI);
                guiGraphics.fill(thumbLeft, thumbTop, thumbRight, thumbTop + 1, thumbColorB);
                guiGraphics.fill(thumbLeft, thumbBottom - 1, thumbRight, thumbBottom, thumbColorB);
                guiGraphics.fill(thumbLeft, thumbTop, thumbLeft + 1, thumbBottom, thumbColorB);
                guiGraphics.fill(thumbRight - 1, thumbTop, thumbRight, thumbBottom, thumbColorB);
            }
        }
    }

    @Override
    public void scroll(boolean down, int amount) {
        if (page != null && page != thisPage) {
            page.updateMaximumScroll(getBottom() - 5); //Called constantly
            page.scroll(down, amount);
        }
    }

    @Override
    public Codec<? extends joshie.enchiridion.api.book.IFeature> codec() {
        return CODEC;
    }
}