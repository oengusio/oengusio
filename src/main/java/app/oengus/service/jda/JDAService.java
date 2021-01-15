package app.oengus.service.jda;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.restaction.MessageActionImpl;
import net.dv8tion.jda.internal.utils.config.AuthorizationConfig;
import net.dv8tion.jda.internal.utils.config.MetaConfig;
import net.dv8tion.jda.internal.utils.config.SessionConfig;
import net.dv8tion.jda.internal.utils.config.ThreadingConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;

@Service
public class JDAService {
    private final JDAImpl jda;

    @Autowired
    public JDAService(
        @Value("${discord.botTokenRaw}") final String botToken
    ) {
        System.out.println("JDA SERVICE INIT: " + botToken);

        final AuthorizationConfig authConfig = new AuthorizationConfig(botToken);
        final SessionConfig sessionConfig = SessionConfig.getDefault();
        final ThreadingConfig threadConfig = ThreadingConfig.getDefault();
        final MetaConfig metaConfig = MetaConfig.getDefault();

        threadConfig.setRateLimitPool(Executors.newScheduledThreadPool(5, (r) -> {
            final Thread t = new Thread(r, "dunctebot-rest-thread");
            t.setDaemon(true);
            return t;
        }), true);

        this.jda = new JDAImpl(authConfig, sessionConfig, threadConfig, metaConfig);
    }

    public MessageAction sendMessage(final String channelId, final MessageEmbed embed) {
        final Route.CompiledRoute route = Route.Messages.SEND_MESSAGE.compile(channelId);

        return new MessageActionImpl(jda, route, null).embed(embed);
    }
}
