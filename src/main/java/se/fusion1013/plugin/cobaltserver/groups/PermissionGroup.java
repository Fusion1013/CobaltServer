package se.fusion1013.plugin.cobaltserver.groups;

import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltserver.manager.PermissionManager;

import java.util.HashMap;

public class PermissionGroup {

    // ----- VARIABLES -----

    private String name;
    private String prefix;

    // Permission information
    private HashMap<String, Boolean> permissions = new HashMap<>();
    private PermissionGroup parent;

    // ----- CONSTRUCTORS -----

    /**
     * Creates a new <code>PermissionGroup</code>.
     *
     * @param name the name of the group
     */
    public PermissionGroup(String name) {
        this.name = name;
    }

    // ----- PLAYER -----

    /**
     * Removes all group permissions from the player.
     *
     * @param player the player to remove all the permissions from.
     */
    public void removePermissions(Player player) {
        if (parent != null) parent.removePermissions(player);

        for (String perm : permissions.keySet()) {
            PermissionManager.removePermission(player, perm);
        }
    }

    /**
     * Applies all permissions for the player.
     *
     * @param player the player to apply the permissions to.
     */
    public void applyPermissions(Player player) {
        if (parent != null) parent.applyPermissions(player); // Applies the permissions from the parents first

        for (String perm : permissions.keySet()) {
            PermissionManager.setPermission(player, perm, permissions.get(perm));
        }
    }

    // ----- INHERITANCE -----

    /**
     * Sets the parent of the group.
     *
     * @param parent the parent to set.
     * @return the result of the inheritance setting.
     */
    public InheritanceResult setParent(PermissionGroup parent) {
        InheritanceResult checkInheritance = checkInheritance(this, parent);
        if (checkInheritance.equals(InheritanceResult.SUCCESS)) this.parent = parent;
        return checkInheritance;
    }

    /**
     * Checks the inheritance of two groups.
     *
     * @param child the child.
     * @param parent the parent.
     * @return SUCCESS if it is safe to set the parent of the child.
     */
    private static InheritanceResult checkInheritance(PermissionGroup child, PermissionGroup parent) {
        if (parent == null) return InheritanceResult.SUCCESS;
        if (child.compareTo(parent)) return InheritanceResult.ERROR_SELF_INHERIT;
        return checkInheritance(child, parent.getParent());
    }

    public enum InheritanceResult {
        SUCCESS,
        ERROR_SELF_INHERIT,
        ERROR_CYCLIC_INHERIT,
        FAIL
    }

    // ----- CHECKS -----

    public boolean compareTo(PermissionGroup compare) {
        if (compare.getName().equalsIgnoreCase(name)) return false;
        return true;
    }

    // ----- GETTERS / SETTERS -----

    public PermissionGroup getParent() {
        return this.parent;
    }

    public void addPermission(String permission, boolean value) {
        permissions.put(permission, value);
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}
