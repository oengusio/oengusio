package app.oengus.service;

import app.oengus.api.DiscordApi;
import app.oengus.entity.model.api.discord.DiscordGuild;
import app.oengus.entity.model.api.discord.DiscordInvite;
import app.oengus.entity.model.api.discord.DiscordMember;
import app.oengus.service.jda.JDAService;
import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.receive.ReadonlyMessage;
import club.minnced.discord.webhook.send.WebhookEmbed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

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

    public WebhookClient forChannel(String channelId) {
        return this.jda.forChannel(channelId);
    }

    public CompletableFuture<ReadonlyMessage> sendMessage(final String channelId, final WebhookEmbed embed) {
        return this.jda.sendMessage(channelId, embed);
    }
}
