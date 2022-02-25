package se.fusion1013.plugin.cobaltserver.discord.system;

public interface IDiscordArgument<T> {

    T parseArgument(String s);

    boolean validArgument(String s);

}
