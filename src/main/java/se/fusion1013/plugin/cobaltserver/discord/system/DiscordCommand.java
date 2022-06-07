package se.fusion1013.plugin.cobaltserver.discord.system;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import se.fusion1013.plugin.cobaltserver.manager.DiscordManager;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class DiscordCommand {

    // ----- VARIABLES -----

    private final List<DiscordCommand> subCommands = new ArrayList<>();
    private final List<IDiscordArgument<?>> arguments = new ArrayList<>();

    private final String commandName;
    private String helpMessage = "[Missing description]";
    private IExecutes executes;

    // Permissions
    private Permission permission = Permission.MESSAGE_SEND;

    // ----- CONSTRUCTORS -----

    public DiscordCommand(String commandName) {
        this.commandName = commandName;
    }

    // ----- COMMAND EXECUTION -----

    public boolean executeCommand(User author, TextChannel channel, List<String> rest) {
        // Check for permissions
        List<Guild> userGuilds = author.getMutualGuilds();
        boolean hasPermission = false;
        for (Guild g : userGuilds) {
            Member userMember = g.getMember(author);

            if (userMember != null) {
                if (userMember.hasPermission(channel, permission)) hasPermission = true;
            }
        }

        // If the user does not have the required permission, return.
        if (!hasPermission) return false;

        if (!executeSubcommand(author, channel, rest)) {

            if (rest.size() < arguments.size()) {
                channel.sendMessage("Incorrect arguments. Usage: " + helpMessage).queue(); // TODO: Automate
                return false;
            }

            Object[] args = new Object[arguments.size()];
            // Check if the arguments match for this command and execute it
            for (int i = 0; i < arguments.size(); i++) {
                if (!arguments.get(i).validArgument(rest.get(i))) {
                    channel.sendMessage("Invalid Argument: " + rest.get(i)).queue();
                    return false;
                }
                args[i] = arguments.get(i).parseArgument(rest.get(i));
            }
            executes.execute(author, channel, args);

        }
        return true;
    }

    private boolean executeSubcommand(User author, TextChannel channel, List<String> rest) {

        if (rest.size() <= 0) return false;

        for (DiscordCommand dc : subCommands) {
            if (dc.getName().equalsIgnoreCase(rest.get(0))) {
                rest.remove(0);
                return dc.executeCommand(author, channel, rest);
            }
        }
        return false;
    }

    // ----- BUILDER -----

    public DiscordCommand withHelp(String helpMessage) {
        this.helpMessage = helpMessage;
        return this;
    }

    public DiscordCommand withPermission(Permission permission) {
        this.permission = permission;
        return this;
    }

    public DiscordCommand withArgument(IDiscordArgument<?> argument) {
        arguments.add(argument);
        return this;
    }

    public DiscordCommand withSubcommand(DiscordCommand subcommand) {
        subCommands.add(subcommand);
        return this;
    }

    public DiscordCommand executes(IExecutes executes) {
        this.executes = executes;
        return this;
    }

    // ----- GETTERS / SETTERS -----

    public String getName() {
        return commandName;
    }

    // ----- REGISTER -----

    public void register() {
        DiscordManager.registerCommand(this);
    }

}
