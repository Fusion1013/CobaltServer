package se.fusion1013.plugin.cobaltserver.manager;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.item.CustomItem;
import se.fusion1013.plugin.cobaltcore.manager.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.manager.Manager;

public class ServerItemManager extends Manager {

    public ServerItemManager(CobaltCore cobaltCore) {
        super(cobaltCore);
    }

    public static final CustomItem TAG_ITEM = CustomItemManager.register(new CustomItem.CustomItemBuilder("tag_item", Material.BLAZE_ROD, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.GOLD + "Tag Stick")
            .addLoreLine(ChatColor.WHITE + "Hit someone with this stick to tag them")
            .build());

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static ServerItemManager INSTANCE = null;
    /**
     * Returns the object representing this <code>ServerItemManager</code>.
     *
     * @return The object of this class
     */
    public static ServerItemManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new ServerItemManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }

}
