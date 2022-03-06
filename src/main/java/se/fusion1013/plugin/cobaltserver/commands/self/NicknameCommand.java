package se.fusion1013.plugin.cobaltserver.commands.self;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import se.fusion1013.plugin.cobaltcore.manager.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;
import se.fusion1013.plugin.cobaltserver.CobaltServer;
import se.fusion1013.plugin.cobaltserver.manager.ChatManager;

public class NicknameCommand {

    // ----- REGISTER -----

    public static void register() {

        // Main Nickname Command
        new CommandAPICommand("nickname")
                .withAliases("nick")
                .withPermission("commands.server.nickname")
                .withArguments(new GreedyStringArgument("nickname"))
                .executesPlayer(((sender, args) -> {
                    ChatManager.getInstance().setNickname(sender, (String)args[0]);
                    StringPlaceholders placeholders = StringPlaceholders.builder()
                            .addPlaceholder("player", sender.getName())
                            .addPlaceholder("to_name", (String)args[0])
                            .build();
                    LocaleManager.getInstance().sendMessage(CobaltServer.getInstance(), sender, "commands.nickname.changed", placeholders);
                })).register();

        // Nickname Reset Command
        new CommandAPICommand("nickname")
                .withAliases("nick")
                .withPermission("commands.server.nickname")
                .executesPlayer(((sender, args) -> {
                    ChatManager.getInstance().resetNickname(sender);
                    StringPlaceholders placeholders = StringPlaceholders.builder()
                            .addPlaceholder("player", sender.getName())
                            .build();
                    LocaleManager.getInstance().sendMessage(CobaltServer.getInstance(), sender, "commands.nickname.reset", placeholders);
                })).register();
    }
}
