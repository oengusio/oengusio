package app.oengus.entity.model.api.discord;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DiscordUser {

	private String username;
	private String discriminator;
	private String id;

	public String getUsername() {
		return this.username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getDiscriminator() {
		return this.discriminator;
	}

	public void setDiscriminator(final String discriminator) {
		this.discriminator = discriminator;
	}

	public String getId() {
		return this.id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getAsTag() {
	    return this.getUsername() + '#' + this.getDiscriminator();
    }
}
