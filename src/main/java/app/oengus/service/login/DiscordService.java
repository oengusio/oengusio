package app.oengus.service.login;

import app.oengus.api.DiscordApi;
import app.oengus.api.DiscordOauthApi;
import app.oengus.entity.dto.SyncDto;
import app.oengus.entity.model.User;
import app.oengus.entity.model.api.discord.DiscordUser;
import app.oengus.application.exception.auth.UnknownUserException;
import app.oengus.helper.OauthHelper;
import app.oengus.helper.PrincipalHelper;
import app.oengus.service.repository.UserRepositoryService;
import app.oengus.spring.model.AccessToken;
import app.oengus.spring.model.params.DiscordParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DiscordService {
    private final DiscordParams discordParams;
    private final DiscordApi discordApi;
    private final DiscordOauthApi discordOauthApi;
    private final UserRepositoryService userRepositoryService;

    @Value("${discord.botToken}")
    private String botToken;

    public User login(final String code, final String host) {
        final Map<String, String> oauthParams = OauthHelper.buildOauthMapForLogin(this.discordParams, code, host);
        final AccessToken accessToken = this.discordOauthApi.getAccessToken(oauthParams);
        final DiscordUser discordUser = this.discordApi.getCurrentUser(
                String.join(" ", accessToken.getTokenType(), accessToken.getAccessToken()));

        final User user = this.userRepositoryService.findByDiscordId(discordUser.getId());

        if (user == null) {
            throw new UnknownUserException();
        }

        return user;
    }

    public SyncDto sync(final String code, final String host) throws LoginException {
        final Map<String, String> oauthParams = OauthHelper.buildOauthMapForSync(this.discordParams, code, host);
        final AccessToken accessToken = this.discordOauthApi.getAccessToken(oauthParams);
        final DiscordUser discordUser = this.discordApi.getCurrentUser(
                String.join(" ", accessToken.getTokenType(), accessToken.getAccessToken())
        );
        final User user = this.userRepositoryService.findByDiscordId(discordUser.getId());

        if (user != null && !Objects.equals(user.getId(), PrincipalHelper.getCurrentUser().getId())) {
            throw new LoginException("ACCOUNT_ALREADY_SYNCED");
        }

        return new SyncDto(discordUser.getId(), discordUser.getAsTag());
    }

    public DiscordUser getUser(final String id) {
        return this.discordApi.getUser(this.botToken, id);
    }
}
