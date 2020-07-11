package app.oengus.service.twitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!prod")
public class TwitterMockService extends AbstractTwitterService {

	static Logger LOGGER = LoggerFactory.getLogger(TwitterMockService.class);

	@Override
	void send(final String message) {
		LOGGER.info(message);
	}
}
