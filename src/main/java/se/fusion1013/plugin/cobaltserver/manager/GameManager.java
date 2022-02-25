package se.fusion1013.plugin.cobaltserver.manager;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.manager.LocaleManager;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;
import se.fusion1013.plugin.cobaltserver.CobaltServer;
import se.fusion1013.plugin.cobaltserver.database.DatabaseHook;

import java.util.*;

public class GameManager extends Manager implements Listener {

    // ----- VARIABLES -----

    private static Map<UUID, Integer> tagStats = new HashMap<>();
    int tagCooldown = 6000;
    boolean tagOnCooldown = false;
    long currentTime = System.currentTimeMillis();

    // ----- CONSTRUCTORS -----

    public GameManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- TAG GAME -----

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity hitEntity = event.getEntity();

        if (damager instanceof Player p1 && hitEntity instanceof Player p2) {
            ItemStack item = p1.getInventory().getItemInMainHand();

            if (ServerItemManager.TAG_ITEM.compareTo(item)) {
                if (!tagOnCooldown) tagPlayer(p1, p2);
                else {
                    p1.playSound(p1.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1, 1);
                    p1.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("Tag is on cooldown: " + getTagCooldownMinutes() + "m left"));
                }
            }
        }
    }

    private double getTagCooldownMinutes() {
        long maxCooldown = tagCooldown * 50L;
        long currentCooldownMillis = maxCooldown - (System.currentTimeMillis() - currentTime);
        long currentCooldownSeconds = currentCooldownMillis / 100;
        return (double)((currentCooldownSeconds) / 60L) / 10.0;
    }

    private void tagPlayer(Player tagger, Player target) {

        // Give tag item
        tagger.getInventory().getItemInMainHand().setAmount(tagger.getInventory().getItemInMainHand().getAmount() - 1);

        ItemStack tagItem = ServerItemManager.TAG_ITEM.getItemStack();

        target.getInventory().addItem(tagItem);
        if (!target.getInventory().contains(tagItem)) {
            Item spawnedEntity = (Item)target.getLocation().getWorld().spawnEntity(target.getLocation(), EntityType.DROPPED_ITEM);
            spawnedEntity.setItemStack(tagItem);
        }

        // Send notification
        target.playSound(target.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
        target.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("You have been tagged!"));
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("tagger", tagger.getName())
                .addPlaceholder("target", target.getName())
                .build();

        LocaleManager.getInstance().broadcastMessage("prefix.tag", "games.tag.message", placeholders);

        // Store new value
        int value = 1;
        if (tagStats.get(target.getUniqueId()) != null) value += tagStats.get(target.getUniqueId());
        tagStats.put(target.getUniqueId(), value);

        // Set tag cooldown
        target.setCooldown(ServerItemManager.TAG_ITEM.getItemStack().getType(), tagCooldown);
        tagOnCooldown = true;
        Bukkit.getScheduler().runTaskLater(CobaltServer.getInstance(), () -> tagOnCooldown = false, tagCooldown);
        currentTime = System.currentTimeMillis();
    }

    public void storeTagStats() {
        DatabaseHook.insertTagStats(tagStats);
    }

    // ----- GETTERS / SETTERS -----

    public int getTagCount(UUID uuid) {
        if (uuid != null) return tagStats.get(uuid);
        else return 0;
    }

    public int getTagCount(Player player) {
        if (tagStats.get(player.getUniqueId()) != null) return tagStats.get(player.getUniqueId());
        else return 0;
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        Bukkit.getPluginManager().registerEvents(this, CobaltServer.getInstance());
        tagStats = DatabaseHook.getTagStats();
    }

    @Override
    public void disable() {
        storeTagStats();
    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static GameManager INSTANCE = null;
    /**
     * Returns the object representing this <code>GameManager</code>.
     *
     * @return The object of this class
     */
    public static GameManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new GameManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
