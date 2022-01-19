package se.fusion1013.plugin.cobaltserver.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import org.bukkit.Bukkit;
import se.fusion1013.plugin.cobaltcore.manager.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.HexUtils;

public class BroadcastCommand {
    public static void register(){
        createBroadcastCommand().register();
    }

    private static CommandAPICommand createBroadcastCommand(){
        return new CommandAPICommand("broadcast")
                .withPermission("cobalt.server.commands.broadcast")
                .withArguments(new GreedyStringArgument("message"))
                .executesPlayer((sender, args) -> {
                    Bukkit.broadcastMessage(LocaleManager.getInstance().getLocaleMessage("commands.broadcast.prefix") + HexUtils.colorify((String)args[0]));
                });
    }
}
