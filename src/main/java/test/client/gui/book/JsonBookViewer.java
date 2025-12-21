package test.client.gui.book;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.test.client.gui.book.editor.MoveableWidget;
import uk.joshiejack.penguinlib.test.data.book.BookWidget;
import uk.joshiejack.penguinlib.test.item.JsonBookMenu;

/**
 * Display-only viewer for JSON-defined books
 * Shows book content with interactive buttons but no editing capabilities
 */
@OnlyIn(Dist.CLIENT)
public class JsonBookViewer extends JsonBook {
    protected static final Object2ObjectMap<ResourceLocation, JsonBookViewer> INSTANCES = new Object2ObjectOpenHashMap<>();

    private JsonBookViewer(ResourceLocation bookId, JsonBookMenu container, Inventory inv) {
        super(bookId, container, inv);
    }

    /**
     * Get or create a cached JsonBookViewer instance for the given book
     */
    public static JsonBookViewer getInstance(ResourceLocation bookId, Inventory inv) {
        return INSTANCES.computeIfAbsent(bookId, id -> new JsonBookViewer(bookId, new JsonBookMenu(-1, bookId), inv));
    }

    @Override
    protected void addWidget(BookWidget widget, int offsetX, int offsetY) {
        // Create MoveableWidget.Display for viewing mode
        MoveableWidget.Display display = new MoveableWidget.Display(
                widget.getElement(),
                widget,
                offsetX,
                offsetY,
                widget.getX(),
                widget.getY(),
                widget.getWidth(),
                widget.getHeight(),
                widget.getLayer(),
                widget.getScale()
        );

        // Add as renderable only for non-button widgets
        // Buttons need to be added with addRenderableWidget to receive clicks
        if (display.isButton()) {
            addRenderableWidget(display);
        } else {
            addRenderableOnly(display);
        }
    }

    @Override
    protected void renderExtras(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        // No extra rendering needed for viewer mode
    }
}
