package joshie.enchiridion.gui.book.element;

import joshie.enchiridion.EConfig;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

/**
 * Registry for toolbar elements with auto-generation support
 * Each element type can register itself with metadata for toolbar display
 */
public class ToolbarRegistry {
    private static final List<ToolbarMetadata> REGISTERED = new ArrayList<>();

    static {
        // Register all toolbar elements in sort order
        register(new ToolbarMetadata(1, icon("text"), () -> new TextElement(EConfig.SETTINGS.defaultText.get(), 1F, 0x555555), 200, 80));
        register(new ToolbarMetadata(5, icon("box"), () -> new BoxElement(0xFFFFFFFF), 50, 5));
        register(new ToolbarMetadata(10, icon("image"), () -> new ImageElement(new ResourceLocation("minecraft", "textures/block/dirt.png")), 100, 100));
        register(new ToolbarMetadata(15, icon("item"), () -> new ItemElement("minecraft:dirt"), 16, 16));
        register(new ToolbarMetadata(20, icon("arrow"), () -> new ActionButtonElement(null), 18, 10));
        register(new ToolbarMetadata(30, icon("crafting"), () -> new RecipeElement(EConfig.getDefaultItem()), 160, 80));
        register(new ToolbarMetadata(40, icon("preview"), () -> new PreviewWindowElement(0), 100, 100));

        // Optional/advanced elements - lower priority
        register(new ToolbarMetadata(100, icon("entity"), () -> new EntityElement(new ResourceLocation("minecraft", "pig"), 1.0F, 0F, 0F, true, false), 64, 64));
        register(new ToolbarMetadata(110, icon("model"), () -> new ModelElement(new ResourceLocation("minecraft", "dirt"), 1.0F, 30F, 45F, false), 64, 64));
        register(new ToolbarMetadata(120, icon("javascript"), () -> new JSElement("return 'Hello';", null, 1F, true, false, "§cError"), 200, 80));
        register(new ToolbarMetadata(130, icon("resource"), () -> new ResourceElement("minecraft:textures/block/dirt.png"), 100, 100));
    }

    private static ResourceLocation icon(String name) {
        return new ResourceLocation("enchiridion", "textures/gui/toolbar/" + name + ".png");
    }

    public static void register(ToolbarMetadata metadata) {
        REGISTERED.add(metadata);
    }

    /**
     * Get all registered toolbar elements sorted by sort order
     */
    public static List<ToolbarMetadata> getAll() {
        List<ToolbarMetadata> sorted = new ArrayList<>(REGISTERED);
        sorted.sort(Comparator.comparingInt(ToolbarMetadata::sortOrder));
        return sorted;
    }

    /**
     * Get toolbar element by index (after sorting)
     */
    public static ToolbarMetadata get(int index) {
        List<ToolbarMetadata> all = getAll();
        return index >= 0 && index < all.size() ? all.get(index) : null;
    }

    /**
     * Get count of registered toolbar elements
     */
    public static int size() {
        return REGISTERED.size();
    }
}
