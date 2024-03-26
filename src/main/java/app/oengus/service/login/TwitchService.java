package app.oengus.service.login;

import app.oengus.api.TwitchApi;
import app.oengus.api.TwitchOauthApi;
import app.oengus.entity.dto.SyncDto;
import app.oengus.entity.model.User;
import app.oengus.entity.model.api.TwitchUser;
import app.oengus.application.exception.auth.UnknownUserException;
import app.oengus.helper.OauthHelper;
import app.oengus.helper.PrincipalHelper;
import app.oengus.service.repository.UserRepositoryService;
import app.oengus.spring.model.AccessToken;
import app.oengus.spring.model.params.TwitchLoginParams;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TwitchService {
    private final TwitchLoginParams twitchLoginParams;
    private final TwitchOauthApi twitchOauthApi;
    private final TwitchApi twitchApi;
    private final UserRepositoryService userRepositoryService;

    public User login(final String code, final String host) {
        final Map<String, String> oauthParams = OauthHelper.buildOauthMapForLogin(this.twitchLoginParams, code, host);
        final TwitchUser twitchUser = fetchTwitchUser(oauthParams);

        final User user = this.userRepositoryService.findByTwitchId(twitchUser.getId());

        if (user == null) {
            throw new UnknownUserException();
        }

        return user;
    }

    @Transactional
    public SyncDto sync(final String code, final String host) throws LoginException {
        final Map<String, String> oauthParams = OauthHelper.buildOauthMapForSync(this.twitchLoginParams, code, host);
        final TwitchUser twitchUser = fetchTwitchUser(oauthParams);

        final User user = this.userRepositoryService.findByTwitchId(twitchUser.getId());

        if (user != null && !Objects.equals(user.getId(), PrincipalHelper.getCurrentUser().getId())) {
            throw new LoginException("ACCOUNT_ALREADY_SYNCED");
        }

        return new SyncDto(twitchUser.getId(), twitchUser.getLogin());
    }

    private TwitchUser fetchTwitchUser(Map<String, String> oauthParams) {
        final AccessToken accessToken = this.twitchOauthApi.getAccessToken(oauthParams);
        final List<TwitchUser> foundUsers = this.twitchApi.getCurrentUser(
                String.join(" ", StringUtils.capitalize(accessToken.getTokenType()), accessToken.getAccessToken()),
                this.twitchLoginParams.getClientId()
        ).getData();

        if (foundUsers.isEmpty()) {
            throw new UnknownUserException();
        }

        return foundUsers.get(0);
    }
}
