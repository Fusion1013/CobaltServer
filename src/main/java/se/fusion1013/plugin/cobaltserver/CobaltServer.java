package se.fusion1013.plugin.cobaltserver;

import org.bukkit.plugin.java.JavaPlugin;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.CobaltPlugin;
import se.fusion1013.plugin.cobaltserver.commands.BroadcastCommand;
import se.fusion1013.plugin.cobaltserver.commands.ColorCommand;
import se.fusion1013.plugin.cobaltserver.commands.GamemodeCommand;
import se.fusion1013.plugin.cobaltserver.commands.WarpCommand;
import se.fusion1013.plugin.cobaltserver.database.DatabaseHook;
import se.fusion1013.plugin.cobaltserver.manager.ChatManager;
import se.fusion1013.plugin.cobaltserver.manager.ServerLocaleManager;

/**
 * This plugin contains various tools for server admins
 */
public final class CobaltServer extends JavaPlugin implements CobaltPlugin {

    // TODO: Terrible to-do list below
    // Move database stuff to CobaltCore
    // TODO: Move to-do list to separate document

    private static CobaltServer INSTANCE;
    public static CobaltServer getInstance() { return INSTANCE; }

    public CobaltServer(){
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        CobaltCore.getInstance().registerCobaltPlugin(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    // ----- MANAGER -----

    @Override
    public void reloadManagers() {
        CobaltCore.getInstance().getManager(ChatManager.class);
        CobaltCore.getInstance().getManager(ServerLocaleManager.class);
    }

    // ----- DATABASE -----

    @Override
    public void initDatabaseTables(){
        DatabaseHook.instantiateTables();
    }

    // ----- COMMAND REGISTRATION -----

    @Override
    public void registerCommands() {
        WarpCommand.register();
        GamemodeCommand.register();
        ColorCommand.register();
        BroadcastCommand.register();
    }
}
