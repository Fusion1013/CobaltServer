package se.fusion1013.plugin.cobaltserver.commands.info;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.util.PlayerUtil;

public class EnderchestCommand {

    // ----- REGISTER -----

    public static void register() {
        createEnderchestCommand().register();
        createEnderchestSelfCommand().register();
    }

    // ----- COMMAND CREATION -----

    private static CommandAPICommand createEnderchestCommand() {
        return new CommandAPICommand("enderchest")
                .withPermission("commands.server.enderchest")
                .withArguments(new PlayerArgument("target").replaceSuggestions(ArgumentSuggestions.strings(PlayerUtil::getPlayerArguments)))
                .executesPlayer(((sender, args) -> {
                    Player target = (Player) args[0];
                    sender.openInventory(target.getEnderChest());
                }));
    }

    private static CommandAPICommand createEnderchestSelfCommand() {
        return new CommandAPICommand("enderchest")
                .withPermission("commands.server.enderchest")
                .executesPlayer(((sender, args) -> {
                    sender.openInventory(sender.getEnderChest());
                }));
    }

}
