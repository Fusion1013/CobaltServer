package se.fusion1013.plugin.cobaltserver.manager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.util.HexUtils;
import se.fusion1013.plugin.cobaltserver.CobaltServer;

public class ChatManager extends Manager implements Listener {

    private static ChatManager INSTANCE = null;
    /**
     * Returns the object representing this <code>ChatManager</code>.
     *
     * @return The object of this class
     */
    public static ChatManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new ChatManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }

    public ChatManager(CobaltCore cobaltCore) {
        super(cobaltCore);
    }

    @EventHandler
    public void playerChatEvent(AsyncPlayerChatEvent event) {
        event.setMessage(HexUtils.colorify(event.getMessage()));
    }

    @Override
    public void reload() {
        CobaltServer.getInstance().getServer().getPluginManager().registerEvents(this, CobaltServer.getInstance());
    }

    @Override
    public void disable() {

    }
}
