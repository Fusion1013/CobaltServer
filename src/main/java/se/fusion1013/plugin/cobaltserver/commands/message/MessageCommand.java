package se.fusion1013.plugin.cobaltserver.commands.message;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.PlayerUtil;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;

import java.util.Collection;

public class MessageCommand {

    // ----- REGISTER -----

    public static void register() {
        CommandAPI.unregister("msg", true);
        CommandAPI.unregister("tell", true);
        CommandAPI.unregister("w", true);

        new CommandAPICommand("msg")
                .withAliases("tell", "w")
                .withArguments(new EntitySelectorArgument("targets", EntitySelectorArgument.EntitySelector.MANY_ENTITIES).replaceSuggestions(ArgumentSuggestions.strings(PlayerUtil::getPlayerArguments)))
                .withArguments(new GreedyStringArgument("message"))
                .executes(((sender, args) -> {
                    Collection<Entity> targets = (Collection<Entity>) args[0];
                    String message = (String) args[1];

                    for (Entity target : targets) {
                        // Create placeholder that contains entity info and message info
                        StringPlaceholders placeholders = StringPlaceholders.builder()
                                .addPlaceholder("sender", sender.getName())
                                .addPlaceholder("receiver", target.getName())
                                .addPlaceholder("message", message)
                                .build();

                        // Check if sender is a player
                        if (sender instanceof Player player) {

                            if (target instanceof Player targetPlayer) {

                                // If player is vanished and sender doesn't have permission to vanish, don't send the message and instead send an error message to the sender
                                if (PlayerUtil.isVanished(targetPlayer) && !sender.hasPermission("cobalt.server.commands.vanish")) {
                                    LocaleManager.getInstance().sendMessage("", player, "commands.error.player_does_not_exist");
                                } else {
                                    // Send success message to both the sender and the target
                                    LocaleManager.getInstance().sendMessage("", player, "commands.message.sender", placeholders);
                                    LocaleManager.getInstance().sendMessage("", targetPlayer, "commands.message.receiver", placeholders);
                                }
                            } else {
                                LocaleManager.getInstance().sendMessage("", player, "commands.message.sender", placeholders);
                            }
                        }
                    }
                })).register();
    }

}
