package joshie.enchiridion.gui.book.buttons.actions;

import joshie.enchiridion.api.book.IButtonAction;
import joshie.enchiridion.gui.book.GuiBook;

import java.awt.*;
import java.net.URI;

public class ActionOpenWebpage extends AbstractAction {
    public String url;

    public ActionOpenWebpage() {
        super("web");
        this.url = "http://www.joshiejack.uk/";
    }

    @Override
    public IButtonAction copy() {
        ActionOpenWebpage action = new ActionOpenWebpage();
        action.url = url;
        copyAbstract(action);
        return action;
    }

    @Override
    public IButtonAction create(GuiBook guiBook) {
        return new ActionOpenWebpage();
    }

    @Override
    public boolean performAction(GuiBook guiBook) {
        try {
            Desktop.getDesktop().browse(new URI(url));
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }
}