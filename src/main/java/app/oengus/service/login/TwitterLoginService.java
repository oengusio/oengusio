package app.oengus.service.login;

import app.oengus.api.TwitterOAuthApi;
import app.oengus.entity.constants.SocialPlatform;
import app.oengus.entity.dto.SyncDto;
import app.oengus.entity.model.SocialAccount;
import app.oengus.entity.model.User;
import app.oengus.entity.model.api.twitter.TwitterUser;
import app.oengus.helper.OauthHelper;
import app.oengus.helper.PrincipalHelper;
import app.oengus.service.repository.UserRepositoryService;
import app.oengus.spring.model.AccessToken;
import app.oengus.spring.model.Role;
import app.oengus.spring.model.params.TwitterOAuthParams;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import javax.security.auth.login.LoginException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class TwitterLoginService {
    private final UserRepositoryService userRepositoryService;

    // TODO: move these values to the constructor
    @Value("${twitter.oauth.consumerKey}")
    private String consumerKey;

    @Value("${twitter.oauth.consumerSecret}")
    private String consumerSecret;

    @Value("${twitter.oauth.clientId}")
    private String clientId;

    @Value("${twitter.oauth.clientSecret}")
    private String clientSecret;

    private TwitterFactory twitterFactory;
    private final TwitterOAuthApi twitterOauth;
    private final TwitterOAuthParams twitterParams;

    @Autowired
    public TwitterLoginService(
        TwitterOAuthParams twitterParams, TwitterOAuthApi twitterOauth, UserRepositoryService userRepositoryService
    ) {
        this.userRepositoryService = userRepositoryService;
        this.twitterOauth = twitterOauth;
        this.twitterParams = twitterParams;
    }

    private Twitter getTwitter() {
        // always return a new instance because it has state :/
        return this.getTwitterFactory().getInstance();
    }

    private TwitterFactory getTwitterFactory() {
        if (this.twitterFactory == null) {
            final ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setOAuthConsumerKey(this.consumerKey)
                .setOAuthConsumerSecret(this.consumerSecret)
                .setDebugEnabled(false);

            this.twitterFactory = new TwitterFactory(cb.build());
        }

        return this.twitterFactory;
    }

    public User login(final String code, final String host) throws LoginException {
        final Map<String, String> oauthParams = OauthHelper.buildOauthMapForLogin(this.twitterParams, code, host);
        oauthParams.put("code_verifier", "challenge");

        final AccessToken accessToken = this.twitterOauth.getAccessToken(
            "Basic " + Base64.getEncoder().encodeToString(
                String.join(":", this.twitterParams.getClientId(), this.twitterParams.getClientSecret()).getBytes()
            ),
            oauthParams
        );
        final TwitterUser twitterUser = this.twitterOauth.getCurrentUser(
            String.join(" ", "Bearer", accessToken.getAccessToken())
        ).getData();

        User user = this.userRepositoryService.findByTwitterId(twitterUser.getId());

        if (user == null) {
            user = new User();
            user.setRoles(List.of(Role.ROLE_USER));
            user.setEnabled(true);
            user.setUsername(twitterUser.getName());

            if (this.userRepositoryService.existsByUsername(user.getUsername())) {
                throw new LoginException("USERNAME_EXISTS");
            }

            if (StringUtils.length(user.getUsername()) < 3) {
                user.setUsername("user" + RandomUtils.nextInt(0, 999999));
            }

            final SocialAccount account = new SocialAccount();
            account.setUser(user);
            account.setPlatform(SocialPlatform.TWITTER);
            account.setUsername(twitterUser.getUsername());
            user.setConnections(List.of(account));

            user.setTwitterId(twitterUser.getId());
            user = this.userRepositoryService.save(user);
        }

        return user;
    }

    public SyncDto sync(final String code, final String host) throws LoginException {
        final Map<String, String> oauthParams = OauthHelper.buildOauthMapForSync(this.twitterParams, code, host);
        oauthParams.put("code_verifier", "challenge");

        final AccessToken accessToken = this.twitterOauth.getAccessToken(
            "Basic " + Base64.getEncoder().encodeToString(
                String.join(":", this.twitterParams.getClientId(), this.twitterParams.getClientSecret()).getBytes()
            ),
            oauthParams
        );
        final TwitterUser twitterUser = this.twitterOauth.getCurrentUser(
            String.join(" ", "Bearer", accessToken.getAccessToken())
        ).getData();

        final User user = this.userRepositoryService.findByTwitterId(twitterUser.getId());
        if (user != null && !Objects.equals(user.getId(), PrincipalHelper.getCurrentUser().getId())) {
            throw new LoginException("ACCOUNT_ALREADY_SYNCED");
        }

        return new SyncDto(twitterUser.getId(), twitterUser.getUsername());
    }
}
