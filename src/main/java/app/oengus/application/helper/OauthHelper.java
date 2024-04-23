package app.oengus.application.helper;

import app.oengus.configuration.params.Oauth2Params;

import java.util.HashMap;
import java.util.Map;

public class OauthHelper {

    public static Map<String, String> buildOauthMapForLogin(final Oauth2Params params, final String code, final String baseUrl) {
        return buildOauthBase(params, code, params.getRedirectUri(), baseUrl);
    }

    public static Map<String, String> buildOauthMapForSync(final Oauth2Params params, final String code, final String baseUrl) {
        return buildOauthBase(params, code, params.getSyncRedirectUri(), baseUrl);
    }

    private static Map<String, String> buildOauthBase(final Oauth2Params params, final String code, final String redirectUri, final String baseUrl) {
        final Map<String, String> map = new HashMap<>();
        map.put("client_id", params.getClientId());
        map.put("client_secret", params.getClientSecret());
        map.put("grant_type", params.getGrantType());
        map.put("code", code);
        map.put("redirect_uri", baseUrl + redirectUri);
        map.put("scope", String.join(" ", params.getScope()));
        return map;
    }

}
