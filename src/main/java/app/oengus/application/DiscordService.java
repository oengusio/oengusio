package app.oengus.application;

import app.oengus.api.DiscordApi;
import app.oengus.entity.model.api.discord.DiscordGuild;
import app.oengus.entity.model.api.discord.DiscordInvite;
import app.oengus.entity.model.api.discord.DiscordMember;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("discordApiService")
@RequiredArgsConstructor
public class DiscordService {
    private final DiscordApi discordApi;

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
}
