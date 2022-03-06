package se.fusion1013.plugin.cobaltserver.commands.self;

import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import se.fusion1013.plugin.cobaltcore.manager.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;
import se.fusion1013.plugin.cobaltserver.CobaltServer;

public class HatCommand {

    // ----- REGISTER -----

    public static void register() {
        new CommandAPICommand("hat")
                .withAliases("addhat","wear")
                .executesPlayer(HatCommand::executeHatCommand)
                .register();
    }

    // ----- HAT COMMAND -----

    private static void executeHatCommand(Player sender, Object[] args) {
        PlayerInventory inventory = sender.getInventory();
        ItemStack heldItem = inventory.getItemInMainHand();
        ItemStack helmet = inventory.getHelmet();
        if (helmet == null) helmet = new ItemStack(Material.AIR, 1);

        LocaleManager localeManager = LocaleManager.getInstance();
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("new_hat", heldItem.getType())
                .addPlaceholder("old_hat", helmet.getType())
                .build();

        if (heldItem.getType().equals(Material.AIR)) {
            inventory.setHelmet(null);
            inventory.setItemInMainHand(helmet);
            localeManager.sendMessage(CobaltServer.getInstance(), sender, "commands.hat.retrieve", placeholders);

        } else if (heldItem.getType().equals(helmet.getType()) && heldItem.getItemMeta().equals(helmet.getItemMeta())) {
            // Give a special message if the item they're holding is the same as their existing hat
            localeManager.sendMessage(CobaltServer.getInstance(), sender, "commands.hat.already_wearing", placeholders);
        } else {
            // Replace their hat with the currently held item
            // Create the new hat
            ItemStack newHat = heldItem.clone();
            newHat.setAmount(1);
            heldItem.setAmount(heldItem.getAmount() - 1);
            inventory.setHelmet(newHat);

            // Give back the player's old hat
            inventory.addItem(helmet);

            // Give feedback
            localeManager.sendMessage(CobaltServer.getInstance(), sender, "commands.hat.new", placeholders);
        }
    }
}
