package app.oengus.service.login;

import app.oengus.api.TwitchApi;
import app.oengus.api.TwitchOauthApi;
import app.oengus.entity.dto.SyncDto;
import app.oengus.entity.model.User;
import app.oengus.entity.model.api.TwitchUser;
import app.oengus.helper.OauthHelper;
import app.oengus.helper.PrincipalHelper;
import app.oengus.service.repository.UserRepositoryService;
import app.oengus.spring.model.AccessToken;
import app.oengus.spring.model.params.TwitchSyncParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import javax.transaction.Transactional;
import java.util.Map;
import java.util.Objects;

@Service
public class TwitchSyncService {

	@Autowired
	private TwitchSyncParams twitchLoginParams;

	@Autowired
	private TwitchOauthApi twitchOauthApi;

	@Autowired
	private TwitchApi twitchApi;

	@Autowired
	private UserRepositoryService userRepositoryService;

	@Transactional
	public SyncDto sync(final String code) throws LoginException {
		final Map<String, String> oauthParams = OauthHelper.buildOauthMapForSync(this.twitchLoginParams, code);
		final AccessToken accessToken = this.twitchOauthApi.getAccessToken(oauthParams);
		final TwitchUser twitchUser = this.twitchApi.getCurrentUser(
				String.join(" ", StringUtils.capitalize(accessToken.getTokenType()), accessToken.getAccessToken()),
				this.twitchLoginParams.getClientId())
		                                            .getData().get(0);

		final User user = this.userRepositoryService.findByTwitchId(twitchUser.getId());
		if (user != null && !Objects.equals(user.getId(), PrincipalHelper.getCurrentUser().getId())) {
			throw new LoginException("ACCOUNT_ALREADY_SYNCED");
		}
		return new SyncDto(twitchUser.getId(), twitchUser.getLogin());
	}
}
