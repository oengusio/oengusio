package app.oengus.service;

import app.oengus.api.DiscordApi;
import app.oengus.entity.model.api.discord.DiscordGuild;
import app.oengus.entity.model.api.discord.DiscordInvite;
import app.oengus.entity.model.api.discord.DiscordMember;
import app.oengus.service.jda.JDAService;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DiscordApiService {
    @Autowired
    private DiscordApi discordApi;

    @Autowired
    private JDAService jda;

    @Value("${discord.botToken}")
    private String botToken;

    public DiscordInvite fetchInvite(final String inviteCode) {
        return this.discordApi.getInvite(this.botToken, inviteCode);
    }

    public DiscordGuild getGuildById(final String guildId) {
        return this.discordApi.getGuild(this.botToken, guildId);
    }

    public DiscordMember getMemberById(final String guildId, final String userId) {
        return this.discordApi.getGuildMember(this.botToken, guildId, userId);
    }

    public MessageAction sendMessage(final String channelId, final MessageEmbed embed) {
        return this.jda.sendMessage(channelId, embed);
    }
}
