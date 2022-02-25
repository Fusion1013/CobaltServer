package se.fusion1013.plugin.cobaltserver.manager;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.manager.LocaleManager;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.util.HexUtils;
import se.fusion1013.plugin.cobaltcore.util.PlayerUtil;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;
import se.fusion1013.plugin.cobaltserver.CobaltServer;
import se.fusion1013.plugin.cobaltserver.database.DatabaseHook;
import se.fusion1013.plugin.cobaltserver.settings.ServerSettings;
import se.fusion1013.plugin.cobaltserver.settings.ServerSettingsManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatManager extends Manager implements Listener {

    // ----- VARIABLES -----

    Map<UUID, String> playerNicknames = new HashMap<>();

    // ----- CONSTRUCTOR -----

    public ChatManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- EVENT -----

    @EventHandler
    public void playerChatEvent(AsyncPlayerChatEvent event) {
        event.setCancelled(true); // Cancel event and handle message handling separately to prevent lag spikes

        String message = event.getMessage();
        Player player = event.getPlayer();

        String playerName = playerNicknames.get(player.getUniqueId());
        if (playerName == null) playerName = player.getName();

        LocaleManager localeManager = LocaleManager.getInstance();

        // Color message
        message = HexUtils.colorify(message);

        // Staff Chat
        if (player.hasPermission("cobalt.server.staffchat") && message.charAt(0) == ',' && message.length() > 1) {
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("player", player.getName())
                    .addPlaceholder("message", message.substring(1)).build();

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("cobalt.server.staffchat")) {
                    localeManager.sendMessage("", p, "message.staffchat", placeholders);
                    if (p != player) p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 100, 1);
                }
            }
        } else {
            // Send Message to all recipients
            for (Player p : event.getRecipients()) {
                p.sendMessage("<" + playerName + "> " + message);
            }

            // Send message to discord channels
            DiscordManager.getInstance().sendMessage("<" + playerName + "> " + message, DiscordManager.ChannelOption.GLOBAL);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        // Set vanished state
        Player player = event.getPlayer();
        if (player.hasPermission("cobalt.commands.server.vanish") && PlayerUtil.isVanishInstalled()) { // TODO: Check setting if player should join vanished & send join message
            PlayerUtil.setVanished(player, true, true);
        } else {
            PlayerUtil.sendJoinMessage(player);
        }

        // CobaltCore.getInstance().getLogger().info("Value: " + ServerSettingsManager.JOIN_VANISHED.getValue(player));

        // TODO: Permissions

        // Discord Hook
        DiscordManager.getInstance().sendMessage("```diff\n+ " + player.getName() + " joined the game" + "\n```", DiscordManager.ChannelOption.JOIN);
        DiscordManager.sendPlayerList(null);

        // Set Nickname
        player.setDisplayName(playerNicknames.get(player.getUniqueId()));
        player.setPlayerListName(playerNicknames.get(player.getUniqueId()));

        event.setJoinMessage("");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Send quit message if player is not vanished // TODO: Check settings if quit message should be sent if player is vanished
        if (!PlayerUtil.isVanished(event.getPlayer())) PlayerUtil.sendQuitMessage(event.getPlayer());

        DiscordManager.getInstance().sendMessage("```diff\n- " + event.getPlayer().getName() + " left the game" + "\n```", DiscordManager.ChannelOption.LEAVE);
        DiscordManager.sendPlayerList(event.getPlayer());

        event.setQuitMessage("");
    }

    @EventHandler
    public void onAdvancementAwarded(PlayerAdvancementDoneEvent event) {
        DiscordManager.getInstance().sendMessage("**" + event.getPlayer() + " completed advancement " + event.getAdvancement() + "**", DiscordManager.ChannelOption.AWARDS);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        DiscordManager.getInstance().sendMessage(event.getDeathMessage(), DiscordManager.ChannelOption.DEATHS);
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        CobaltServer.getInstance().getServer().getPluginManager().registerEvents(this, CobaltServer.getInstance());
        loadPlayerNicknames();
    }

    @Override
    public void disable() {
        savePlayerNicknames();
    }

    // ----- GETTERS / SETTERS -----

    private void loadPlayerNicknames() {
        this.playerNicknames = DatabaseHook.getPlayerNicknames();
    }

    private void savePlayerNicknames() {
        for (UUID uuid : playerNicknames.keySet()) {
            DatabaseHook.updatePlayerNickname(uuid, playerNicknames.get(uuid));
        }
    }

    public void resetNickname(Player p) {
        setNickname(p, p.getName());
    }

    public void setNickname(Player p, String nickname) {
        playerNicknames.put(p.getUniqueId(), nickname);
        p.setDisplayName(nickname);
        p.setPlayerListName(nickname);
        p.setCustomName(nickname);
    }

    // ----- INSTANCE VARIABLE & METHOD -----

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
}
