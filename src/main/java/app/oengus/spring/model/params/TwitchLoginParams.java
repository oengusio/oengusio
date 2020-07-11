package app.oengus.spring.model.params;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "twitch.login")
public class TwitchLoginParams extends Oauth2Params {
}
