package app.oengus.service.login;

import app.oengus.entity.dto.SyncDto;
import app.oengus.entity.model.User;
import app.oengus.exception.OengusBusinessException;
import app.oengus.helper.PrincipalHelper;
import app.oengus.service.repository.UserRepositoryService;
import app.oengus.spring.model.Role;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import javax.security.auth.login.LoginException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
public class TwitterLoginService {

	@Autowired
	private UserRepositoryService userRepositoryService;

	@Value("${twitter.loginCallback}")
	private String loginCallback;

	@Value("${twitter.syncCallback}")
	private String syncCallback;

	TwitterFactory twitterFactory = new TwitterFactory();

	public String generateAuthUrlForLogin() {
		try {
			final RequestToken requestToken =
					this.twitterFactory.getInstance().getOAuthRequestToken(this.loginCallback);
			return requestToken.getAuthenticationURL();
		} catch (final TwitterException e) {
			throw new OengusBusinessException("TWITTER_ERROR");
		}
	}

	public String generateAuthUrlForSync() {
		try {
			final RequestToken requestToken =
					this.twitterFactory.getInstance().getOAuthRequestToken(this.syncCallback);
			return requestToken.getAuthenticationURL();
		} catch (final TwitterException e) {
			throw new OengusBusinessException("TWITTER_ERROR");
		}
	}

	@Transactional
	public User login(final String oauthToken, final String oauthVerifier) throws LoginException {
		try {
			final AccessToken accessToken =
					this.twitterFactory.getInstance()
					                   .getOAuthAccessToken(new RequestToken(oauthToken, ""), oauthVerifier);
			final twitter4j.User twitterUser = this.twitterFactory.getInstance(accessToken).verifyCredentials();
			User user = this.userRepositoryService.findByTwitterId(Long.toString(twitterUser.getId()));
			if (user == null) {
				user = new User();
				user.setRoles(List.of(Role.ROLE_USER));
				user.setEnabled(true);
				user.setUsername(twitterUser.getScreenName());
				if (this.userRepositoryService.existsByUsername(user.getUsername())) {
					throw new LoginException("USERNAME_EXISTS");
				}
				if (StringUtils.length(user.getUsername()) < 3) {
					user.setUsername("user" + RandomUtils.nextInt(0, 999999));
				}
				user.setTwitterId(Long.toString(twitterUser.getId()));
				user.setTwitterName(twitterUser.getScreenName());
				user = this.userRepositoryService.save(user);
			}

			return user;
		} catch (final TwitterException e) {
			throw new OengusBusinessException("TWITTER_ERROR");
		}
	}

	public SyncDto sync(final String oauthToken, final String oauthVerifier) throws LoginException {
		try {
			final AccessToken accessToken =
					TwitterFactory.getSingleton().getOAuthAccessToken(new RequestToken(oauthToken, ""), oauthVerifier);
			final twitter4j.User twitterUser = this.twitterFactory.getInstance(accessToken).verifyCredentials();
			final User user = this.userRepositoryService.findByTwitterId(Long.toString(twitterUser.getId()));
			if (user != null && !Objects.equals(user.getId(), PrincipalHelper.getCurrentUser().getId())) {
				throw new LoginException("ACCOUNT_ALREADY_SYNCED");
			}

			return new SyncDto(Long.toString(twitterUser.getId()), twitterUser.getScreenName());
		} catch (final TwitterException e) {
			throw new OengusBusinessException("TWITTER_ERROR");
		}
	}

}
