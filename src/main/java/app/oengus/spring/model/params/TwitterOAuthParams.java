package app.oengus.spring.model.params;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "twitter.oauth")
public class TwitterOAuthParams extends Oauth2Params {
}
