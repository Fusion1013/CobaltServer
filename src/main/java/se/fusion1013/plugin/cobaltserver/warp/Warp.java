package se.fusion1013.plugin.cobaltserver.warp;

import org.bukkit.Location;

import java.util.UUID;

public class Warp {

    private final int id;
    private final String name;
    private final UUID owner;
    private final Location location;
    private PrivacyLevel privacyLevel;

    public Warp(String name, UUID owner, Location location){
        this.id = hashCode();
        this.name = name;
        this.owner = owner;
        this.location = location;
        this.privacyLevel = PrivacyLevel.PUBLIC;
    }

    public void setPrivacyLevel(String privacyLevel){
        if (privacyLevel.equalsIgnoreCase("private")) this.privacyLevel = PrivacyLevel.PRIVATE;
        else if (privacyLevel.equalsIgnoreCase("public")) this.privacyLevel = PrivacyLevel.PUBLIC;
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
}
