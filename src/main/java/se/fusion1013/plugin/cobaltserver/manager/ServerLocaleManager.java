package se.fusion1013.plugin.cobaltserver.manager;

import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltserver.locale.ServerEnglishLocale;

public class ServerLocaleManager extends Manager {

    private static ServerLocaleManager INSTANCE = null;
    /**
     * Returns the object representing this <code>ServerLocaleManager</code>.
     *
     * @return The object of this class
     */
    public static ServerLocaleManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new ServerLocaleManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }

    public ServerLocaleManager(CobaltCore cobaltCore) {
        super(cobaltCore);
    }

    private static void instantiateLocale(){
        ServerEnglishLocale.addLocale();
    }

    @Override
    public void reload() {
        instantiateLocale();
    }

    @Override
    public void disable() {

    }
}
