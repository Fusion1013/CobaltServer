package se.fusion1013.plugin.cobaltserver.manager;

import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.manager.ConfigManager;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltserver.CobaltServer;

public class ResourcePackManager extends Manager {

    // ----- VARIABLES -----

    private String resourcePackString = "";

    // ----- CONSTRUCTORS -----

    public ResourcePackManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        this.resourcePackString = (String)ConfigManager.getInstance().getFromConfig(CobaltServer.getInstance(), "server.yml", "resource-pack-string");
    }

    @Override
    public void disable() {

    }

    // ----- GETTERS / SETTERS -----

    /**
     * Sets the resource pack string. This should be a direct download link to the resource pack.
     *
     * @param url the link to the resource pack.
     */
    public void setResourcePackString(String url) {
        this.resourcePackString = url;
        ConfigManager.getInstance().writeString(CobaltServer.getInstance(), "server.yml", "resource-pack-string", url);
    }

    /**
     * Gets the resource pack string.
     *
     * @return the resource pack string.
     */
    public String getResourcePackString() {
        return resourcePackString;
    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static ResourcePackManager INSTANCE = null;
    /**
     * Returns the object representing this <code>ResourcePackManager</code>.
     *
     * @return The object of this class
     */
    public static ResourcePackManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new ResourcePackManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
