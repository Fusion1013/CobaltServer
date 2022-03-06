package se.fusion1013.plugin.cobaltserver.warp;

import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltserver.database.DatabaseHook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarpManager extends Manager {

    // ----- VARIABLES -----

    private static Map<String, Warp> warpCache = new HashMap<>(); // Warps stored as <expandedName, warp>

    // ----- WARP PRIVACY SETTING -----

    public static String[] getPrivacyNames() {
        return new String[] {"private", "public"};
    }

    public static boolean setWarpPrivacy(Player player, String warpName, String privacy) {
        Warp warp = getWarp(player, warpName);

        if (warp != null) {
            return warp.setPrivacyLevel(privacy);
        }

        return false;
    }

    // ----- WARP REMOVAL -----

    public static int removeWarp(Player player, String warpName) {
        Warp warp = getWarp(player, warpName);

        if (warp != null) {
            warpCache.remove(warp.getExpandedName());
            return DatabaseHook.deleteWarp(warp.getName(), warp.getOwner());
        }

        return 0;
    }

    // ----- WARP CREATION -----

    public static void insertWarp(Warp warp) {
        warpCache.put(warp.getExpandedName(), warp);
    }

    // ----- WARP GETTING -----

    /**
     * Gets a warp if the player has access to it.
     *
     * @param player the player that is getting the warp.
     * @param warpName the name of the warp.
     * @return the warp, or null if no match was found.
     */
    public static Warp getWarp(Player player, String warpName) {
        Warp[] warps = getWarps(player);
        for (Warp warp : warps) {
            if (warp.getName().equalsIgnoreCase(warpName) || warp.getExpandedName().equalsIgnoreCase(warpName)) return warp; // TODO: Make sure that name is not in expanded name format
        }
        return null;
    }

    /**
     * Gets an array of all warp names.
     *
     * @return an array of warp names.
     */
    @Deprecated
    public static String[] getAllWarpNames() {
        List<Warp> warps = new ArrayList<>(warpCache.values());
        List<String> names = new ArrayList<>();
        for (Warp warp : warps) names.add(warp.getName());
        return names.toArray(new String[0]);
    }

    /**
     * Gets an array of all warp names that the player has access to.
     *
     * @param player the player that is getting the warp names.
     * @return an array of warp names.
     */
    public static String[] getWarpNames(Player player) {
        Warp[] warps = getWarps(player);
        List<String> names = new ArrayList<>();
        for (Warp warp : warps) names.add(warp.getName());
        return names.toArray(new String[0]);
    }

    /**
     * Gets all warps.
     *
     * @return an array of warps.
     */
    public static Warp[] getAllWarps() {
        return warpCache.values().toArray(new Warp[0]);
    }

    /**
     * Gets all warps that the player has permission to see.
     *
     * @param player the player that is getting the warps.
     * @return an array of warps.
     */
    public static Warp[] getWarps(Player player) {
        List<Warp> availableWarps = new ArrayList<>();

        for (Warp warp : warpCache.values()) {
            if (warp.getPrivacyLevel() == Warp.PrivacyLevel.PUBLIC) availableWarps.add(warp); // If the warp is public, add it.
            else if (warp.getOwner() == player.getUniqueId()) availableWarps.add(warp); // If the player is the owner of the warp, add it.
            else if (player.hasPermission("cobalt.server.warps.see_private")) availableWarps.add(warp); // If the player has the see private warps permission, add it.
        }

        return availableWarps.toArray(new Warp[0]);
    }

    // ----- CONSTRUCTORS -----

    public WarpManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        warpCache = DatabaseHook.getWarps();
    }

    @Override
    public void disable() {
        DatabaseHook.saveWarps(warpCache);
    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static WarpManager INSTANCE = null;
    /**
     * Returns the object representing this <code>WarpManager</code>.
     *
     * @return The object of this class
     */
    public static WarpManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new WarpManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
