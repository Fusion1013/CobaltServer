package se.fusion1013.plugin.cobaltserver.commands;

import dev.jorel.commandapi.CommandAPICommand;
import se.fusion1013.plugin.cobaltcore.manager.LocaleManager;
import se.fusion1013.plugin.cobaltserver.CobaltServer;

public class ColorCommand {
    public static void register(){
        createColorsCommand().register();
    }

    private static CommandAPICommand createColorsCommand(){
        return new CommandAPICommand("colors")
                .executesPlayer((sender, args) -> {
                    LocaleManager localeManager = LocaleManager.getInstance();

                    localeManager.sendMessage(CobaltServer.getInstance(), sender, "commands.colors.header");
                    localeManager.sendMessage("", sender, "commands.colors.color_codes_description");
                    localeManager.sendMessage("", sender, "commands.colors.color_codes");
                    localeManager.sendMessage("", sender, "commands.colors.formatting_codes");
                });
    }
}
