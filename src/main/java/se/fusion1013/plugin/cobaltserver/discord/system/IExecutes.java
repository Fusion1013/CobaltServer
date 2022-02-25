package se.fusion1013.plugin.cobaltserver.discord.system;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public interface IExecutes {
    void execute(User author, TextChannel channel, Object[] args);
}
