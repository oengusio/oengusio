package app.oengus.service.jda;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.receive.ReadonlyMessage;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class JDAService {
    @Value("${discord.botTokenRaw}")
    private String botToken;

    public WebhookClient forChannel(String channelId) {
        return WebhookClient.create(Long.parseLong(channelId), this.botToken);
    }

    public CompletableFuture<ReadonlyMessage> sendMessage(final String channelId, final WebhookEmbed embed) {
        try (WebhookClient client = WebhookClient.create(Long.parseLong(channelId), this.botToken)) {
            return client.send(WebhookMessage.embeds(embed));
        }
    }
}
