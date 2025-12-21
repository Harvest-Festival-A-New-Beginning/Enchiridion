package test.client.gui.book.page;

import dev.latvian.mods.rhino.ScriptableObject;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.client.gui.book.Book;
import uk.joshiejack.penguinlib.client.gui.book.page.AbstractMultiPage;
import uk.joshiejack.penguinlib.scripting.Interpreter;
import uk.joshiejack.penguinlib.scripting.ScriptFactory;
import uk.joshiejack.penguinlib.test.client.gui.book.editor.MoveableWidget;
import uk.joshiejack.penguinlib.test.data.book.BookPage;
import uk.joshiejack.penguinlib.test.data.book.BookRegistries;
import uk.joshiejack.penguinlib.test.data.book.BookWidget;
import uk.joshiejack.penguinlib.test.data.book.element.RenderElement;
import uk.joshiejack.penguinlib.test.data.book.element.TextElement;
import uk.joshiejack.penguinlib.util.icon.Icon;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON-defined dynamic pages that use JavaScript scripts to populate entries
 * Supports left, right, and both-side layouts like AbstractMultiPage
 */
@OnlyIn(Dist.CLIENT)
public class JsonMultiPage {

    /**
     * Helper method to render an entry using a template widget
     * Applies data from the entry object to the template's text elements
     */
    private static void renderEntryWithTemplate(Book book, BookPage pageData, int left, int top,
                                                Object entry, int yOffset) {
        ResourceLocation templateId = pageData.getEntryTemplate();
        if (templateId == null) {
            return;
        }

        BookWidget template = BookRegistries.WIDGETS.get(templateId);
        if (template == null) {
            PenguinLib.LOGGER.warn("Entry template not found: {}", templateId);
            return;
        }

        // Clone the template widget and apply data from entry
        BookWidget clonedWidget = cloneWidgetWithData(template, entry);

        // Render the widget at the appropriate position
        int y = top + yOffset;
        book.addRenderableOnly(new MoveableWidget.Display(
                clonedWidget.getElement(),
                clonedWidget,
                left,
                y,
                clonedWidget.getX(),
                clonedWidget.getY(),
                clonedWidget.getWidth(),
                clonedWidget.getHeight(),
                clonedWidget.getLayer(),
                clonedWidget.getScale()
        ));
    }

    /**
     * Clone a widget and apply data from a JavaScript object to its element
     * Supports basic text substitution for TextElements
     */
    private static BookWidget cloneWidgetWithData(BookWidget template, Object entry) {
        RenderElement element = template.getElement();
        if (element == null) {
            return new BookWidget();
        }

        RenderElement newElement = element;

        // If it's a text element and entry is a ScriptableObject, create a new TextElement with substituted text
        if (element instanceof TextElement textElement && entry instanceof ScriptableObject scriptObj) {
            // Try to get a "text" or "name" property from the JavaScript object
            Object textValue = scriptObj.get(Interpreter.context, "text", scriptObj);
            if (textValue == null || textValue == ScriptableObject.NOT_FOUND) {
                textValue = scriptObj.get(Interpreter.context, "name", scriptObj);
            }

            if (textValue != null && textValue != ScriptableObject.NOT_FOUND) {
                // Create a new TextElement with the substituted text
                newElement = new TextElement(
                        textValue.toString(), // Substituted text
                        0x000000, // Default color
                        false     // No shadow
                );
            } else {
                // No data to substitute - use original element
                newElement = textElement;
            }
        }

        // Clone the widget with the same position/size/layer/scale but new element
        return new BookWidget(
                template.getX(),
                template.getY(),
                template.getWidth(),
                template.getHeight(),
                template.getLayer(),
                template.getScale(),
                newElement
        );
    }

    /**
     * Left-side dynamic page
     */
    @OnlyIn(Dist.CLIENT)
    public static class Left extends AbstractMultiPage.Left<Object> {
        private final BookPage pageData;

        public Left(BookPage pageData, Component name) {
            super(name, pageData.getPerPage());
            this.pageData = pageData;
        }

        @Override
        protected Icon getIcon() {
            return pageData.getIcon();
        }

        @Override
        protected List<Object> getEntries() {
            if (pageData.getScript() == null) {
                PenguinLib.LOGGER.warn("Dynamic page {} has no script defined", pageData.id());
                return new ArrayList<>();
            }

            List<Object> result = ScriptFactory.getResult(
                    pageData.getScript(),
                    pageData.getEntriesFunction(),
                    new ArrayList<>()
            );

            return result != null ? result : new ArrayList<>();
        }

        @Override
        protected void initEntry(Book book, int left, int top, int id, Object entry) {
            if (pageData.getEntryTemplate() != null) {
                // Render entry using template
                // Calculate vertical offset based on entry index (assuming ~28 pixels per entry)
                int yOffset = id * 28;
                renderEntryWithTemplate(book, pageData, left, top, entry, yOffset);
            } else {
                // No template - just log for debugging
                PenguinLib.LOGGER.debug("Entry {}: {}", id, entry);
            }
        }
    }

    /**
     * Right-side dynamic page
     */
    @OnlyIn(Dist.CLIENT)
    public static class Right extends AbstractMultiPage.Right<Object> {
        private final BookPage pageData;

        public Right(BookPage pageData, Component name) {
            super(name, pageData.getPerPage());
            this.pageData = pageData;
        }

        @Override
        protected Icon getIcon() {
            return pageData.getIcon();
        }

        @Override
        protected List<Object> getEntries() {
            if (pageData.getScript() == null) {
                PenguinLib.LOGGER.warn("Dynamic page {} has no script defined", pageData.id());
                return new ArrayList<>();
            }

            List<Object> result = ScriptFactory.getResult(
                    pageData.getScript(),
                    pageData.getEntriesFunction(),
                    new ArrayList<>()
            );

            return result != null ? result : new ArrayList<>();
        }

        @Override
        protected void initEntry(Book book, int left, int top, int id, Object entry) {
            if (pageData.getEntryTemplate() != null) {
                // Render entry using template
                // Calculate vertical offset based on entry index (assuming ~28 pixels per entry)
                int yOffset = id * 28;
                renderEntryWithTemplate(book, pageData, left, top, entry, yOffset);
            } else {
                // No template - just log for debugging
                PenguinLib.LOGGER.debug("Entry {}: {}", id, entry);
            }
        }
    }

    /**
     * Both-sides dynamic page
     */
    @OnlyIn(Dist.CLIENT)
    public static class Both extends AbstractMultiPage.Both<Object> {
        private final BookPage pageData;

        public Both(BookPage pageData, Component name) {
            super(name, pageData.getPerPage());
            this.pageData = pageData;
        }

        @Override
        protected Icon getIcon() {
            return pageData.getIcon();
        }

        @Override
        protected List<Object> getEntries() {
            if (pageData.getScript() == null) {
                PenguinLib.LOGGER.warn("Dynamic page {} has no script defined", pageData.id());
                return new ArrayList<>();
            }

            return ScriptFactory.getResult(
                    pageData.getScript(),
                    pageData.getEntriesFunction(),
                    new ArrayList<>()
            );
        }

        @Override
        protected void initEntry(Book book, int left, int top, int id, Object entry) {
            if (pageData.getEntryTemplate() != null) {
                // Render entry using template
                // Calculate vertical offset based on entry index (assuming ~28 pixels per entry)
                int yOffset = id * 28;
                renderEntryWithTemplate(book, pageData, left, top, entry, yOffset);
            } else {
                // No template - just log for debugging
                PenguinLib.LOGGER.debug("Entry {}: {}", id, entry);
            }
        }
    }
}
