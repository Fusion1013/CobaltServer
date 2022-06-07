package se.fusion1013.plugin.cobaltserver.commands.info;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.util.PlayerUtil;

public class InvseeCommand {

    // ----- REGISTER -----

    public static void register() {
        createInvseeCommand().register();
    }

    // ----- INVSEE COMMAND -----

    private static CommandAPICommand createInvseeCommand() {
        return new CommandAPICommand("invsee")
                .withPermission("commands.server.invsee")
                .withArguments(new PlayerArgument("target").replaceSuggestions(ArgumentSuggestions.strings(PlayerUtil::getPlayerArguments)))
                .executesPlayer(InvseeCommand::executeInvseeCommand);
    }

    private static void executeInvseeCommand(Player player, Object[] args) {
        Player target = (Player)args[0];
        player.openInventory(target.getInventory());
    }

}
