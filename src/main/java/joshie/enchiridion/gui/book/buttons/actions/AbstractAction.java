package joshie.enchiridion.gui.book.buttons.actions;

import joshie.enchiridion.Enchiridion;
import joshie.enchiridion.gui.book.buttons.IButtonAction;
import joshie.enchiridion.gui.book.GuiBook;
import joshie.enchiridion.util.ELocation;
import net.minecraft.resources.ResourceLocation;

import java.util.Comparator;

public abstract class AbstractAction implements IButtonAction {
    protected transient ResourceLocation resource;
    protected transient String name;

    public AbstractAction() {
    }

    public AbstractAction(String name) {
        this.name = name;
        this.resource = new ELocation(name);
    }

    public IButtonAction copyAbstract(AbstractAction action) {
        action.name = name;
        return this;
    }

    @Override
    public void onFieldsSet(String field) {
        if (field.equals("")) initAction();
    }

    public void initAction() {
    }

    @Override
    public String getName() {
        return Enchiridion.format("action." + name);
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public ResourceLocation getResource() {
        return resource;
    }

    protected static class SortNumerical implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            return ((Integer) o1).compareTo((Integer) o2);
        }
    }
}