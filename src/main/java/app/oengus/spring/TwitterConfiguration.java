package app.oengus.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@Configuration
@Profile("prod")
public class TwitterConfiguration {

	@Value("${twitter.oauth.consumerKey}")
	private String consumerKey;

	@Value("${twitter.oauth.consumerSecret}")
	private String consumerSecret;

	@Value("${twitter.oauth.accessToken}")
	private String accessToken;

	@Value("${twitter.oauth.accessTokenSecret}")
	private String accessTokenSecret;

	@Bean
	public Twitter twitter() {
		final ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setOAuthConsumerKey(this.consumerKey)
		  .setOAuthConsumerSecret(this.consumerSecret)
		  .setOAuthAccessToken(this.accessToken)
		  .setOAuthAccessTokenSecret(this.accessTokenSecret);
		final TwitterFactory tf = new TwitterFactory(cb.build());
		return tf.getInstance();
	}

}
