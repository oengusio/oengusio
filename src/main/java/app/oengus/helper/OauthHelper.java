package app.oengus.helper;

import app.oengus.spring.model.params.Oauth2Params;

import java.util.HashMap;
import java.util.Map;

public class OauthHelper {

	public static Map<String, String> buildOauthMapForLogin(final Oauth2Params params, final String code) {
		final Map<String, String> map = new HashMap<>();
		map.put("client_id", params.getClientId());
		map.put("client_secret", params.getClientSecret());
		map.put("grant_type", params.getGrantType());
		map.put("code", code);
		map.put("redirect_uri", params.getRedirectUri());
		map.put("scope", String.join(" ", params.getScope()));
		return map;
	}

	public static Map<String, String> buildOauthMapForSync(final Oauth2Params params, final String code) {
		final Map<String, String> map = new HashMap<>();
		map.put("client_id", params.getClientId());
		map.put("client_secret", params.getClientSecret());
		map.put("grant_type", params.getGrantType());
		map.put("code", code);
		map.put("redirect_uri", params.getSyncRedirectUri());
		map.put("scope", String.join(" ", params.getScope()));
		return map;
	}

}
