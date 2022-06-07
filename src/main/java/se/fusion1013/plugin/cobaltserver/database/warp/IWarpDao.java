package se.fusion1013.plugin.cobaltserver.database.warp;

import se.fusion1013.plugin.cobaltcore.database.system.IDao;
import se.fusion1013.plugin.cobaltserver.warp.Warp;

import java.util.Map;
import java.util.UUID;

public interface IWarpDao extends IDao {

    /**
     * Deletes the warps with the given name.
     *
     * @param name the name of the warp(s).
     * @param playerUUID the uuid of the player that is deleting the warp.
     */
    void deleteWarp(String name, UUID playerUUID);

    /**
     * Saves a map of warps to the database.
     *
     * @param warps warps to save.
     */
    void saveWarps(Map<String, Warp> warps);

    /**
     * Returns a map of all warps.
     *
     * @return a map of all warps.
     */
    Map<String, Warp> getWarps();

    @Override
    default String getId() { return "warp"; }
}
