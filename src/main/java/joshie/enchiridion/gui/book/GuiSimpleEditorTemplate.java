package joshie.enchiridion.gui.book;

import joshie.enchiridion.EConfig;
import joshie.enchiridion.Enchiridion;
import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.data.book.FeatureProvider;
import joshie.enchiridion.data.book.ITemplate;
import joshie.enchiridion.data.book.Page;
import joshie.enchiridion.data.book.Template;
import joshie.enchiridion.helpers.MCClientHelper;
import joshie.enchiridion.lib.EnchiridionRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class GuiSimpleEditorTemplate extends GuiSimpleEditorAbstract {
    public static final GuiSimpleEditorTemplate INSTANCE = new GuiSimpleEditorTemplate();

    private final HashMap<String, ITemplate> templates = new HashMap<>();
    private ArrayList<ITemplate> sorted = new ArrayList<>();
    private int position = 0;

    protected GuiSimpleEditorTemplate() {
    }

    /**
     * Load templates from the registry (default templates from data packs).
     * Should be called after resource reload.
     */
    public void loadFromRegistry() {
        // TODO: Implement proper ReloadableRegistry iteration when PenguinLib API is finalized
        // Templates are currently registered manually via registerTemplate()
    }

    public void registerTemplate(ITemplate template) {
        templates.put(template.getUniqueName(), template);

        for (FeatureProvider provider : template.getFeatures()) {
            provider.update(new Page(0));
        }
    }

    /**
     * Clear all templates. Should be called before reloading.
     */
    public void clear() {
        templates.clear();
        sorted.clear();
        position = 0;
    }

    public List<FeatureProvider> getFeaturesFromString(String unique) {
        return templates.get(unique).getFeatures();
    }

    private boolean isOverPosition(int x1, int y1, int x2, int y2, int mouseX, int mouseY) {
        if (mouseX >= EConfig.SETTINGS.editorXPos + x1 && mouseX <= EConfig.SETTINGS.editorXPos + x2) {
            return mouseY >= EConfig.SETTINGS.toolbarYPos.get() + y1 && mouseY <= EConfig.SETTINGS.toolbarYPos.get() + y2;
        }
        return false;
    }

    //CONVERT IN TO BUTTONS

    private static final int X_POS_START = 4;

    private void drawBoxLabel(net.minecraft.client.gui.GuiGraphics guiGraphics, String name, int yPos, GuiBook guiBook) {
        drawBorderedRectangle(guiGraphics, X_POS_START - 2, yPos, 83, yPos + 10, 0xFFB0A483, 0xFF48453C, guiBook);
        drawSplitScaledString(guiGraphics, "[b]" + name + "[/b]", X_POS_START, yPos + 3, 0xFF48453C, 0.5F, guiBook);
    }

    @Override
    public void draw(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, GuiBook guiBook) {
        int count = 0;
        int yPlus = 0;
        int xPlus = 0;
        for (ITemplate template : sorted) {
            if (count < position || count > position + 17) {
                count++;
                continue;
            }

            if (isOverPosition(2 + xPlus, 11 + yPlus, 42 + xPlus, 34 + yPlus, mouseX, mouseY)) {
                for (FeatureProvider provider : template.getFeatures()) {
                    provider.draw(mouseX, mouseY);
                }
            }

            drawImage(guiGraphics, template.getIcon(), 2 + xPlus, 11 + yPlus, 42 + xPlus, 34 + yPlus, guiBook);
            if (guiBook.getBook().getDefaultFeatures().contains(template.getUniqueName())) {
                drawBorderedRectangle(guiGraphics, 2 + xPlus, 11 + yPlus, 42 + xPlus, 34 + yPlus, 0x00000000, 0xFF8C0000, guiBook);
            }

            xPlus += 41;

            if (xPlus >= 42) {
                xPlus = 0;
                yPlus += 25;
            }

            count++;
        }
    }

    @Override
    public void scroll(boolean down, int mouseX, int mouseY) {
        if (mouseX >= EConfig.SETTINGS.editorXPos + 7 && mouseX <= EConfig.SETTINGS.editorXPos + 91) {
            if (mouseY >= EConfig.SETTINGS.toolbarYPos.get() + 14 && mouseY <= EConfig.SETTINGS.toolbarYPos.get() + 302) {
                if (down) {
                    position = Math.min(((sorted.size() + 1) / 2), position + 2);
                } else {
                    position = Math.max(0, position - 2);
                }
            }
        }
    }

    @Override
    public void addToolTip(List<String> tooltip, int mouseX, int mouseY) {
        int count = 0;
        int yPlus = 0;
        int xPlus = 0;
        for (ITemplate template : sorted) {
            if (count < position || count > position + 17) {
                count++;
                continue;
            }

            if (isOverPosition(2 + xPlus, 11 + yPlus, 42 + xPlus, 34 + yPlus, mouseX, mouseY)) {
                tooltip.add(template.getTemplateName());
                tooltip.add("");
                tooltip.add(Enchiridion.format("template.click"));
                return;
            }

            xPlus += 41;

            if (xPlus >= 42) {
                xPlus = 0;
                yPlus += 25;
            }

            count++;
        }
    }

    private GuiBook currentGuiBook; // Store reference for mouseClicked

    private void switchDefaulthood(ITemplate template, GuiBook guiBook) {
        HashSet<String> set = new HashSet<>(guiBook.getBook().getDefaultFeatures());
        if (set.contains(template.getUniqueName())) {
            set.remove(template.getUniqueName());
        } else set.add(template.getUniqueName());

        guiBook.getBook().setDefaultFeatures(set);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, GuiBook guiBook) {
        currentGuiBook = guiBook; // Store for other methods if needed
        int count = 0;
        int yPlus = 0;
        int xPlus = 0;
        for (ITemplate template : sorted) {
            for (FeatureProvider provider : template.getFeatures()) {
                provider.setFromTemplate(true);
            }
            if (count < position || count > position + 17) {
                count++;
                continue;
            }

            if (isOverPosition(2 + xPlus, 11 + yPlus, 42 + xPlus, 34 + yPlus, mouseX, mouseY)) {
                if (MCClientHelper.isShiftPressed()) {
                    switchDefaulthood(template, guiBook);
                } else {
                    for (FeatureProvider provider : template.getFeatures()) {
                        guiBook.getPage().addFeature(provider.getFeature(), provider.getLeft(), provider.getTop(), provider.getWidth(), provider.getHeight(), provider.isLocked(), !provider.isVisible(), provider.isFromTemplate());
                    }
                }
                return true;
            }
            xPlus += 41;

            if (xPlus >= 42) {
                xPlus = 0;
                yPlus += 25;
            }
            count++;
        }
        return false;
    }

    @Override
    public void updateSearch(String search) {
        sorted = new ArrayList<>();
        if (search == null || search.equals("")) {
            sorted.addAll(templates.values());
        } else {
            sorted.addAll(templates.values().stream().filter(template -> template.getTemplateName().toLowerCase().contains(search.toLowerCase())).collect(Collectors.toList()));
        }
    }
}