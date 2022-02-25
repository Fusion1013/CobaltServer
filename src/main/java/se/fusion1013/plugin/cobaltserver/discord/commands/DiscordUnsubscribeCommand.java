package se.fusion1013.plugin.cobaltserver.discord.commands;

import se.fusion1013.plugin.cobaltserver.discord.system.DiscordCommand;
import se.fusion1013.plugin.cobaltserver.discord.system.DiscordStringArgument;
import se.fusion1013.plugin.cobaltserver.manager.DiscordManager;

public class DiscordUnsubscribeCommand {

    // ----- REGISTER -----

    public static void register() {
        new DiscordCommand("unsubscribe")
                .withHelp("!unsubscribe [global|status|awards|deaths|join|leave|playerlist|all]")
                .withArgument(new DiscordStringArgument("subscription").setLimitedOptions("global", "status", "awards", "deaths", "join", "leave", "playerlist"))
                .executes(((author, channel, args) -> {
                    String option = (String)args[0];

                    if (option.equalsIgnoreCase("all")) {
                        for (DiscordManager.ChannelOption o : DiscordManager.ChannelOption.values()) {
                            DiscordManager.getInstance().removeChannel(channel, o);
                        }
                        channel.sendMessage("Unsubscribed channel from all subscriptions").queue();
                        return;
                    }

                    if (DiscordManager.getInstance().removeChannel(channel, DiscordManager.ChannelOption.valueOf(option.toUpperCase()))) {
                        channel.sendMessage("Unsubscribed channel from subscription " + option).queue();
                    } else {
                        channel.sendMessage("Channel is not subscribed to subscription " + option).queue();
                    }
                }))
                .register();
    }

}
