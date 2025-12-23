package joshie.enchiridion.gui.book;

import joshie.enchiridion.api.gui.IToolbarButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Internal registry for toolbar buttons.
 * Not part of the public API - buttons are registered during client setup.
 */
public class ToolbarButtonRegistry {
    private static final List<IToolbarButton> BUTTONS = new ArrayList<>();

    public static void register(IToolbarButton button) {
        BUTTONS.add(button);
    }

    public static List<IToolbarButton> getAll() {
        return new ArrayList<>(BUTTONS);
    }

    public static void clear() {
        BUTTONS.clear();
    }
}
