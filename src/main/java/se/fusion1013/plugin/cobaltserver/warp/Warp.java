package se.fusion1013.plugin.cobaltserver.warp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Warp {

    // ----- VARIABLES -----

    private final int id;
    private final String name;
    private final UUID owner;
    private final String ownerName;
    private final Location location;
    private final UUID locationUUID;
    private PrivacyLevel privacyLevel;

    private final String expandedName;

    // ----- CONSTRUCTORS -----

    public Warp(String name, UUID owner, String ownerName, Location location, UUID locationUUID){
        this.id = hashCode();
        this.name = name;
        this.owner = owner;
        this.ownerName = ownerName;
        this.location = location;
        this.locationUUID = locationUUID;
        this.privacyLevel = PrivacyLevel.PUBLIC;
        this.expandedName = ownerName + ":" + name;
    }

    // ----- GETTERS / SETTERS -----

    public String getOwnerName() {
        return ownerName;
    }

    public boolean setPrivacyLevel(String privacyLevel){
        if (privacyLevel.equalsIgnoreCase("private")) {
            this.privacyLevel = PrivacyLevel.PRIVATE;
            return true;
        }
        else if (privacyLevel.equalsIgnoreCase("public")) {
            this.privacyLevel = PrivacyLevel.PUBLIC;
            return true;
        }

        return false;
    }

    public UUID getLocationUUID() {
        return locationUUID;
    }

    public String getExpandedName() {
        return expandedName;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UUID getOwner() {
        return owner;
    }

    public Location getLocation() {
        return location;
    }

    public PrivacyLevel getPrivacyLevel() {
        return privacyLevel;
    }

    public double getShortX(){
        return (double)Math.round(location.getX()*100)/100;
    }
    public double getShortY(){
        return (double)Math.round(location.getY()*100)/100;
    }
    public double getShortZ(){
        return (double)Math.round(location.getZ()*100)/100;
    }

    public enum PrivacyLevel{
        PRIVATE, PUBLIC
    }

    // ----- UTIL -----

    /**
     * Constructs the expanded name for the warp. (PlayerName:WarpName)
     *
     * @param player the owner of the warp.
     * @param warpName the name of the warp.
     * @return the expanded name of the warp.
     */
    public static String constructExpandedName(Player player, String warpName) {
        return player.getName() + ":" + warpName;
    }
}
