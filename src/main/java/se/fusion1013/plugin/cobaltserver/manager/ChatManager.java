package se.fusion1013.plugin.cobaltserver.manager;

import net.dv8tion.jda.api.entities.Activity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.ItemMeta;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.util.HexUtils;
import se.fusion1013.plugin.cobaltcore.util.PlayerUtil;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;
import se.fusion1013.plugin.cobaltserver.CobaltServer;
import se.fusion1013.plugin.cobaltserver.database.DatabaseHook;
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
    public void anvilNameEvent(InventoryClickEvent event) {

        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getType() == Material.AIR) return;

        if (event.getInventory().getType() == InventoryType.ANVIL) {
            if (event.getSlotType() == InventoryType.SlotType.RESULT) {
                ItemMeta meta = event.getCurrentItem().getItemMeta();
                if (meta == null) return;

                meta.setDisplayName(HexUtils.colorify(meta.getDisplayName()));
                event.getCurrentItem().setItemMeta(meta);
            }
        }
    }

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
                p.sendMessage(HexUtils.colorify("<" + playerName + "&r> ") + message);
            }

            // Send message to discord channels
            DiscordManager.getInstance().sendMessage("<" + playerName + "> " + message, DiscordManager.ChannelOption.GLOBAL);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {

        // Set vanished state
        Player player = event.getPlayer();
        if (player.hasPermission("cobalt.commands.server.vanish") && PlayerUtil.isVanishInstalled() && ServerSettingsManager.JOIN_VANISHED.getValue(player)) { // TODO: Check setting if player should join vanished & send join message
            PlayerUtil.setVanished(player, true, true);
            player.sendMessage(ChatColor.DARK_AQUA + "You have joined vanished and silently");

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.equals(player) && p.hasPermission("cobalt.commands.server.vanish")) p.sendMessage(ChatColor.DARK_AQUA + player.getName() + " has joined vanished");
            }
        } else {
            // Send join messages
            PlayerUtil.sendJoinMessage(CobaltServer.getInstance(), player);
            DiscordManager.getInstance().sendMessage("```diff\n+ " + player.getName() + " joined the game" + "\n```", DiscordManager.ChannelOption.JOIN);
            DiscordManager.sendPlayerList(null);

            // Update discord bot status
            DiscordManager.getInstance().updateBotStatus(Activity.watching(PlayerUtil.getUnvanishedPlayerCount() + " players online"));
        }

        // TODO: Permissions

        // Set Nickname
        String nickname = playerNicknames.get(player.getUniqueId());
        if (nickname != null) {
            if (!nickname.equals("")) {
                player.setDisplayName(HexUtils.colorify(nickname));
                player.setPlayerListName(HexUtils.colorify(nickname));
            }
        }

        event.setJoinMessage("");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage("");

        // Send quit message if player is not vanished // TODO: Check settings if quit message should be sent if player is vanished
        if (!PlayerUtil.isVanished(event.getPlayer())) {
            PlayerUtil.sendQuitMessage(CobaltServer.getInstance(), event.getPlayer());
            DiscordManager.getInstance().sendMessage("```diff\n- " + event.getPlayer().getName() + " left the game" + "\n```", DiscordManager.ChannelOption.LEAVE);
            DiscordManager.sendPlayerList(event.getPlayer());

            // Update discord bot status
            DiscordManager.getInstance().updateBotStatus(Activity.watching(PlayerUtil.getUnvanishedPlayerCount() - 1 + " players online")); // Minus the player that is leaving
        }
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

        if (nickname != null) {
            if (!nickname.equals("")) {
                p.setDisplayName(HexUtils.colorify(nickname));
                p.setPlayerListName(HexUtils.colorify(nickname));
            }
        }
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
