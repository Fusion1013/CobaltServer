package se.fusion1013.plugin.cobaltserver.commands.info;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.PlayerUtil;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;
import se.fusion1013.plugin.cobaltserver.CobaltServer;
import se.fusion1013.plugin.cobaltserver.database.DatabaseHook;

/**
 * The find command allows players to find the location, world and gamemode of other players.
 * If the player is offline, it will provide that players last known location.
 */
public class FindCommand {

    // ----- REGISTER -----

    /**
     * Registers the find command.
     */
    public static void register() {
        new CommandAPICommand("find")
                .withPermission("cobalt.commands.find")
                .withArguments(new StringArgument("player").replaceSuggestions(ArgumentSuggestions.strings(info -> PlayerUtil.getOnlinePlayerNames())))
                .executesPlayer(FindCommand::findCommandExecutor)
                .register();
    }

    // ----- COMMAND EXECUTOR METHODS -----

    /**
     * Prints information about a players last location to the executor. If the player is offline, it will print the players last known location.
     * @param sender the command sender.
     * @param args the command arguments.
     */
    private static void findCommandExecutor(Player sender, Object[] args) {

        LocaleManager localeManager = LocaleManager.getInstance();

        // Tries to get the player from the currently online players
        String playerName = (String)args[0];
        Player player = Bukkit.getPlayer(playerName);

        boolean online = true;

        // Information variables
        World world;
        GameMode gameMode;
        Location playerLocation;

        // If the player is not online, get the information from the database.
        if (player == null) {
            online = false;
            PlayerUtil.PlayerStorage storage = DatabaseHook.getPlayerStorage(playerName);

            // If the requested player was not found in database, return an error message
            if (storage == null) {
                StringPlaceholders placeholders = StringPlaceholders.builder()
                        .addPlaceholder("player", playerName)
                        .build();
                localeManager.sendMessage(CobaltServer.getInstance(), sender, "commands.player_not_found", placeholders);
                return;
            }

            // Set variables
            world = storage.world;
            playerLocation = storage.location;
        } else {
            // If player is online, get information directly from them
            world = player.getWorld();
            gameMode = player.getGameMode(); // TODO:
            playerLocation = player.getLocation();
        }

        // Create information placeholder
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("player", playerName)
                .addPlaceholder("world", world.getName())
                .addPlaceholder("gamemode", "TODO") // TODO
                .addPlaceholder("location", playerLocation)
                .build();

        // Check if they are in the same world, if they are get distance and add that to placeholder
        // Send message to player
        if (world.getName().equalsIgnoreCase(sender.getWorld().getName())) {
            placeholders.addPlaceholder("distance", Math.round(playerLocation.distance(sender.getLocation()) * 10) / 10.0);

            if (online) localeManager.sendMessage(CobaltServer.getInstance(), sender, "commands.find.result.online.same_world", placeholders);
            else localeManager.sendMessage(CobaltServer.getInstance(), sender, "commands.find.result.offline.same_world", placeholders);
        } else {
            if (online) localeManager.sendMessage(CobaltServer.getInstance(), sender, "commands.find.result.online.different_world", placeholders);
            else localeManager.sendMessage(CobaltServer.getInstance(), sender, "commands.find.result.offline.different_world", placeholders);
        }
    }
}
