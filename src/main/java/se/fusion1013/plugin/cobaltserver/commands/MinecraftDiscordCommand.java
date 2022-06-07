package se.fusion1013.plugin.cobaltserver.commands;

import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltserver.CobaltServer;
import se.fusion1013.plugin.cobaltserver.manager.DiscordManager;

public class MinecraftDiscordCommand {

    // ----- REGISTER -----

    public static void register() {

        new CommandAPICommand("discord")
                .withPermission("commands.server.discord")
                .withSubcommand(createReloadCommand())
                .register();

    }

    // ----- RELOAD COMMAND -----

    private static CommandAPICommand createReloadCommand() {
        return new CommandAPICommand("reload")
                .withPermission("commands.server.discord.reload")
                .executes(((sender, args) -> {
                    DiscordManager.getInstance().reloadBot();
                    if (sender instanceof Player p) {
                        LocaleManager.getInstance().sendMessage(CobaltServer.getInstance(), p, "commands.discord.reload");
                    }
                }));
    }

}
