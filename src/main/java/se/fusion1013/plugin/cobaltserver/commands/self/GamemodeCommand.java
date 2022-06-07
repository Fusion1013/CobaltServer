package se.fusion1013.plugin.cobaltserver.commands.self;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.PlayerUtil;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;
import se.fusion1013.plugin.cobaltserver.CobaltServer;

public class GamemodeCommand {

    // ----- REGISTER -----

    public static void register(){
        CommandAPI.unregister("gamemode", true);
        createGamemodeOtherCommand().register();
        createGamemodeCommand().register();
    }

    // ----- GAMEMODE COMMAND -----

    private static CommandAPICommand createGamemodeOtherCommand() {
        return new CommandAPICommand("gamemode")
                .withPermission("cobalt.server.commands.gamemode")
                .withAliases("gm")
                .withArguments(new StringArgument("gamemode").replaceSuggestions(ArgumentSuggestions.strings(info -> new String[]{"survival","creative","adventure","spectator"})))
                .withArguments(new PlayerArgument("target").replaceSuggestions(ArgumentSuggestions.strings(PlayerUtil::getPlayerArguments)))
                .executesPlayer(((sender, args) -> {
                    GameMode gameMode = getGamemode((String)args[0]);

                    Player other = (Player)args[1];

                    LocaleManager localeManager = LocaleManager.getInstance();
                    StringPlaceholders placeholders = StringPlaceholders.builder()
                            .addPlaceholder("player_name", other.getName())
                            .addPlaceholder("gamemode_raw", args[0])
                            .build();

                    if (gameMode != null) {
                        placeholders.addPlaceholder("gamemode", gameMode.name().toLowerCase());
                        other.setGameMode(gameMode);
                        localeManager.sendMessage(CobaltServer.getInstance(), sender, "commands.gamemode.change", placeholders);
                    } else {
                        localeManager.sendMessage(CobaltServer.getInstance(), sender, "commands.gamemode.error.gamemode_not_found", placeholders);
                    }
                }));
    }

    private static CommandAPICommand createGamemodeCommand() {
        return new CommandAPICommand("gamemode")
                .withPermission("cobalt.server.commands.gamemode")
                .withAliases("gm")
                .withArguments(new StringArgument("gamemode").replaceSuggestions(ArgumentSuggestions.strings(info -> new String[]{"survival","creative","adventure","spectator"})))
                .executesPlayer(((sender, args) -> {
                    GameMode gameMode = getGamemode((String)args[0]);

                    LocaleManager localeManager = LocaleManager.getInstance();
                    StringPlaceholders placeholders = StringPlaceholders.builder()
                            .addPlaceholder("player_name", sender.getName())
                            .addPlaceholder("gamemode_raw", args[0])
                            .build();

                    if (gameMode != null) {
                        sender.setGameMode(gameMode);
                        placeholders.addPlaceholder("gamemode", gameMode.name().toLowerCase());
                        localeManager.sendMessage(CobaltServer.getInstance(), sender, "commands.gamemode.change", placeholders);
                    } else {
                        localeManager.sendMessage(CobaltServer.getInstance(), sender, "commands.gamemode.error.gamemode_not_found", placeholders);
                    }
                }));
    }

    private static GameMode getGamemode(String s){
        if (GameMode.SURVIVAL.toString().startsWith(s.toUpperCase()) || s.equalsIgnoreCase("0")) return GameMode.SURVIVAL;
        if (GameMode.CREATIVE.toString().startsWith(s.toUpperCase()) || s.equalsIgnoreCase("1")) return GameMode.CREATIVE;
        if (GameMode.SPECTATOR.toString().startsWith(s.toUpperCase()) || s.equalsIgnoreCase("3")) return GameMode.SPECTATOR;
        if (GameMode.ADVENTURE.toString().startsWith(s.toUpperCase()) || s.equalsIgnoreCase("2")) return GameMode.ADVENTURE;
        return null;
    }
}
