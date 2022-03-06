package se.fusion1013.plugin.cobaltserver.settings;

import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.settings.BooleanSetting;
import se.fusion1013.plugin.cobaltcore.settings.SettingsManager;
import se.fusion1013.plugin.cobaltserver.CobaltServer;

public class ServerSettingsManager extends Manager {

    // ----- REGISTERED SETTINGS -----

    public static final BooleanSetting JOIN_VANISHED = SettingsManager.register(new BooleanSetting(CobaltServer.getInstance(), "join_vanished",
            "Whether the player will join in a vanished state or not.", "server.setting.join_vanished", false, false));
    public static final BooleanSetting FAKE_VANISH_MESSAGES = SettingsManager.register(new BooleanSetting(CobaltServer.getInstance(), "fake_vanish_messages",
            "Whether the player will send fake join/quit messages when vanishing/unvanishing.", "server.setting.fake_vanish_messages", false, false));

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
