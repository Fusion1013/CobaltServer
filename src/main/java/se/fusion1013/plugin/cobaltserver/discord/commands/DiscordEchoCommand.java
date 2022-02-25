package se.fusion1013.plugin.cobaltserver.discord.commands;

import se.fusion1013.plugin.cobaltserver.discord.system.DiscordCommand;
import se.fusion1013.plugin.cobaltserver.discord.system.DiscordStringArgument;

public class DiscordEchoCommand {

    // ----- REGISTER -----

    public static void register() {

        new DiscordCommand("echo")
                .withArgument(new DiscordStringArgument("message"))
                .withHelp("!echo [message]")
                .executes(((author, channel, args) -> {
                    String msg = (String)args[0];
                    channel.sendMessage(msg).queue();
                }))
                .register();

    }
}
