package joshie.enchiridion.library;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import joshie.enchiridion.network.PacketHandler;
import joshie.enchiridion.network.packet.PacketLibraryCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class LibraryCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) Commands.literal("enchiridion").requires((command) -> command.hasPermission(0)))
                // TODO: refresh command - no matching Action enum
                // .then(Commands.literal("refresh").executes((command) -> refresh()))
                .then(Commands.literal("resources").executes((command) -> resources()))
                .then(Commands.literal("reset").executes((command) -> reset()))
                .then(Commands.literal("clear").executes((command) -> clear())));
    }

    // TODO: No Action enum for refresh
    /*
    private static int refresh() {
        PacketHandler.sendToServer(new PacketLibraryCommand(PacketLibraryCommand.Action.???));
        return 0;
    }
    */

    private static int resources() {
        Minecraft.getInstance().getResourcePackRepository().reload();
        return 0;
    }

    private static int reset() {
        PacketHandler.sendToServer(new PacketLibraryCommand(PacketLibraryCommand.Action.RESET_INVENTORY));
        return 0;
    }

    private static int clear() {
        PacketHandler.sendToServer(new PacketLibraryCommand(PacketLibraryCommand.Action.CLEAR_INVENTORY));
        return 0;
    }
}