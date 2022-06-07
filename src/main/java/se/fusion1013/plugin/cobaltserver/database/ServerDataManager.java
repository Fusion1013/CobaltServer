package se.fusion1013.plugin.cobaltserver.database;

import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltserver.database.warp.IWarpDao;
import se.fusion1013.plugin.cobaltserver.database.warp.WarpDaoSQLite;
import se.fusion1013.plugin.cobaltserver.warp.WarpManager;

public class ServerDataManager extends Manager {

    // ----- CONSTRUCTORS -----

    public ServerDataManager(CobaltCore cobaltCore) {
        super(cobaltCore);
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        DataManager.getInstance().registerDao(new WarpDaoSQLite(), IWarpDao.class);
    }

    @Override
    public void disable() {

    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static ServerDataManager INSTANCE = null;
    /**
     * Returns the object representing this <code>ServerDataManager</code>.
     *
     * @return The object of this class
     */
    public static ServerDataManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new ServerDataManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
