package se.fusion1013.plugin.cobaltserver;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.CobaltPlugin;
import se.fusion1013.plugin.cobaltcore.config.ConfigManager;
import se.fusion1013.plugin.cobaltcore.util.PlayerUtil;
import se.fusion1013.plugin.cobaltserver.commands.*;
import se.fusion1013.plugin.cobaltserver.commands.info.*;
import se.fusion1013.plugin.cobaltserver.commands.message.BroadcastCommand;
import se.fusion1013.plugin.cobaltserver.commands.message.MessageCommand;
import se.fusion1013.plugin.cobaltserver.commands.self.GamemodeCommand;
import se.fusion1013.plugin.cobaltserver.commands.self.HatCommand;
import se.fusion1013.plugin.cobaltserver.commands.self.NicknameCommand;
import se.fusion1013.plugin.cobaltserver.commands.self.VanishCommand;
import se.fusion1013.plugin.cobaltserver.commands.teleport.TeleportCommand;
import se.fusion1013.plugin.cobaltserver.commands.teleport.WarpCommand;
import se.fusion1013.plugin.cobaltserver.database.DatabaseHook;
import se.fusion1013.plugin.cobaltserver.database.ServerDataManager;
import se.fusion1013.plugin.cobaltserver.discord.commands.DiscordEchoCommand;
import se.fusion1013.plugin.cobaltserver.discord.commands.DiscordPingCommand;
import se.fusion1013.plugin.cobaltserver.discord.commands.DiscordSubscribeCommand;
import se.fusion1013.plugin.cobaltserver.discord.commands.DiscordUnsubscribeCommand;
import se.fusion1013.plugin.cobaltserver.event.PlayerEvents;
import se.fusion1013.plugin.cobaltserver.manager.*;
import se.fusion1013.plugin.cobaltserver.settings.ServerSettingsManager;
import se.fusion1013.plugin.cobaltserver.warp.WarpManager;

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
        // CobaltCore.getInstance().onDisable();
        CobaltCore.getInstance().disableCobaltPlugin(this);
    }

    // ----- MANAGER -----

    @Override
    public void reloadManagers() {
        CobaltCore.getInstance().getManager(this, ServerDataManager.class);

        CobaltCore.getInstance().getManager(this, ServerConfigManager.class);
        CobaltCore.getInstance().getManager(this, ChatManager.class);
        CobaltCore.getInstance().getManager(this, DiscordManager.class);
        CobaltCore.getInstance().getManager(this, ServerItemManager.class);
        CobaltCore.getInstance().getManager(this, GameManager.class);
        CobaltCore.getInstance().getManager(this, ResourcePackManager.class);
        CobaltCore.getInstance().getManager(this, ServerSettingsManager.class);
        CobaltCore.getInstance().getManager(this, WarpManager.class);
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
        if (PlayerUtil.isVanishInstalled()) Bukkit.getPluginCommand("vanish").setExecutor(new VanishCommand()); // TODO: Remove this and use CommandAPI if possible
        TeleportCommand.register();
        InvseeCommand.register();
        EnderchestCommand.register();
        MessageCommand.register();

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
