package app.oengus.application;

import app.oengus.api.TwitchApi;
import app.oengus.api.TwitchOauthApi;
import app.oengus.application.exception.auth.UnknownUserException;
import app.oengus.application.port.persistence.UserPersistencePort;
import app.oengus.application.port.security.UserSecurityPort;
import app.oengus.domain.OengusUser;
import app.oengus.entity.dto.SyncDto;
import app.oengus.entity.model.api.TwitchUser;
import app.oengus.helper.OauthHelper;
import app.oengus.spring.model.AccessToken;
import app.oengus.spring.model.params.TwitchLoginParams;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TwitchService {
    private final TwitchLoginParams twitchLoginParams;
    private final TwitchOauthApi twitchOauthApi;
    private final TwitchApi twitchApi;
    private final UserPersistencePort userPersistencePort;
    private final UserSecurityPort securityPort;

    public OengusUser login(final String code, final String host) {
        final Map<String, String> oauthParams = OauthHelper.buildOauthMapForLogin(this.twitchLoginParams, code, host);
        final TwitchUser twitchUser = fetchTwitchUser(oauthParams);

        final Optional<OengusUser> user = this.userPersistencePort.findByTwitchId(twitchUser.getId());

        if (user.isEmpty()) {
            throw new UnknownUserException();
        }

        return user.get();
    }

    public SyncDto sync(final String code, final String host) throws LoginException {
        final Map<String, String> oauthParams = OauthHelper.buildOauthMapForSync(this.twitchLoginParams, code, host);
        final TwitchUser twitchUser = fetchTwitchUser(oauthParams);

        final var optionalUser = this.userPersistencePort.findByTwitchId(twitchUser.getId());
        final var authUserId = this.securityPort.getAuthenticatedUserId();

        if (optionalUser.isPresent() && !Objects.equals(optionalUser.get().getId(), authUserId)) {
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
