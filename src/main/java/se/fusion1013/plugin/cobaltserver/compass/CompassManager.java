package se.fusion1013.plugin.cobaltserver.compass;

import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltserver.warp.WarpManager;

public class CompassManager extends Manager {

    // ----- CONSTRUCTORS -----

    public CompassManager(CobaltCore cobaltCore) {
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

    private static CompassManager INSTANCE = null;
    /**
     * Returns the object representing this <code>CompassManager</code>.
     *
     * @return The object of this class
     */
    public static CompassManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new CompassManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
