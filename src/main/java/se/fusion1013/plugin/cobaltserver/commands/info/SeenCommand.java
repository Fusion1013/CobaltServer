package se.fusion1013.plugin.cobaltserver.commands.info;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.PlayerUtil;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;
import se.fusion1013.plugin.cobaltcore.util.TimeUtil;
import se.fusion1013.plugin.cobaltserver.CobaltServer;
import se.fusion1013.plugin.cobaltserver.database.DatabaseHook;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SeenCommand {

    // ----- REGISTER -----

    public static void register() {
        new CommandAPICommand("seen")
                .withPermission("cobalt.server.commands.seen")
                .withArguments(new StringArgument("player").replaceSuggestions(ArgumentSuggestions.strings(info -> PlayerUtil.getOnlinePlayerNames())))
                .executesPlayer(SeenCommand::executeSeenCommand)
                .register();
    }

    // ----- COMMAND EXECUTORS -----

    /**
     * Executes the seen command.
     * Prints the given players last seen location and time.
     *
     * @param sender the player that is executing the command.
     * @param args the command arguments.
     */
    private static void executeSeenCommand(Player sender, Object[] args) {
        String s = (String)args[0];

        PlayerUtil.PlayerStorage storage = DatabaseHook.getPlayerStorage(s);

        // Check if player is online.
        boolean offline = Bukkit.getServer().getPlayer(s) == null;

        LocaleManager locale = LocaleManager.getInstance();
        // If player was not found, send error.
        if (storage == null) {
            locale.sendMessage(CobaltServer.getInstance(), sender, "commands.player_not_found", StringPlaceholders.builder().addPlaceholder("player", s).build());
            return;
        }

        // Get the time since the player logged on / logged off last time depending on their current online status.
        Map<TimeUnit, Long> diff;
        if (offline) diff = TimeUtil.computeDiff(storage.lastLeft, new Date());
        else diff = TimeUtil.computeDiff(storage.lastJoined, new Date());

        // Create placeholder containing player data.
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("player", storage.name)
                .addPlaceholder("time", diff)
                .build();

        // Send messages
        if (offline) locale.sendMessage(CobaltServer.getInstance(), sender, "commands.seen.result.offline", placeholders);
        else locale.sendMessage(CobaltServer.getInstance(), sender, "commands.seen.result.online", placeholders);

        // TODO: Send ip, vanished, also known as names
    }
}
