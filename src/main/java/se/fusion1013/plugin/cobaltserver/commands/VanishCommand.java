package se.fusion1013.plugin.cobaltserver.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.plugin.cobaltcore.util.PlayerUtil;

public class VanishCommand implements CommandExecutor {

    // ----- REGISTER -----

    public static void register() {
        CommandAPI.unregister("vanish", true);

        // Vanish command without player specifier
        new CommandAPICommand("vanish")
                .withPermission("cobalt.server.commands.vanish")
                .withAliases("v")
                .executesPlayer(((sender, args) -> {
                    executeToggleVanishCommand(sender);
                }))
                .register();

        new CommandAPICommand("vanish")
                .withPermission("cobalt.server.commands.vanish")
                .withAliases("v")
                .withArguments(new PlayerArgument("player"))
                .executes(((sender, args) -> {
                    executeToggleVanishCommand((Player)args[0]);
                }))
                .register();
    }

    // ----- VANISH EXECUTOR -----

    private static void executeToggleVanishCommand(Player player) {
        // TODO: Silent, fake messages

        // Set vanished state
        boolean newVanishedState = !PlayerUtil.isVanished(player); // True if the new state is vanished state
        PlayerUtil.setVanished(player, newVanishedState, false);

        // Join / Quit Message
        if (newVanishedState) PlayerUtil.sendQuitMessage(player);
        else PlayerUtil.sendJoinMessage(player);
    }


    // TODO: Remove this, this is stupid

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player p) {
            executeToggleVanishCommand(p);
        }

        return true;
    }
}
