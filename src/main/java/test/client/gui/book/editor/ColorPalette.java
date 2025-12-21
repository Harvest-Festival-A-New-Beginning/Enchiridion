package test.client.gui.book.editor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.PenguinLib;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages color palettes loaded from JSON configuration
 * Provides Paint-style color grids that can be customized
 */
@OnlyIn(Dist.CLIENT)
public class ColorPalette {
    private static final Gson GSON = new GsonBuilder().create();
    private static ColorPalette INSTANCE = null;

    private List<Integer> colors = new ArrayList<>();
    private int colorsPerRow = 10;

    public static class Config {
        public List<String> colors = new ArrayList<>();
        public int colorsPerRow = 10;
    }

    private ColorPalette() {
        loadDefaultPalette();
    }

    public static ColorPalette getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ColorPalette();
        }
        return INSTANCE;
    }

    private void loadDefaultPalette() {
        try {
            // Try to load from resources
            InputStream stream = getClass().getResourceAsStream("/assets/penguinlib/book_editor/color_palette.json");
            if (stream != null) {
                Config config = GSON.fromJson(new InputStreamReader(stream), Config.class);
                colors.clear();
                for (String colorHex : config.colors) {
                    try {
                        // Remove # if present
                        colorHex = colorHex.replace("#", "");
                        int color = Integer.parseInt(colorHex, 16);
                        colors.add(color);
                    } catch (NumberFormatException e) {
                        PenguinLib.LOGGER.warn("Invalid color format: {}", colorHex);
                    }
                }
                colorsPerRow = config.colorsPerRow;
                PenguinLib.LOGGER.info("Loaded {} colors from palette configuration", colors.size());
                return;
            }
        } catch (Exception e) {
            PenguinLib.LOGGER.warn("Failed to load color palette from JSON, using built-in defaults", e);
        }

        // Fallback to Paint-style palette if file doesn't exist
        loadPaintStylePalette();
    }

    private void loadPaintStylePalette() {
        // Paint-style color palette with gradients
        colors.clear();
        colorsPerRow = 10;

        // Row 1: Black to white gradient
        colors.add(0x000000); // Black
        colors.add(0x444444); // Dark gray
        colors.add(0x666666); // Gray
        colors.add(0x888888); // Light gray
        colors.add(0xAAAAAA); // Lighter gray
        colors.add(0xCCCCCC); // Very light gray
        colors.add(0xDDDDDD); // Nearly white
        colors.add(0xEEEEEE); // Almost white
        colors.add(0xF5F5F5); // Off white
        colors.add(0xFFFFFF); // White

        // Row 2: Reds and oranges
        colors.add(0x800000); // Maroon
        colors.add(0xA00000); // Dark red
        colors.add(0xC00000); // Red
        colors.add(0xFF0000); // Bright red
        colors.add(0xFF4444); // Light red
        colors.add(0xFF8888); // Pink red
        colors.add(0xCC4400); // Dark orange
        colors.add(0xFF6600); // Orange
        colors.add(0xFF8800); // Bright orange
        colors.add(0xFFAA44); // Light orange

        // Row 3: Yellows and greens
        colors.add(0xCC8800); // Dark yellow
        colors.add(0xFFCC00); // Yellow
        colors.add(0xFFFF00); // Bright yellow
        colors.add(0xFFFF88); // Light yellow
        colors.add(0x888800); // Olive
        colors.add(0xAACC00); // Yellow-green
        colors.add(0x88FF00); // Lime
        colors.add(0x00FF00); // Bright green
        colors.add(0x008800); // Dark green
        colors.add(0x00CC00); // Green

        // Row 4: Cyans and blues
        colors.add(0x00FF88); // Spring green
        colors.add(0x00FFAA); // Light cyan
        colors.add(0x00FFCC); // Cyan
        colors.add(0x00FFFF); // Bright cyan
        colors.add(0x00CCFF); // Sky blue
        colors.add(0x0088FF); // Light blue
        colors.add(0x0000FF); // Bright blue
        colors.add(0x0000CC); // Blue
        colors.add(0x000088); // Dark blue
        colors.add(0x000044); // Navy

        // Row 5: Purples and magentas
        colors.add(0x4400CC); // Dark purple
        colors.add(0x6600FF); // Purple
        colors.add(0x8800FF); // Bright purple
        colors.add(0xAA44FF); // Light purple
        colors.add(0xCC00CC); // Magenta
        colors.add(0xFF00FF); // Bright magenta
        colors.add(0xFF00AA); // Pink magenta
        colors.add(0xFF0088); // Hot pink
        colors.add(0xFF88CC); // Light pink
        colors.add(0xFFCCFF); // Pale pink

        PenguinLib.LOGGER.info("Loaded Paint-style color palette with {} colors", colors.size());
    }

    public List<Integer> getColors() {
        return colors;
    }

    public int getColorsPerRow() {
        return colorsPerRow;
    }

    public int getColorCount() {
        return colors.size();
    }
}
