package joshie.enchiridion.gui.book.buttons.actions;

import joshie.enchiridion.api.book.IButtonAction;
import joshie.enchiridion.gui.book.GuiBook;
import net.minecraft.client.Minecraft;

public class ActionExecuteCommand extends AbstractAction {
    public String command;
    public boolean close;

    public ActionExecuteCommand() {
        super("command");
        this.command = "/say hello";
        this.close = false;
    }

    @Override
    public IButtonAction copy() {
        ActionExecuteCommand action = new ActionExecuteCommand();
        action.command = command;
        action.close = close;
        copyAbstract(action);
        return action;
    }

    @Override
    public IButtonAction create(GuiBook guiBook) {
        return new ActionExecuteCommand();
    }

    @Override
    public boolean performAction(GuiBook guiBook) {
        Minecraft mc = Minecraft.getInstance();
        try {
            String parsedCommand = command.replace("@p", mc.player.getName().getString());
            // In 1.20.4, sendChatMessage() was replaced with connection.sendCommand() for commands
            if (parsedCommand.startsWith("/")) {
                mc.player.connection.sendCommand(parsedCommand.substring(1));
            } else {
                mc.player.connection.sendCommand(parsedCommand);
            }
        } catch (Exception ignored) {
        }

        if (close) {
            mc.setScreen(null);
        }
        return true;
    }
}