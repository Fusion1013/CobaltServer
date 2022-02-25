package se.fusion1013.plugin.cobaltserver.discord.commands;

import se.fusion1013.plugin.cobaltserver.discord.system.DiscordCommand;
import se.fusion1013.plugin.cobaltserver.discord.system.DiscordStringArgument;
import se.fusion1013.plugin.cobaltserver.manager.DiscordManager;

public class DiscordSubscribeCommand {

    // ----- REGISTER -----

    public static void register() {
        new DiscordCommand("subscribe")
                .withHelp("!subscribe [global|status|awards|deaths|join|leave|playerlist]")
                .withArgument(new DiscordStringArgument("subscription").setLimitedOptions("global", "status", "awards", "deaths", "join", "leave", "playerlist"))
                .executes(((author, channel, args) -> {
                    String option = (String)args[0];

                    boolean added = DiscordManager.getInstance().addChannel(channel, DiscordManager.ChannelOption.valueOf(option.toUpperCase()));
                    if (added) channel.sendMessage("Set this channel to receive " + option + " messages").queue();
                    else channel.sendMessage("This channel is already set to receive " + option + " messages").queue();
                }))
                .register();
    }

}
