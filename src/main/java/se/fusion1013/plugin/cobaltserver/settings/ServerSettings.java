package se.fusion1013.plugin.cobaltserver.settings;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import se.fusion1013.plugin.cobaltcore.settings.SettingCategory;

import java.util.ArrayList;
import java.util.List;

public enum ServerSettings implements SettingCategory {

    // ----- VALUES -----

    SECRET("Secret", "Secret settings for CobaltServer", Material.END_CRYSTAL);

    // ----- VARIABLES -----

    private final String displayName;
    private final String description;
    private final ItemStack displayItem;

    // ----- CONSTRUCTORS -----

    ServerSettings(String displayName, String description, Material displayItem) {
        this.displayName = displayName;
        this.description = description;
        this.displayItem = new ItemStack(displayItem);
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public ItemStack getItem() {
        return displayItem.clone();
    }

    @Override
    public ItemStack getItem(Object parameter) {
        ItemStack item = getItem();

        ItemMeta im = item.getItemMeta();
        List<String> lore = new ArrayList<>();

        im.setDisplayName(ChatColor.WHITE + getDisplayName());
        lore.add(ChatColor.GRAY + getDescription());

        im.setLore(lore);
        item.setItemMeta(im);
        return item;

    }

}
