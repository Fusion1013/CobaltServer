package se.fusion1013.plugin.cobaltserver.settings;

import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.settings.Setting;
import se.fusion1013.plugin.cobaltcore.settings.SettingsManager;

public class ServerSettingsManager extends Manager {

    // ----- REGISTERED SETTINGS -----

    public static final Setting JOIN_VANISHED = SettingsManager.register()

    // ----- CONSTRUCTORS -----

    public ServerSettingsManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static ServerSettingsManager INSTANCE = null;
    /**
     * Returns the object representing this <code>ServerSettingsManager</code>.
     *
     * @return The object of this class
     */
    public static ServerSettingsManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new ServerSettingsManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
