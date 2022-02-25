package se.fusion1013.plugin.cobaltserver.discord.system;

public class DiscordStringArgument implements IDiscordArgument<String> {

    // ----- VARIABLES -----

    String name;
    String[] limitedOptions;

    // ------ CONSTRUCTORS -----

    public DiscordStringArgument(String name) {
        this.name = name;
    }

    // ----- PARSING -----

    @Override
    public String parseArgument(String s) {
        return s;
    }

    @Override
    public boolean validArgument(String s) {
        if (limitedOptions == null) return true;

        for (String option : limitedOptions) if (s.equalsIgnoreCase(option)) return true;

        return false;
    }

    // ----- GETTERS / SETTERS -----

    public DiscordStringArgument setLimitedOptions(String... options) {
        this.limitedOptions = options;
        return this;
    }
}
