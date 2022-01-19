package se.fusion1013.plugin.cobaltserver.commands;

import dev.jorel.commandapi.CommandAPICommand;
import se.fusion1013.plugin.cobaltcore.locale.Message;
import se.fusion1013.plugin.cobaltcore.manager.LocaleManager;

public class ColorCommand {
    public static void register(){
        createColorsCommand().register();
    }

    private static CommandAPICommand createColorsCommand(){
        return new CommandAPICommand("colors")
                .executesPlayer((sender, args) -> {
                    LocaleManager localeManager = LocaleManager.getInstance();

                    localeManager.sendMessage(sender, new Message("commands.colors.header").setPrefix("cobalt.server"));
                    localeManager.sendMessage(sender, new Message("commands.colors.color_codes_description").setPrefix(""));
                    localeManager.sendMessage(sender, new Message("commands.colors.color_codes").setPrefix(""));
                    localeManager.sendMessage(sender, new Message("commands.colors.formatting_codes").setPrefix(""));
                });
    }
}
