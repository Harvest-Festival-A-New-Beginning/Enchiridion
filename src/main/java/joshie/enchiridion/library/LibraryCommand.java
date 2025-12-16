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
                .then(Commands.literal("refresh").executes((command) -> refresh()))
                .then(Commands.literal("resources").executes((command) -> resources()))
                .then(Commands.literal("reset").executes((command) -> reset()))
                .then(Commands.literal("clear").executes((command) -> clear())));
    }

    private static int refresh() {
        PacketHandler.sendToServer(new PacketLibraryCommand("refresh"));
        return 0;
    }

    private static int resources() {
        Minecraft.getInstance().getResourcePackRepository().reload();
        return 0;
    }

    private static int reset() {
        PacketHandler.sendToServer(new PacketLibraryCommand("reset"));
        return 0;
    }

    private static int clear() {
        PacketHandler.sendToServer(new PacketLibraryCommand("clear"));
        return 0;
    }
}