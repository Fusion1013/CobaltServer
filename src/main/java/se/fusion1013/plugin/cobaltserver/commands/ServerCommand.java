package se.fusion1013.plugin.cobaltserver.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import se.fusion1013.plugin.cobaltcore.util.VersionUtil;
import se.fusion1013.plugin.cobaltserver.CobaltServer;
import se.fusion1013.plugin.cobaltserver.manager.ResourcePackManager;

public class ServerCommand {

    // ----- REGISTRATION -----

    public static void register() {
        new CommandAPICommand("server")
                .withSubcommand(createVersionCommand())
                .withSubcommand(createResourcePackCommand())
                .register();
    }

    // ----- RESOURCE PACK COMMAND -----

    /**
     * Creates the resource pack command.
     * This command changes the server resource pack.
     *
     * @return the resource pack command.
     */
    private static CommandAPICommand createResourcePackCommand() {
        return new CommandAPICommand("resource_pack")
                .withPermission("commands.server.resource_pack")
                .withArguments(new GreedyStringArgument("url"))
                .executes(((sender, args) -> {
                    ResourcePackManager.getInstance().setResourcePackString((String)args[0]);
                }));
    }

    // ----- VERSION COMMAND -----

    /**
     * Creates the version command.
     * This command prints the plugin version and github issue link.
     *
     * @return the version command.
     */
    private static CommandAPICommand createVersionCommand() {
        return new CommandAPICommand("version")
                .executesPlayer(((sender, args) -> {
                    VersionUtil.printVersion(CobaltServer.getInstance(), sender);
                }));
    }
}
