package se.fusion1013.plugin.cobaltserver.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import se.fusion1013.plugin.cobaltserver.manager.ChatManager;

public class NicknameCommand {

    // ----- REGISTER -----

    public static void register() {
        new CommandAPICommand("nickname")
                .withAliases("nick")
                .withPermission("commands.server.nickname")
                .withArguments(new GreedyStringArgument("nickname"))
                .executesPlayer(((sender, args) -> {
                    ChatManager.getInstance().setNickname(sender, (String)args[0]);
                })).register();
    }
}
