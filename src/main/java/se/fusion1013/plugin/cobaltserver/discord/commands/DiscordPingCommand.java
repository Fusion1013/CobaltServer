package se.fusion1013.plugin.cobaltserver.discord.commands;

import se.fusion1013.plugin.cobaltserver.discord.system.DiscordCommand;

public class DiscordPingCommand {

    // ----- REGISTER -----

    public static void register() {
        new DiscordCommand("ping")
                .withHelp("!ping")
                .executes(((author, channel, args) -> {
                    long time = System.currentTimeMillis();
                    channel.sendMessage("pong").queue(response /* => Message */ -> {
                        response.editMessageFormat("Pong: %d ms", System.currentTimeMillis() - time).queue();
                    });
                }))
                .register();
    }

}
