package app.oengus.spring.model.params;

import java.util.List;

public abstract class Params {

	private String clientId;
	private String clientSecret;
	private String grantType;
	private String redirectUri;
	private String syncRedirectUri;
	private List<String> scope;

	public String getClientId() {
		return this.clientId;
	}

	public void setClientId(final String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return this.clientSecret;
	}

	public void setClientSecret(final String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getGrantType() {
		return this.grantType;
	}

	public void setGrantType(final String grantType) {
		this.grantType = grantType;
	}

	public String getRedirectUri() {
		return this.redirectUri;
	}

	public void setRedirectUri(final String redirectUri) {
		this.redirectUri = redirectUri;
	}

	public List<String> getScope() {
		return this.scope;
	}

	public void setScope(final List<String> scope) {
		this.scope = scope;
	}

	public String getSyncRedirectUri() {
		return this.syncRedirectUri;
	}

	public void setSyncRedirectUri(final String syncRedirectUri) {
		this.syncRedirectUri = syncRedirectUri;
	}
}
