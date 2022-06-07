package se.fusion1013.plugin.cobaltserver.manager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.config.ConfigManager;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;
import se.fusion1013.plugin.cobaltserver.CobaltServer;
import se.fusion1013.plugin.cobaltserver.database.DatabaseHook;
import se.fusion1013.plugin.cobaltserver.discord.system.DiscordCommand;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.*;
import java.util.List;

public class DiscordManager extends Manager implements EventListener {

    // ----- VARIABLES -----

    String prefix = "!";
    JDA jda;

    private static List<DiscordCommand> registeredCommands = new ArrayList<>();

    // Subscribed channels
    Map<ChannelOption, List<TextChannel>> channelOptionListMap = new HashMap<>();

    // ----- CONSTRUCTOR -----

    public DiscordManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- MESSAGE SENDING -----

    public void sendMessage(String message, ChannelOption option) {
        if (channelOptionListMap.get(option) != null) sendMessageToChannels(message, channelOptionListMap.get(option));
    }

    private void sendMessageToChannels(String message, List<TextChannel> channels) {
        for (TextChannel channel : channels) {
            channel.sendMessage(message).queue();
        }
    }

    // ----- INITIALIZATION / RELOADING -----

    public boolean reloadBot() {
        if (jda != null) jda.shutdown();

        return initializeBot();
    }

    private boolean initializeBot() {

        CobaltServer.getInstance().getLogger().info("Instantiating bot...");

        // Get bot prefix
        prefix = (String) ConfigManager.getInstance().getFromConfig(CobaltServer.getInstance(), "discord.yml", "prefix");

        String token = (String)ConfigManager.getInstance().getFromConfig(CobaltServer.getInstance(), "discord.yml", "discord-token");
        if (token.equalsIgnoreCase("")) {
            CobaltServer.getInstance().getLogger().info("Token not found");
            return false;
        }

        try {
            jda = JDABuilder.createDefault(token)
                    //.addEventListeners(new DiscordCommands())
                    .addEventListeners(this)
                    .setActivity(Activity.watching(Bukkit.getOnlinePlayers().size() + " players online")) // Set activity
                    .build().awaitReady();

        } catch (LoginException | InterruptedException ex) {
            ex.printStackTrace();
        }

        loadSubscribedChannels();

        return true;
    }

    /**
     * Loads all subscribed discord channels.
     */
    private void loadSubscribedChannels() {
        channelOptionListMap.clear();

        channelOptionListMap.put(ChannelOption.GLOBAL, getSubscribedChannels(ChannelOption.GLOBAL));
        channelOptionListMap.put(ChannelOption.STATUS, getSubscribedChannels(ChannelOption.STATUS));
        channelOptionListMap.put(ChannelOption.AWARDS, getSubscribedChannels(ChannelOption.AWARDS));
        channelOptionListMap.put(ChannelOption.DEATHS, getSubscribedChannels(ChannelOption.DEATHS));
        channelOptionListMap.put(ChannelOption.JOIN, getSubscribedChannels(ChannelOption.JOIN));
        channelOptionListMap.put(ChannelOption.LEAVE, getSubscribedChannels(ChannelOption.LEAVE));
        channelOptionListMap.put(ChannelOption.PLAYERLIST, getSubscribedChannels(ChannelOption.PLAYERLIST));
    }

    public static void registerCommand(DiscordCommand command) {
        registeredCommands.add(command);
    }

    // ----- COMMAND PARSING -----

    public void onMessageReceivedEvent(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        if (isCommand(event.getMessage())) {
            List<String> args = splitArguments(event.getMessage().getContentRaw());
            parseCommand(event.getAuthor(), event.getTextChannel(), args);
            return;
        }

        sendMinecraftMessage(event);
    }

    private List<String> splitArguments(String message) {
        if (message.length() <= 0) return new ArrayList<>();
        if (message.startsWith(prefix)) message = message.substring(1);
        List<String> split = new ArrayList<>();
        Collections.addAll(split, message.split(" "));
        return split;
    }

    private boolean parseCommand(User author, TextChannel channel, List<String> args) {

        if (args.size() <= 0) return false;
        String command = args.get(0);

        for (DiscordCommand dc : registeredCommands) {
            if (dc.getName().equalsIgnoreCase(command)) {
                args.remove(0);
                dc.executeCommand(author, channel, args);
                return true;
            }
        }
        return false;
    }

    // ----- MESSAGE SENDING -----

    /**
     * Sends a message to the minecraft chat.
     *
     * @param event the message event.
     */
    private void sendMinecraftMessage(MessageReceivedEvent event) {
        List<TextChannel> channels = DiscordManager.getInstance().getSubscribedChannels(DiscordManager.ChannelOption.GLOBAL);
        for (TextChannel c : channels) {
            if (event.getChannel() == c) {
                User author = event.getAuthor();

                StringPlaceholders placeholders = StringPlaceholders.builder()
                        .addPlaceholder("author", author.getName())
                        .addPlaceholder("message", event.getMessage().getContentRaw())
                        .build();
                LocaleManager locale = LocaleManager.getInstance();

                locale.broadcastMessage("prefix.discord", "discord.message", placeholders);
            }
        }
    }

    public static void sendPlayerList(Player excludePlayer) {
        List<TextChannel> channels = DiscordManager.getInstance().getSubscribedChannels(ChannelOption.PLAYERLIST);
        for (TextChannel c : channels) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Player List");
            eb.setColor(Color.BLUE);
            StringBuilder players = new StringBuilder();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p != excludePlayer) players.append(p.getName()).append(", ");
            }
            eb.addField("Players:", players.toString().substring(0, Math.max(0, players.length()-2)), false);
            c.sendMessageEmbeds(eb.build()).queue();
        }
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        if (!initializeBot()) return;

        // Send startup message to discord chat
        sendMessage("[Server]: Starting up minecraft server...", ChannelOption.STATUS);
    }

    @Override
    public void disable() {
        CobaltServer.getInstance().getLogger().info("Shutting down discord bot...");

        if (jda != null) jda.shutdown();
    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static DiscordManager INSTANCE = null;
    /**
     * Returns the object representing this <code>DiscordManager</code>.
     *
     * @return The object of this class
     */
    public static DiscordManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new DiscordManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }

    // ----- CHECKS -----

    private boolean isCommand(Message msg) {
        String contentRaw = msg.getContentRaw();
        if (contentRaw.length() <= 0) return false;
        else return contentRaw.startsWith(prefix);
    }

    // ----- GETTERS / SETTERS -----

    public void updateBotStatus(Activity activity) {
        if (jda != null) jda.getPresence().setActivity(activity);
    }

    private String getCommand(MessageReceivedEvent event) {
        Message msg = event.getMessage();
        String content = msg.getContentRaw();
        String[] split = content.split(" ");
        return split[0].substring(1);
    }

    // TODO: public ChannelOption[] getSubscriptions(TextChannel channel)

    /**
     * Removes a discord channel from the map.
     *
     * @param channel the channel to remove.
     * @param option the option the channel is subscribed to.
     * @return if the channel was removed or not.
     */
    public boolean removeChannel(TextChannel channel, ChannelOption option) {
        if (DatabaseHook.removeDiscordChannel(channel.getId(), option) > 0) {
            channelOptionListMap.get(option).remove(channel);
            return true;
        }
        return false;
    }

    /**
     * Adds a subscribed channel.
     *
     * @param channel channel to add.
     * @param option option the channel is subscribed to.
     * @return if the channel was successfully added.
     */
    public boolean addChannel(TextChannel channel, ChannelOption option) {
        if (DatabaseHook.insertDiscordChannel(channel.getId(), option) > 0) {
            channelOptionListMap.get(option).add(channel);
            return true;
        }
        return false;
    }

    /**
     * Gets all text channels that subscribe to the given option from the database.
     *
     * @param option the option the channels are subscribed to.
     * @return a list of text channels.
     */
    public List<TextChannel> getSubscribedChannels(ChannelOption option) {
        String[] channelKeys = DatabaseHook.getDiscordChannelKeys(option);
        List<TextChannel> textChannels = new ArrayList<>();

        for (String s : channelKeys) {
            textChannels.add(jda.getTextChannelById(s));
        }
        return textChannels;
    }

    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        if (genericEvent instanceof MessageReceivedEvent event) onMessageReceivedEvent(event);
    }

    // ----- ENUMERATOR -----

    public enum ChannelOption {
        GLOBAL,
        STATUS,
        AWARDS,
        DEATHS,
        JOIN,
        LEAVE,
        PLAYERLIST
    }

}
