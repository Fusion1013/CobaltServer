package se.fusion1013.plugin.cobaltserver.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import se.fusion1013.plugin.cobaltserver.database.DatabaseHook;
import se.fusion1013.plugin.cobaltserver.manager.ResourcePackManager;

public class PlayerEvents implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Database
        DatabaseHook.insertLeavingPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Database
        DatabaseHook.insertJoiningPlayer(event.getPlayer());

        // Resource Pack
        String resourcePackString = ResourcePackManager.getInstance().getResourcePackString();
        event.getPlayer().setResourcePack(resourcePackString);
    }
}
