package app.oengus.service;

import app.oengus.api.DiscordApi;
import app.oengus.entity.model.api.discord.DiscordGuild;
import app.oengus.entity.model.api.discord.DiscordInvite;
import app.oengus.entity.model.api.discord.DiscordMember;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DiscordApiService {
    private final DiscordApi discordApi;
    private final String botToken;

    public DiscordApiService(DiscordApi discordApi, @Value("${discord.botToken}") String botToken) {
        this.discordApi = discordApi;
        this.botToken = botToken;
    }

    public DiscordInvite fetchInvite(final String inviteCode) {
        return this.discordApi.getInvite(this.botToken, inviteCode);
    }

    public DiscordGuild getGuildById(final String guildId) {
        return this.discordApi.getGuild(this.botToken, guildId);
    }

    public DiscordMember getMemberById(final String guildId, final String userId) {
        return this.discordApi.getGuildMember(this.botToken, guildId, userId);
    }
}
