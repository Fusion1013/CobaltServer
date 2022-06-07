package se.fusion1013.plugin.cobaltserver.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.database.player.IPlayerDao;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltcore.database.system.SQLite;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.PlayerUtil;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;
import se.fusion1013.plugin.cobaltserver.CobaltServer;
import se.fusion1013.plugin.cobaltserver.database.DatabaseHook;
import se.fusion1013.plugin.cobaltserver.manager.GameManager;

import java.util.List;
import java.util.UUID;

public class GameCommand {

    // ----- REGISTER -----

    public static void register() {
        new CommandAPICommand("game")
                .withSubcommand(createTagCommand())
                .register();
    }

    // ----- CREATE TAG COMMAND -----

    private static CommandAPICommand createTagCommand() {
        return new CommandAPICommand("tag")
                .withSubcommand(createTagStatsCommand())
                .withSubcommand(createTagLeaderboardCommand());
    }

    private static CommandAPICommand createTagStatsCommand() {
        return new CommandAPICommand("stats")
                .withArguments(new PlayerArgument("player").replaceSuggestions(ArgumentSuggestions.strings(PlayerUtil::getPlayerArguments)))
                .executesPlayer(((sender, args) -> {
                    Player target = (Player)args[0];
                    int count = GameManager.getInstance().getTagCount(target);
                    LocaleManager locale = LocaleManager.getInstance();
                    StringPlaceholders placeholders = StringPlaceholders.builder()
                            .addPlaceholder("player", target.getName())
                            .addPlaceholder("count", count)
                            .build();

                    locale.sendMessage(CobaltServer.getInstance(), sender, "commands.tag.stats", placeholders);
                }));
    }

    private static CommandAPICommand createTagLeaderboardCommand() {
        return new CommandAPICommand("loserboard")
                .executesPlayer(((sender, args) -> {
                    GameManager.getInstance().storeTagStats();
                    List<UUID> leaderboard = DatabaseHook.getTagLeaderboard();

                    StringPlaceholders placeholders1 = StringPlaceholders.builder()
                            .addPlaceholder("header", "Tag Loserboard")
                            .build();
                    LocaleManager locale = LocaleManager.getInstance();
                    locale.sendMessage(CobaltServer.getInstance(), sender, "list-header", placeholders1);

                    for (int i = 0; i < Math.min(10, leaderboard.size()); i++) {
                        String playerName = DataManager.getInstance().getDao(IPlayerDao.class).getPlayerName(leaderboard.get(i));
                        if (!playerName.equalsIgnoreCase("")) {
                            StringPlaceholders placeholders2 = StringPlaceholders.builder()
                                    .addPlaceholder("player", playerName)
                                    .addPlaceholder("id", i+1)
                                    .addPlaceholder("count", GameManager.getInstance().getTagCount(leaderboard.get(i)))
                                    .build();
                            locale.sendMessage("", sender, "games.tag.loserboard", placeholders2);
                        }
                    }
                }));
    }

}
