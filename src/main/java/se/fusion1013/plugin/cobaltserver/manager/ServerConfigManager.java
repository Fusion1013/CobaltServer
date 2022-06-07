package se.fusion1013.plugin.cobaltserver.manager;

import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.config.ConfigManager;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltserver.CobaltServer;

public class ServerConfigManager extends Manager {

    // ----- CONSTRUCTOR -----

    public ServerConfigManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- LOGIC -----

    /**
     * Creates all the configuration files for <code>CobaltServer</code>.
     */
    private void createConfig() {
        // Discord
        ConfigManager.getInstance().updateCustomConfig(CobaltServer.getInstance(), "discord.yml");

        // Server
        ConfigManager.getInstance().updateCustomConfig(CobaltServer.getInstance(), "server.yml");
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        createConfig();
    }

    @Override
    public void disable() {

    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static ServerConfigManager INSTANCE = null;
    /**
     * Returns the object representing this <code>ServerConfigManager</code>.
     *
     * @return The object of this class
     */
    public static ServerConfigManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new ServerConfigManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
