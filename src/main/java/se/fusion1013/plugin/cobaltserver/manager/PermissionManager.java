package se.fusion1013.plugin.cobaltserver.manager;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltserver.CobaltServer;
import se.fusion1013.plugin.cobaltserver.groups.PermissionGroup;

import java.util.HashMap;
import java.util.UUID;

public class PermissionManager extends Manager {

    // ----- VARIABLES -----

    // All Permissions
    private static final HashMap<UUID, PermissionAttachment> perms = new HashMap<>(); // This stores player permission data for easy lookup. <uuid, attachment>

    // Private Permissions
    // TODO: private static final HashMap<String, Boolean>

    // Group Permissions
    private static final HashMap<String, PermissionGroup> permissionGroups = new HashMap<>(); // Stores all permission groups. <name, group>
    private static final HashMap<UUID, PermissionGroup> playerPermissionGroups = new HashMap<>(); // The group each player is a member of. <uuid, group>

    // ----- CONSTRUCTORS -----

    public PermissionManager(CobaltCore cobaltCore) {
        super(cobaltCore);
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }

    // ----- GETTERS / SETTERS -----

    public static void recalculatePermissions(Player player) {
        resetPermissions(player); // Removes all permissions from the player

        // Adds all permissions back to the player, starting with group permissions
        PermissionGroup group = playerPermissionGroups.get(player.getUniqueId());
        if (group != null) group.applyPermissions(player);

        // Individual permissions

    }

    /**
     * Sets the permission group for the player.
     *
     * @param player the player to set the group for.
     * @param groupName the name of the group to set.
     * @return false if the group was not found.
     */
    public static boolean setPlayerPermissionGroup(Player player, String groupName) {
        PermissionGroup group = permissionGroups.get(groupName);
        if (group == null) return false;
        playerPermissionGroups.put(player.getUniqueId(), group);
        return true;
    }

    /**
     * Resets all group permissions for the player to their default values.
     *
     * @param player
     */
    public static void resetGroupPermissions(Player player) {

    }

    /**
     * Resets all permissions for the player to their default values.
     *
     * @param player the player to reset the permissions for.
     */
    public static void resetPermissions(Player player) {

        // Remove Group Permissions
        PermissionGroup group = playerPermissionGroups.get(player.getUniqueId());
        if (group != null) {
            group.removePermissions(player);
        }

        // Remove Individual Permissions
        PermissionAttachment attachment = perms.get(player.getUniqueId());
        if (attachment != null) {
            for (String permission : attachment.getPermissions().keySet()) {
                attachment.unsetPermission(permission);
            }
        }
    }

    /**
     * Adds a permission to a player.
     *
     * @param player the player to give the permission.
     * @param permission the permission to give to the player.
     */
    public static void addPermission(Player player, String permission) {
        setPermission(player, permission, true);
    }

    /**
     * Sets a permission for a player.
     *
     * @param player the player to set the permission for.
     * @param permission the permission to set.
     * @param toSet the value to set the permission to.
     */
    public static void setPermission(Player player, String permission, boolean toSet) {
        PermissionAttachment pperms = perms.get(player.getUniqueId());
        if (pperms == null) pperms = perms.put(player.getUniqueId(), player.addAttachment(CobaltServer.getInstance()));
        if (pperms == null) return;
        pperms.setPermission(permission, toSet);
    }

    /**
     * Removes a permission from a player.
     *
     * @param player the player to remove the permission from.
     * @param permission the permission to remove.
     */
    public static void removePermission(Player player, String permission) {
        PermissionAttachment pperms = perms.get(player.getUniqueId());
        if (pperms == null) return;
        pperms.unsetPermission(permission);
    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static PermissionManager INSTANCE = null;
    /**
     * Returns the object representing this <code>PermissionManager</code>.
     *
     * @return The object of this class
     */
    public static PermissionManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new PermissionManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }

}
