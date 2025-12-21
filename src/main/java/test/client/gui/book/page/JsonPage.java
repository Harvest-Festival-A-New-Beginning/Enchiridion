package test.client.gui.book.page;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.client.gui.book.Book;
import uk.joshiejack.penguinlib.client.gui.book.page.AbstractPage;
import uk.joshiejack.penguinlib.test.client.gui.book.editor.JsonBookEditor;
import uk.joshiejack.penguinlib.test.client.gui.book.editor.MoveableWidget;
import uk.joshiejack.penguinlib.test.data.book.BookPage;
import uk.joshiejack.penguinlib.test.data.book.BookRegistries;
import uk.joshiejack.penguinlib.test.data.book.BookWidget;
import uk.joshiejack.penguinlib.test.data.book.element.RenderElement;
import uk.joshiejack.penguinlib.util.icon.Icon;
import uk.joshiejack.penguinlib.util.icon.ItemIcon;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * A page implementation that renders JSON-defined widgets
 * Supports both static widget-based pages and dynamic script-driven pages
 * In edit mode, creates editable MoveableWidget instances instead of static rendering
 */
@OnlyIn(Dist.CLIENT)
public class JsonPage extends AbstractPage {
    private final ResourceLocation pageId;
    private BookPage pageData;
    private final List<BookWidget> leftWidgetInstances = new ArrayList<>();
    private final List<BookWidget> rightWidgetInstances = new ArrayList<>();

    // For dynamic pages, delegate to JsonMultiPage
    @Nullable
    private AbstractPage dynamicDelegate = null;

    public JsonPage(ResourceLocation pageId) {
        super(Component.translatable("book.page." + pageId.getNamespace() + "." + pageId.getPath()));
        this.pageId = pageId;
        loadPageData();
    }

    private void loadPageData() {
        pageData = BookRegistries.PAGES.get(pageId);
        if (pageData == null) {
            pageData = new BookPage(); // Empty page if not found
        }

        // Check if this is a dynamic page type
        String type = pageData.getType();
        Component pageName = Component.translatable("book.page." + pageId.getNamespace() + "." + pageId.getPath());

        if ("left".equals(type)) {
            dynamicDelegate = new JsonMultiPage.Left(pageData, pageName);
        } else if ("right".equals(type)) {
            dynamicDelegate = new JsonMultiPage.Right(pageData, pageName);
        } else if ("both".equals(type)) {
            dynamicDelegate = new JsonMultiPage.Both(pageData, pageName);
        } else {
            // Static page - widgets are now embedded directly in page data
            dynamicDelegate = null;

            leftWidgetInstances.clear();
            leftWidgetInstances.addAll(pageData.getLeftWidgets());

            rightWidgetInstances.clear();
            rightWidgetInstances.addAll(pageData.getRightWidgets());
        }
    }

    @Override
    protected Icon getIcon() {
        return pageData != null ? pageData.getIcon() : ItemIcon.EMPTY;
    }

    @Override
    public void initLeft(Book book, int left, int top) {
        // If this is a dynamic page, delegate to the dynamic implementation
        if (dynamicDelegate != null) {
            dynamicDelegate.initLeft(book, left, top);
            return;
        }

        // Check if this is an editor in edit mode
        boolean isEditMode = book instanceof JsonBookEditor;

        if (isEditMode) {
            // EDIT MODE: Let editor handle initialization (creates widgets with canEdit=true)
            JsonBookEditor editor = (JsonBookEditor) book;
            editor.setCurrentPage(this);
            // Editor's setCurrentPage() creates editable widgets and toolbar
        } else {
            // NORMAL MODE: Create MoveableWidgets with canEdit=false
            PenguinLib.LOGGER.info("NORMAL MODE - Left page offset: ({}, {})", left, top);
            for (BookWidget widget : leftWidgetInstances) {
                RenderElement element = widget.getElement();
                if (element != null) {
                    PenguinLib.LOGGER.info("  Widget at ({}, {}) -> screen pos ({}, {})",
                            widget.getX(), widget.getY(), left + widget.getX(), top + widget.getY());
                    book.addRenderableWidget(new MoveableWidget.Display(element, widget, left, top,
                            widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight(), widget.getLayer(), widget.getScale()));
                }
            }
        }
    }

    @Override
    public void initRight(Book book, int left, int top) {
        // If this is a dynamic page, delegate to the dynamic implementation
        if (dynamicDelegate != null) {
            dynamicDelegate.initRight(book, left, top);
            return;
        }

        // Check if this is an editor in edit mode
        boolean isEditMode = book instanceof JsonBookEditor;

        if (isEditMode) {
            // EDIT MODE: Editor already initialized widgets in initLeft via setCurrentPage
            // Nothing to do here
        } else {
            // NORMAL MODE: Create MoveableWidgets with canEdit=false
            PenguinLib.LOGGER.info("NORMAL MODE - Right page offset: ({}, {})", left, top);
            for (BookWidget widget : rightWidgetInstances) {
                RenderElement element = widget.getElement();
                if (element != null) {
                    PenguinLib.LOGGER.info("  Widget at ({}, {}) -> screen pos ({}, {})",
                            widget.getX(), widget.getY(), left + widget.getX(), top + widget.getY());
                    book.addRenderableWidget(new MoveableWidget.Display(element, widget, left, top,
                            widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight(), widget.getLayer(), widget.getScale()));
                }
            }
        }
    }

    public ResourceLocation getPageId() {
        return pageId;
    }

    public BookPage getPageData() {
        return pageData;
    }

    public List<BookWidget> getLeftWidgetInstances() {
        return leftWidgetInstances;
    }

    public List<BookWidget> getRightWidgetInstances() {
        return rightWidgetInstances;
    }

    /**
     * Reload the page data (useful after editing)
     */
    public void reload() {
        loadPageData();
    }
}
