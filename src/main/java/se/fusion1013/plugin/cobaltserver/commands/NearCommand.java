package se.fusion1013.plugin.cobaltserver.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.DoubleArgument;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.manager.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.EntityUtil;
import se.fusion1013.plugin.cobaltcore.util.PlayerUtil;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;
import se.fusion1013.plugin.cobaltserver.CobaltServer;

public class NearCommand {

    // ----- REGISTER -----

    public static void register() {
        new CommandAPICommand("near")
                .withPermission("cobalt.commands.near")
                .executesPlayer(NearCommand::printNearbyPlayers)
                .register();
        new CommandAPICommand("near")
                .withPermission("cobalt.commands.near")
                .withArguments(new DoubleArgument("distance"))
                .executesPlayer(NearCommand::printNearbyPlayers)
                .register();
    }

    // ----- COMMAND CREATION -----

    /**
     * Prints a list of all players within the specified range (100 blocks by default).
     *
     * @param sender the player that sent the command.
     * @param args command arguments.
     */
    private static void printNearbyPlayers(Player sender, Object[] args) {
        // Get the distance to search for nearby players
        double distance = 100;
        if (args.length > 0) distance = (double)args[0];

        // Get player array
        Player[] players = PlayerUtil.getNearbyPlayers(sender.getLocation(), 0.001, distance);

        // Create player info string
        StringBuilder playerString = new StringBuilder();
        for (Player p : players) {
            double pDist = Math.round(p.getLocation().distance(sender.getLocation()) * 10) / 10.0;
            playerString.append("&3").append(p.getName()).append(" &7(&b").append(pDist).append("&7m), ");
        }
        playerString = new StringBuilder(playerString.substring(0, Math.max(playerString.length() - 2, 0)));

        // Create placeholder containing all info
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("player_count", players.length)
                .addPlaceholder("range", distance)
                .addPlaceholder("players", playerString)
                .build();

        // Send message, depending on number of players found
        if (players.length <= 0) LocaleManager.getInstance().sendMessage(CobaltServer.getInstance(), sender, "commands.near.fail", placeholders);
        else LocaleManager.getInstance().sendMessage(CobaltServer.getInstance(), sender, "commands.near.result", placeholders);
    }
}
