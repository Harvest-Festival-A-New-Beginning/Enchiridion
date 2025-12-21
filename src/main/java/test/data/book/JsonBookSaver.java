package test.data.book;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import uk.joshiejack.penguinlib.PenguinLib;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Handles saving book data to JSON files
 * Saves to the data folder for hot-reloading
 */
public class JsonBookSaver {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Get the base data path for the mod
     */
    private static Path getDataPath(String modid) {
        // Save to run/data/{modid}/data/{modid}/
        return Paths.get("data", modid, "data", modid);
    }

    /**
     * Save a book definition to JSON
     */
    public static boolean saveBook(ResourceLocation id, BookDefinition book) {
        try {
            Path path = getDataPath(id.getNamespace())
                    .resolve("book")
                    .resolve(id.getPath() + ".json");

            JsonElement json = BookDefinition.CODEC.encodeStart(JsonOps.INSTANCE, book)
                    .getOrThrow(false, error -> PenguinLib.LOGGER.error("Failed to encode book: {}", error));

            Files.createDirectories(path.getParent());
            Files.writeString(path, GSON.toJson(json));

            PenguinLib.LOGGER.info("Saved book to: {}", path);
            return true;
        } catch (Exception e) {
            PenguinLib.LOGGER.error("Failed to save book {}", id, e);
            return false;
        }
    }

    /**
     * Save a tab definition to JSON
     */
    public static boolean saveTab(ResourceLocation id, BookTab tab) {
        try {
            Path path = getDataPath(id.getNamespace())
                    .resolve("book/tab")
                    .resolve(id.getPath() + ".json");

            JsonElement json = BookTab.CODEC.encodeStart(JsonOps.INSTANCE, tab)
                    .getOrThrow(false, error -> PenguinLib.LOGGER.error("Failed to encode tab: {}", error));

            Files.createDirectories(path.getParent());
            Files.writeString(path, GSON.toJson(json));

            PenguinLib.LOGGER.info("Saved tab to: {}", path);
            return true;
        } catch (Exception e) {
            PenguinLib.LOGGER.error("Failed to save tab {}", id, e);
            return false;
        }
    }

    /**
     * Save a page definition to JSON
     */
    public static boolean savePage(ResourceLocation id, BookPage page) {
        try {
            Path path = getDataPath(id.getNamespace())
                    .resolve("book/page")
                    .resolve(id.getPath() + ".json");

            JsonElement json = BookPage.CODEC.encodeStart(JsonOps.INSTANCE, page)
                    .getOrThrow(false, error -> PenguinLib.LOGGER.error("Failed to encode page: {}", error));

            Files.createDirectories(path.getParent());
            Files.writeString(path, GSON.toJson(json));

            PenguinLib.LOGGER.info("Saved page to: {}", path);
            return true;
        } catch (Exception e) {
            PenguinLib.LOGGER.error("Failed to save page {}", id, e);
            return false;
        }
    }

    /**
     * Save a widget definition to JSON
     */
    public static boolean saveWidget(ResourceLocation id, BookWidget widget) {
        try {
            Path path = getDataPath(id.getNamespace())
                    .resolve("book/widget")
                    .resolve(id.getPath() + ".json");

            JsonElement json = BookWidget.CODEC.encodeStart(JsonOps.INSTANCE, widget)
                    .getOrThrow(false, error -> PenguinLib.LOGGER.error("Failed to encode widget: {}", error));

            Files.createDirectories(path.getParent());
            Files.writeString(path, GSON.toJson(json));

            PenguinLib.LOGGER.info("Saved widget to: {}", path);
            return true;
        } catch (Exception e) {
            PenguinLib.LOGGER.error("Failed to save widget {}", id, e);
            return false;
        }
    }

    /**
     * Create a new book with a given name
     */
    public static ResourceLocation createNewBook(String modid, String name) {
        ResourceLocation id = new ResourceLocation(modid, sanitizeName(name));
        BookDefinition book = new BookDefinition();

        if (saveBook(id, book)) {
            return id;
        }

        return null;
    }

    /**
     * Sanitize a book name to be a valid resource location path
     */
    private static String sanitizeName(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9_/.-]", "_")
                .replaceAll("_{2,}", "_");
    }
}
