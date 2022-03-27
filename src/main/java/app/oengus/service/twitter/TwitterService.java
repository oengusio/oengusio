package app.oengus.service.twitter;

import io.sentry.Sentry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import twitter4j.Twitter;
import twitter4j.TwitterException;

@Service
@Profile("prod")
public class TwitterService extends AbstractTwitterService {

	static Logger LOGGER = LoggerFactory.getLogger(TwitterService.class);

	private final Twitter twitter;

    public TwitterService(Twitter twitter) {
        this.twitter = twitter;
    }

    @Override
	public void send(final String message) {
		try {
			this.twitter.updateStatus(message);
		} catch (final TwitterException e) {
            Sentry.captureException(e);
			LOGGER.error("Failed to update twitter status", e);
		}
	}
}
