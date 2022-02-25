package se.fusion1013.plugin.cobaltserver.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.manager.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;
import se.fusion1013.plugin.cobaltcore.util.StringUtil;
import se.fusion1013.plugin.cobaltserver.CobaltServer;
import se.fusion1013.plugin.cobaltserver.database.DatabaseHook;
import se.fusion1013.plugin.cobaltserver.warp.Warp;

import java.util.List;
import java.util.UUID;

public class WarpCommand {
    public static void register(){
        CommandAPICommand warpListCommand = new CommandAPICommand("list")
                .withPermission("cobalt.server.command.warp.list")
                .executesPlayer(WarpCommand::warpList);
        warpListCommand.register();

        CommandAPICommand warpDeleteCommand = new CommandAPICommand("delete")
                .withPermission("cobalt.server.command.warp.delete")
                .withArguments(new StringArgument("warp name").replaceSuggestions(info -> getWarpNames()))
                .executesPlayer(WarpCommand::warpDelete);
        warpDeleteCommand.register();

        CommandAPICommand warpInfoCommand = new CommandAPICommand("info")
                .withPermission("cobalt.server.command.warp.info")
                .withArguments(new StringArgument("warp name").replaceSuggestions(info -> getWarpNames()))
                .executesPlayer(WarpCommand::warpInfo);
        warpInfoCommand.register();

        CommandAPICommand warpCreateCommand = new CommandAPICommand("create")
                .withPermission("cobalt.server.command.warp.create")
                .withArguments(new StringArgument("warp name"))
                .executesPlayer(WarpCommand::warpCreate);
        warpCreateCommand.register();

        new CommandAPICommand("warp")
                .withPermission("cobalt.server.command.warp")
                .withSubcommand(warpListCommand)
                .withSubcommand(warpDeleteCommand)
                .withSubcommand(warpInfoCommand)
                .withSubcommand(warpCreateCommand)
                .withArguments(new StringArgument("warp name").replaceSuggestions(info -> getWarpNames()))
                .executesPlayer(WarpCommand::warpTp)
                .register();
    }

    /**
     * Teleports the player to the warp
     *
     * @param player the player to teleport
     * @param args the warp to teleport the player to
     */
    private static void warpTp(Player player, Object[] args){
        LocaleManager localeManager = LocaleManager.getInstance();
        UUID pID = player.getUniqueId();

        String name = (String)args[0];
        List<Warp> warps = DatabaseHook.getWarpsByName(name);

        if (warps == null) return;

        StringPlaceholders namePlaceholder = StringPlaceholders.builder()
                .addPlaceholder("name", name).build();

        if (warps.size() == 0){
            localeManager.sendMessage(CobaltServer.getInstance(), player, "commands.warp.error.warp_not_found", namePlaceholder);
            return;
        }

        Warp highestPriorityWarp = warps.get(0);

        localeManager.sendMessage(CobaltServer.getInstance(), player, "commands.warp.teleport.success", namePlaceholder);
        player.teleport(highestPriorityWarp.getLocation());
    }

    /**
     * Creates a new warp
     *
     * @param player the player that is creating the warp
     * @param args the name of the warp
     */
    private static void warpCreate(Player player, Object[] args){
        LocaleManager localeManager = LocaleManager.getInstance();
        List<Warp> currentWarps = DatabaseHook.getWarps();

        String name = (String)args[0];
        StringPlaceholders namePlaceholder = StringPlaceholders.builder()
                .addPlaceholder("name", name).build();

        // Check if the name is an alphanumerical word
        if (!StringUtil.isWord(name)){
            localeManager.sendMessage(CobaltServer.getInstance(), player, "commands.warp.create.error.invalid_name", namePlaceholder);
            return;
        }

        // Check if warp with the same name already exists
        for (Warp warp : currentWarps){
            if (warp.getName().equalsIgnoreCase(name)){
                localeManager.sendMessage(CobaltServer.getInstance(), player, "commands.warp.create.error.name_already_exists", namePlaceholder);
                return;
            }
        }

        // Create the warp and store it in the database
        Warp warp = new Warp(name, player.getUniqueId(), player.getLocation());
        DatabaseHook.insertWarp(warp);

        localeManager.sendMessage(CobaltServer.getInstance(), player, "commands.warp.create.info.created_warp", namePlaceholder);
    }

    /**
     * Displays info about a warp
     *
     * @param player the player to display the info to
     * @param args the warp to display the info of
     */
    private static void warpInfo(Player player, Object[] args){
        LocaleManager localeManager = LocaleManager.getInstance();
        UUID pID = player.getUniqueId();

        String name = (String)args[0];
        List<Warp> warps = DatabaseHook.getWarpsByName(name);

        if (warps == null) return;

        StringPlaceholders namePlaceholder = StringPlaceholders.builder()
                .addPlaceholder("name", name).build();

        if (warps.size() == 0){
            localeManager.sendMessage(CobaltServer.getInstance(), player, "commands.warp.error.warp_not_found", namePlaceholder);
            return;
        }

        Warp highestPriorityWarp = warps.get(0);
        if (warps.size() == 1 || highestPriorityWarp.getOwner().equals(pID)){
            localeManager.sendMessage(CobaltServer.getInstance(), player, "commands.warp.info.header", namePlaceholder);

            Location loc = highestPriorityWarp.getLocation();

            // Send publicly available details
            StringPlaceholders pOwner = StringPlaceholders.builder()
                    .addPlaceholder("owner", Bukkit.getPlayer(highestPriorityWarp.getOwner()).getDisplayName())
                    .build();
            localeManager.sendMessage("", player, "commands.warp.info.detail.owner", pOwner);
            StringPlaceholders pWorld = StringPlaceholders.builder()
                    .addPlaceholder("world", loc.getWorld().getName())
                    .build();
            localeManager.sendMessage("", player, "commands.warp.info.detail.world", pWorld);
            StringPlaceholders pLocation = StringPlaceholders.builder()
                    .addPlaceholder("location", highestPriorityWarp.getLocation())
                    .build();
            localeManager.sendMessage("", player, "commands.warp.info.detail.location", pLocation);
            StringPlaceholders pDistance = StringPlaceholders.builder()
                    .addPlaceholder("distance", (double)Math.round(player.getLocation().distance(loc)*100)/100)
                    .build();
            localeManager.sendMessage("", player, "commands.warp.info.detail.distance", pDistance);
            StringPlaceholders pPrivacy = StringPlaceholders.builder()
                    .addPlaceholder("privacy", highestPriorityWarp.getPrivacyLevel().name())
                    .build();
            localeManager.sendMessage("", player, "commands.warp.info.detail.privacy", pPrivacy);
        }
    }

    /**
     * Returns all warp names
     *
     * @return a list of warp names
     */
    private static String[] getWarpNames(){
        List<Warp> warps = DatabaseHook.getWarps();
        String[] warpNames = new String[warps.size()];
        for (int i = 0; i < warps.size(); i++){
            warpNames[i] = warps.get(i).getName();
        }
        return warpNames;
    }

    /**
     * Deletes a warp
     *
     * @param player the player that is deleting the warp
     * @param args the warp to be deleted
     */
    private static void warpDelete(Player player, Object[] args){
        LocaleManager localeManager = LocaleManager.getInstance();

        String name = (String)args[0];
        int deletedWarps = DatabaseHook.deleteWarp(name);
        StringPlaceholders namePlaceholder = StringPlaceholders.builder()
                .addPlaceholder("name", name)
                .addPlaceholder("count", deletedWarps)
                .build();

        if (deletedWarps > 0){
            localeManager.sendMessage(CobaltServer.getInstance(), player, "commands.warp.delete.deleted_warps", namePlaceholder);
        } else {
            localeManager.sendMessage(CobaltServer.getInstance(), player, "commands.warp.error.warp_not_found", namePlaceholder);
        }
    }

    /**
     * Displays all warps to the player
     *
     * @param player player to display the warps to
     * @param args this will always be empty, but has to be here for call to work
     */
    private static void warpList(Player player, Object[] args){
        LocaleManager localeManager = LocaleManager.getInstance();

        List<Warp> warps = DatabaseHook.getWarps();
        if (warps == null) return;

        localeManager.sendMessage(CobaltServer.getInstance(), player, "commands.warp.list.header");
        for (Warp w : warps){
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("name", w.getName())
                    .addPlaceholder("location", w.getLocation())
                    .addPlaceholder("world", w.getLocation().getWorld().getName())
                    .build();
            localeManager.sendMessage("", player, "commands.warp.list.entry", placeholders);
        }
    }
}
