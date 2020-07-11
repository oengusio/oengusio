package app.oengus.spring.model.params;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "discord")
public class DiscordParams extends Oauth2Params {
}
