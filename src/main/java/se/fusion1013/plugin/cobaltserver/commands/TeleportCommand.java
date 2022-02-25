package se.fusion1013.plugin.cobaltserver.commands;

import dev.jorel.commandapi.CommandAPICommand;

public class TeleportCommand {

    // ----- REGISTER -----

    public static void register() { // TODO

        // Main Teleport Command
        new CommandAPICommand("teleport")
                .withPermission("cobalt.commands.teleport")
                .withAliases("tp")
                .register();
    }
}
