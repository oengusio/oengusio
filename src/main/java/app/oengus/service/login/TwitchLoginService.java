package app.oengus.service.login;

import app.oengus.api.TwitchApi;
import app.oengus.api.TwitchOauthApi;
import app.oengus.entity.model.User;
import app.oengus.entity.model.api.TwitchUser;
import app.oengus.helper.OauthHelper;
import app.oengus.service.repository.UserRepositoryService;
import app.oengus.spring.model.AccessToken;
import app.oengus.spring.model.Role;
import app.oengus.spring.model.params.TwitchLoginParams;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@Service
public class TwitchLoginService {

	@Autowired
	private TwitchLoginParams twitchLoginParams;

	@Autowired
	private TwitchOauthApi twitchOauthApi;

	@Autowired
	private TwitchApi twitchApi;

	@Autowired
	private UserRepositoryService userRepositoryService;

	@Transactional
	public User login(final String code) throws LoginException {
		final Map<String, String> oauthParams = OauthHelper.buildOauthMapForLogin(this.twitchLoginParams, code);
		final AccessToken accessToken = this.twitchOauthApi.getAccessToken(oauthParams);
		final TwitchUser twitchUser = this.twitchApi.getCurrentUser(
				String.join(" ", StringUtils.capitalize(accessToken.getTokenType()), accessToken.getAccessToken()),
				this.twitchLoginParams.getClientId())
		                                            .getData()
		                                            .get(0);

		User user = this.userRepositoryService.findByTwitchId(twitchUser.getId());
		if (user == null) {
			user = new User();
			user.setRoles(List.of(Role.ROLE_USER));
			user.setEnabled(true);
			user.setUsername(
					StringUtils.substring(twitchUser.getLogin(), 0, 16));
			if (this.userRepositoryService.existsByUsername(user.getUsername())) {
				throw new LoginException("USERNAME_EXISTS");
			}
			if (StringUtils.length(user.getUsername()) < 3) {
				user.setUsername("user" + RandomUtils.nextInt(0, 999999));
			}
			user.setTwitchId(twitchUser.getId());
			user.setTwitchName(twitchUser.getLogin());
			user = this.userRepositoryService.save(user);
		}

		return user;
	}
}
