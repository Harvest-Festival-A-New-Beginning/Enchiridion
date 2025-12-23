package joshie.enchiridion.gui.book;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.platform.GlConst;
import joshie.enchiridion.EConfig;
import joshie.enchiridion.Enchiridion;
import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.api.book.IBook;
import joshie.enchiridion.api.book.IBookHelper;
import joshie.enchiridion.data.book.FeatureProvider;
import joshie.enchiridion.data.book.Page;
import joshie.enchiridion.gui.book.element.PreviewWindowElement;
import joshie.enchiridion.helpers.*;
import joshie.enchiridion.lib.EInfo;
import joshie.enchiridion.util.TextEditor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;
import uk.joshiejack.penguinlib.world.inventory.AbstractBookMenu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class GuiBook extends GuiBase implements IBookHelper {
    //Page Number Cache - shared across all instances
    private static HashMap<String, Integer> pageCache = new HashMap<>();
    private static HashMap<String, PreviewWindowElement> scrollFeatures = new HashMap<>();

    private Set<AbstractGuiOverlay> overlays = new HashSet<>();
    private boolean isEditMode = false; // Whether we are in edit mode or not
    private IBook book; // The current book being displayed
    private Page page; // The current page being displayed
    private FeatureProvider selected; //Currently selected feature
    private Set<FeatureProvider> group = new HashSet<>(); //Groups?
    private Set<FeatureProvider> clipboard = new HashSet<>(); //Clipboard
    private float red, green, blue; //Colour to render the book
    private boolean isGroupMoveMode = false;

    // Overlay instances - created per GuiBook instance
    private final GuiGrid grid;
    private final GuiTimeLine timeLine;
    private final GuiToolbar toolbar;
    private final GuiLayers layers;
    private final GuiSimpleEditor simpleEditor;

    public GuiBook(AbstractBookMenu container, Inventory inventory) {
        super(EInfo.MODID, container, inventory, Component.translatable("enchiridion.guiBook.title"));

        // Create overlay instances
        this.grid = new GuiGrid(this);
        this.timeLine = new GuiTimeLine(this);
        this.toolbar = new GuiToolbar(this);
        this.layers = new GuiLayers(this);
        this.simpleEditor = new GuiSimpleEditor(this);

        // Register overlays
        registerOverlay(grid);
        registerOverlay(timeLine);
        registerOverlay(toolbar);
        registerOverlay(layers);
        registerOverlay(simpleEditor);

        // Register toolbar buttons from API
        for (Object button : EnchiridionAPI.instance.getToolbarButtons()) {
            if (button instanceof joshie.enchiridion.api.gui.IToolbarButton) {
                toolbar.registerButton((joshie.enchiridion.api.gui.IToolbarButton) button);
            }
        }
    }

    public GuiSimpleEditor getSimpleEditor() {
        return simpleEditor;
    }

    // Public getters for protected position fields (needed by overlay classes)
    public int getLeftPos() {
        return leftPos;
    }

    public int getTopPos() {
        return topPos;
    }

    public GuiTimeLine getTimeLine() {
        return timeLine;
    }

    public GuiLayers getLayers() {
        return layers;
    }

    public GuiToolbar getToolbar() {
        return toolbar;
    }

    public GuiGrid getGrid() {
        return grid;
    }

    public static HashMap<String, Integer> getPageCache() {
        return pageCache;
    }

    public static HashMap<String, PreviewWindowElement> getScrollFeatures() {
        return scrollFeatures;
    }

    public void registerOverlay(AbstractGuiOverlay overlay) {
        overlays.add(overlay);
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTicks, mouseX, mouseY);

        if (book.isBackgroundVisible()) {
            // Draw custom background
            ResourceLocation bg = book.getBackgroundResource();
            int left = book.getBackgroundStartX();
            int top = book.getBackgroundStartY();
            int right = book.getBackgroundEndX();
            int bottom = book.getBackgroundEndY();
            int w = right - left;
            int h = bottom - top;
            guiGraphics.blit(bg, leftPos + left, topPos + top, 0, 0, w, h, w, h);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int x2, int y2, float partialTicks) {
        // Convert screen coordinates to GUI-relative coordinates
        int mouseX = x2 - leftPos;
        int mouseY = y2 - topPos;

        // Update feature positions before rendering (handles scroll offset)
        if (page != null) {
            int bookX = this.leftPos;
            int bookY = this.topPos - page.getScroll();
            for (FeatureProvider feature : page.getFeatures()) {
                feature.setX(bookX + feature.relativeX);
                feature.setY(bookY + feature.relativeY);
            }
        }

        // super.render() calls renderBg() and renders all widgets (features) via addRenderableWidget()
        super.render(guiGraphics, x2, y2, partialTicks);

        // Collect tooltips from features
        if (page != null) {
            int scrollAdjustedMouseY = mouseY + page.getScroll();
            for (FeatureProvider feature : page.getFeatures()) {
                feature.addTooltip(TOOLTIP, mouseX, scrollAdjustedMouseY);
            }
        }

        // Draw editor overlays
        if (isEditMode) {
            for (AbstractGuiOverlay overlay : overlays) {
                overlay.draw(guiGraphics, mouseX, mouseY, this);
                overlay.addToolTip(TOOLTIP, mouseX, mouseY);
            }
        }

        // Render tooltips if any
        if (!TOOLTIP.isEmpty()) {
            guiGraphics.renderTooltip(this.font, TOOLTIP.stream().map(s -> Component.literal(s).getVisualOrderText()).toList(), x2, y2);
        }
    }

    @Override
    public void initScreen(@Nonnull Minecraft minecraft, @Nonnull net.minecraft.world.entity.player.Player player) {
        super.initScreen(minecraft, player);

        // Add features as widgets using addRenderableWidget() (PenguinLib pattern)
        if (page != null) {
            for (FeatureProvider feature : page.getFeatures()) {
                feature.init(this);
                addRenderableWidget(feature);
            }
        }
    }

    @Override
    public void init() {
        super.init(); // Call parent to set up background positions (centre, bgLeftOffset)

        simpleEditor.setEditor(null); //Reset the editor
        TextEditor.INSTANCE.clearEditable();

        if (isEditMode) {
            for (AbstractGuiOverlay overlay : overlays) {
                overlay.init();
            }
        }
    }

    @Override
    public void containerTick() {
        super.containerTick();
        if (isEditMode) {
            for (AbstractGuiOverlay overlay : overlays) {
                overlay.tick();
            }
        }
    }

    @Override
    @Nullable
    public GuiEventListener getFocused() {
        return simpleEditor.getFocused(); //TODO?
    }

    @Override
    public void removed() {
        // TODO: setRepeatEvents() removed in 1.20.4 - need to find alternative API
        // Minecraft.getInstance().keyboardHandler.setRepeatEvents(false);
        if (!book.doesBookForgetClose() && page != null) pageCache.put(book.getUniqueName(), page.getPageNumber());
        if (isEditMode) {
            if (selected != null) selected.deselect();

            try {
                book.setMadeIn189(); //Force it to a mc189book with new formatting
                File toSave = FileHelper.getSaveJSONForBook(book);
                Writer writer = new OutputStreamWriter(new FileOutputStream(toSave), StandardCharsets.UTF_8);
                // Cast to Book for Codec serialization
                if (book instanceof joshie.enchiridion.data.book.Book) {
                    writer.write(CodecHelper.toJson(joshie.enchiridion.data.book.Book.CODEC, (joshie.enchiridion.data.book.Book) book));
                }
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean charTyped(char character, int key) {
        // TODO: setRepeatEvents() removed in 1.20.4 - need to find alternative API
        // Minecraft.getInstance().keyboardHandler.setRepeatEvents(true);

        //super.charTyped(character, key);

        if (isEditMode) {
            group.stream().filter(g -> g.keyTyped(character, key, this)).forEach(g -> {
                page.removeFeature(g);
                group = new HashSet<>();
            });

            //Copy to clipboard
            long handle = Minecraft.getInstance().getWindow().getWindow();
            if (MCClientHelper.isCtrlPressed() && (InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_C) || InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_X))) {
                clipboard.clear();
                for (FeatureProvider provider : group) {
                    FeatureProvider copy = provider.copy();
                    copy.update(getPage());
                    clipboard.add(copy);
                }

                if (InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_X)) {
                    for (FeatureProvider provider : group) {
                        page.removeFeature(provider);
                    }
                }
            } else if (MCClientHelper.isCtrlPressed() && InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_V)) { //Paste features
                for (FeatureProvider provider : clipboard) {
                    // copy() returns FeatureProvider which is also an IFeature (FeatureProvider implements both)
                    page.addFeature(provider.copy(), provider.getLeft(), provider.getTop(), provider.getWidth(), provider.getHeight(), provider.isLocked(), !provider.isVisible(), provider.isFromTemplate());
                }
            }

            //Update itself
            group.stream().filter(Objects::nonNull).forEach(provider -> provider.update(getPage()));

            for (AbstractGuiOverlay overlay : overlays) {
                overlay.charTyped(character, key);
                overlay.updateSearch(simpleEditor.getText());
            }
        }
        return true;
    }

    private transient boolean wasControlPressedBefore = false;

    public void selectLayer(FeatureProvider feature, int mouseX, int mouseY) {
        if (selected != null) selected.deselect();
        selected = feature;
        selected.select(mouseX, mouseY + page.getScroll());
        boolean isCtrlPressedNow = MCClientHelper.isCtrlPressed();
        //If we didn't have control before and we do now
        if (isCtrlPressedNow) {
            wasControlPressedBefore = true; //It has now been pressed
            isGroupMoveMode = false;
        } else if (wasControlPressedBefore) { //Now if it was pressed before but isn't now
            wasControlPressedBefore = false; //Reset the info
            isGroupMoveMode = true;
        } else if (!group.contains(selected)) { //If the control wasn't pressed before trying to move this item, then clear the group
            group = new HashSet<>();
            isGroupMoveMode = false;
        }

        group.add(selected); //Add it to the group
        for (FeatureProvider provider : group) { //Refresh the x position
            provider.select(mouseX, mouseY + page.getScroll());
        }
    }

    @Override
    public boolean mouseClicked(double x, double y, int mouseButton) {
        super.mouseClicked(x, y, mouseButton);
        int mouseX = (int) x;
        int mouseY = (int) y;

        //Perform clicks for the overlays
        if (isEditMode) {
            for (AbstractGuiOverlay overlay : overlays) {
                if (overlay.mouseClicked(mouseX, mouseY, this)) {
                    return false;
                }
            }
        }

        //Perform clicks for the features
        for (FeatureProvider feature : page.getFeatures()) {
            if (feature.mouseClicked(mouseX, mouseY + page.getScroll(), mouseButton, this)) {
                if (isEditMode && mouseButton == 0) {
                    selectLayer(feature, mouseX, mouseY);
                }
                return false;
            }
        }

        //If nothing was clicked on, remove the current selection
        if (selected != null) selected.deselect();
        selected = null;
        group.forEach(FeatureProvider::deselect);

        group = new HashSet<>();
        return true;
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        int mouseX = (int) x;
        int mouseY = (int) y;

        isGroupMoveMode = false;
        for (FeatureProvider provider : page.getFeatures()) {
            provider.mouseReleased(mouseX, mouseY + page.getScroll(), button);
        }

        //Perform releases for the overlays
        if (isEditMode) {
            for (AbstractGuiOverlay overlay : overlays) {
                overlay.mouseReleased(mouseX, mouseY, this);
            }
        }
        return super.mouseReleased(x, y, button);
    }

    @Override
    public boolean mouseDragged(double mX, double mY, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
        int mouseX = (int) mX;
        int mouseY = (int) mY;

        if (!layers.isDragging()) {
            for (FeatureProvider provider : group) {
                provider.follow(mouseX, mouseY + page.getScroll(), isGroupMoveMode, this);
            }
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mX, double mY, double deltaX, double deltaY) {
        int mouseX = (int) mX;
        int mouseY = (int) mY;

        if (deltaY != 0) {
            boolean down = deltaY < 0;
            if (isEditMode) {
                for (AbstractGuiOverlay overlay : overlays) {
                    overlay.scroll(down, mouseX, mouseY);
                }
            }

            for (FeatureProvider provider : page.getFeatures()) {
                provider.scroll(mouseX, mouseY, down);
            }

            page.updateMaximumScroll(0); //Called constantly
            page.scroll(down, 10);
        }
        return super.mouseScrolled(mX, mY, deltaX, deltaY);
    }

    // Helper methods
    @Override //Getters
    public boolean isEditMode() {
        return isEditMode;
    }

    @Override
    public IBook getBook() {
        return book;
    }

    @Override
    public Page getPage() {
        return page;
    }

    @Override
    public FeatureProvider getSelected() {
        return selected;
    }

    @Override
    public boolean isGroupSelected(FeatureProvider provider) {
        return group.contains(provider);
    }

    //Setters
    @Override
    public IBookHelper setBook(IBook book, boolean playerSneaked) {
        this.book = book; //Set the book
        try {
            int color = book.getColorAsInt();
            red = (color >> 16 & 255) / 255.0F;
            green = (color >> 8 & 255) / 255.0F;
            blue = (color & 255) / 255.0F;
        } catch (Exception ignored) {
        }

        //If the config allows editing, and the book isn't locked, enable edit mode
        isEditMode = EConfig.SETTINGS.enableEditing.get() && !book.isLocked() && playerSneaked;

        Integer number = pageCache.get(book.getUniqueName());
        if (number == null) number = book.getDefaultPage();
        jumpToPageIfExists(number);
        if (page == null) { //If we've got a dumb book, without a good page, then let's create a new blank page
            page = DefaultHelper.addDefaults(this.book, new Page(0).setBook(this.book));
            book.addPage(page);
        }
        return this;
    }

    @Override
    public void setSelected(FeatureProvider provider) {
        this.selected = provider;
    }

    @Override
    public boolean jumpToPageIfExists(int number) {
        for (Page page : getBook().getPages()) {
            if (page.getPageNumber() == number) {
                simpleEditor.setEditor(null); //Reset the editor
                TextEditor.INSTANCE.clearEditable();

                if (this.page != null) {
                    int closest = (int) (5 * (Math.floor(page.getPageNumber() / 5)));
                    int difference = closest - this.page.getPageNumber();
                    if (difference > 50 || difference < -50) {
                        timeLine.setStartPage(closest);
                    }
                }

                this.page = page;
                return true;
            }
        }

        return false;
    }

    @Override
    public Page getPageIfNotExists(int number) {
        Page page = JumpHelper.getPageByNumber(book, number);
        if (page == null) {
            page = new Page(number).setBook(book);
            book.addPage(page);
            return page;
        } else return null;
    }
}