package se.fusion1013.plugin.cobaltserver;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.CobaltPlugin;
import se.fusion1013.plugin.cobaltcore.manager.ConfigManager;
import se.fusion1013.plugin.cobaltcore.util.PlayerUtil;
import se.fusion1013.plugin.cobaltserver.commands.*;
import se.fusion1013.plugin.cobaltserver.database.DatabaseHook;
import se.fusion1013.plugin.cobaltserver.discord.commands.DiscordEchoCommand;
import se.fusion1013.plugin.cobaltserver.discord.commands.DiscordPingCommand;
import se.fusion1013.plugin.cobaltserver.discord.commands.DiscordSubscribeCommand;
import se.fusion1013.plugin.cobaltserver.discord.commands.DiscordUnsubscribeCommand;
import se.fusion1013.plugin.cobaltserver.event.PlayerEvents;
import se.fusion1013.plugin.cobaltserver.manager.*;
import se.fusion1013.plugin.cobaltserver.settings.ServerSettingsManager;

/**
 * This plugin contains various tools for server admins
 */
public final class CobaltServer extends JavaPlugin implements CobaltPlugin {

    private static CobaltServer INSTANCE;
    public static CobaltServer getInstance() { return INSTANCE; }

    public CobaltServer(){
        INSTANCE = this;
    }

    @Override
    public String getPrefix() {
        return "prefix.server";
    }

    @Override
    public void onEnable() {
        CobaltCore.getInstance().registerCobaltPlugin(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        CobaltCore.getInstance().onDisable();
    }

    // ----- MANAGER -----

    @Override
    public void reloadManagers() {
        CobaltCore.getInstance().getManager(ServerConfigManager.class);
        CobaltCore.getInstance().getManager(ChatManager.class);
        CobaltCore.getInstance().getManager(DiscordManager.class);
        CobaltCore.getInstance().getManager(ServerItemManager.class);
        CobaltCore.getInstance().getManager(GameManager.class);
        CobaltCore.getInstance().getManager(ResourcePackManager.class);
        CobaltCore.getInstance().getManager(ServerSettingsManager.class);
    }

    // ----- DATABASE -----

    @Override
    public void initDatabaseTables(){
        DatabaseHook.instantiateTables();
    }

    // ----- COMMAND REGISTRATION -----

    @Override
    public void registerCommands() {
        ConfigManager config = ConfigManager.getInstance();

        if (config.getBooleanFromConfig(this, "server.yml", "enable-server-command")) ServerCommand.register();
        if (config.getBooleanFromConfig(this, "server.yml", "enable-warp-command")) WarpCommand.register();
        if (config.getBooleanFromConfig(this, "server.yml", "enable-gamemode-command")) GamemodeCommand.register();
        if (config.getBooleanFromConfig(this, "server.yml", "enable-color-command")) ColorCommand.register();
        if (config.getBooleanFromConfig(this, "server.yml", "enable-broadcast-command")) BroadcastCommand.register();
        if (config.getBooleanFromConfig(this, "server.yml", "enable-near-command")) NearCommand.register();
        if (config.getBooleanFromConfig(this, "server.yml", "enable-find-command")) FindCommand.register();
        if (config.getBooleanFromConfig(this, "server.yml", "enable-seen-command")) SeenCommand.register();

        // TODO: Add to config
        NicknameCommand.register();
        MinecraftDiscordCommand.register();
        GameCommand.register();
        HatCommand.register();
        // TODO: if (PlayerUtil.isVanishInstalled()) VanishCommand.register();
        if (PlayerUtil.isVanishInstalled()) Bukkit.getPluginCommand("vanish").setExecutor(new VanishCommand()); // TODO: Remove this and use CommandAPI if possible

        // Discord Commands
        DiscordPingCommand.register();
        DiscordEchoCommand.register();
        DiscordSubscribeCommand.register();
        DiscordUnsubscribeCommand.register();
    }

    // ----- LISTENERS -----

    @Override
    public void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerEvents(), this);
    }
}
