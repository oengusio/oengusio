package app.oengus.entity.dto;

import app.oengus.entity.model.Availability;

public class AvailabilityDto extends Availability {

	private String username;
	private String usernameJapanese;

	public AvailabilityDto(final String username, final String usernameJapanese) {
		this.username = username;
		this.usernameJapanese = usernameJapanese;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getUsernameJapanese() {
		return this.usernameJapanese;
	}

	public void setUsernameJapanese(final String usernameJapanese) {
		this.usernameJapanese = usernameJapanese;
	}
}
