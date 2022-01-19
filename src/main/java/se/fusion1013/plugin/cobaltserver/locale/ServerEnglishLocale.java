package se.fusion1013.plugin.cobaltserver.locale;

import se.fusion1013.plugin.cobaltcore.locale.EnglishLocale;

public class ServerEnglishLocale {

    public static void addLocale(){
        EnglishLocale.addLocaleString("#0", "Plugin Message Prefix");
        EnglishLocale.addLocaleString("prefix.server", "&7[<g:#00aaaa:#0066aa>Server&7] ");

        EnglishLocale.addLocaleString("", "");

        // ----- WARP COMMAND -----

        EnglishLocale.addLocaleString("commands.warp.error.warp_not_found", "&7Warp &3%name% &7not found");
        EnglishLocale.addLocaleString("commands.warp.create.error.invalid_name", "&7Invalid Name: &3%name%");
        EnglishLocale.addLocaleString("commands.warp.create.error.name_already_exists", "&7Name Already Exists: &3%name%");
        EnglishLocale.addLocaleString("commands.warp.create.info.created_warp", "&7Created Warp &3%name%");

        EnglishLocale.addLocaleString("commands.warp.info.header", "&7Information for Warp &3%name%&7:");
        EnglishLocale.addLocaleString("commands.warp.info.detail.owner", "&7Owner: &3%owner%");
        EnglishLocale.addLocaleString("commands.warp.info.detail.world", "&7World: &3%world%");
        EnglishLocale.addLocaleString("commands.warp.info.detail.location", "&7Location: &3%x%&7, &3%y%&7, &3%z%");
        EnglishLocale.addLocaleString("commands.warp.info.detail.distance", "&7Distance: &3%distance% &7blocks");
        EnglishLocale.addLocaleString("commands.warp.info.detail.privacy", "&7Privacy: &3%privacy%");

        EnglishLocale.addLocaleString("commands.warp.list.header", "&7Displaying All Available Warps:");
        EnglishLocale.addLocaleString("commands.warp.list.entry", "&3%name% &7- &3%x%&7, &3%y%&7, &3%z%&7 in &3%world%");

        EnglishLocale.addLocaleString("commands.warp.teleport.success", "&7Teleported to &3%name%");

        EnglishLocale.addLocaleString("commands.warp.delete.deleted_warps", "&7Deleted &3%count% &7warp(s) with the name &3%name%");

        // ----- GAMEMODE COMMAND -----

        EnglishLocale.addLocaleString("commands.gamemode.change", "&7Set &3%player_name%&7's gamemode to &3%gamemode%");
        EnglishLocale.addLocaleString("commands.gamemode.error.gamemode_not_found", "&7Gamemode &3%gamemode% &7not found");

        // ----- BROADCAST COMMAND -----

        EnglishLocale.addLocaleString("commands.broadcast.prefix", "&7[<g:#00aaaa:#0066aa>Broadcast&7] ");

        // ----- COLORS COMMAND

        EnglishLocale.addLocaleString("commands.colors.header", "&7&lColors");
        EnglishLocale.addLocaleString("commands.colors.color_codes_description", "&7Usage: <#HEXCODE>, #HEXCODE, &&7FORMAT_CODE, followed by the message");
        EnglishLocale.addLocaleString("commands.colors.color_codes", "&7Color Codes: &0&&00 &1&&11 &2&&22 &3&&33 &4&&44 &5&&55 &6&&66 &7&&77 &8&&88 &9&&99 &a&&aa &b&&bb &c&&cc &d&&dd &e&&ee &f&&ff");
        EnglishLocale.addLocaleString("commands.colors.formatting_codes", "&7Formatting Codes: &&ll&r&7 &&mm&r&7 &&nn&r&7 &&oo&r&7 &&rr");
    }
}
