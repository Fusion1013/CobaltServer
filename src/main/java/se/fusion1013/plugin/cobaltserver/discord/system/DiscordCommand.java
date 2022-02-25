package se.fusion1013.plugin.cobaltserver.discord.system;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import se.fusion1013.plugin.cobaltserver.manager.DiscordManager;

import java.util.ArrayList;
import java.util.List;

public class DiscordCommand {

    // ----- VARIABLES -----

    private List<DiscordCommand> subCommands = new ArrayList<>();
    private List<IDiscordArgument<?>> arguments = new ArrayList<>();

    private String commandName;
    private String helpMessage = "[Missing description]";
    private IExecutes executes;

    // ----- CONSTRUCTORS -----

    public DiscordCommand(String commandName) {
        this.commandName = commandName;
    }

    // ----- COMMAND EXECUTION -----

    public boolean executeCommand(User author, TextChannel channel, List<String> rest) {
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

    public DiscordCommand withPermission(String permission) {
        // TODO
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
