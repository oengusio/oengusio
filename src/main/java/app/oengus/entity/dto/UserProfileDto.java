package app.oengus.entity.dto;

import java.util.ArrayList;
import java.util.List;

public class UserProfileDto {

	private String username;
	private String usernameJapanese;
	private Boolean enabled;
	private String twitterName;
	private String twitchName;
	private String speedruncomName;
	private List<UserHistoryDto> history;
	private List<MarathonBasicInfoDto> moderatedMarathons;

	public UserProfileDto() {
		this.history = new ArrayList<>();
		this.moderatedMarathons = new ArrayList<>();
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

	public Boolean getEnabled() {
		return this.enabled;
	}

	public void setEnabled(final Boolean enabled) {
		this.enabled = enabled;
	}

	public String getTwitterName() {
		return this.twitterName;
	}

	public void setTwitterName(final String twitterName) {
		this.twitterName = twitterName;
	}

	public String getTwitchName() {
		return this.twitchName;
	}

	public void setTwitchName(final String twitchName) {
		this.twitchName = twitchName;
	}

	public String getSpeedruncomName() {
		return this.speedruncomName;
	}

	public void setSpeedruncomName(final String speedruncomName) {
		this.speedruncomName = speedruncomName;
	}

	public List<UserHistoryDto> getHistory() {
		return this.history;
	}

	public void setHistory(final List<UserHistoryDto> history) {
		this.history = history;
	}

	public List<MarathonBasicInfoDto> getModeratedMarathons() {
		return this.moderatedMarathons;
	}

	public void setModeratedMarathons(final List<MarathonBasicInfoDto> moderatedMarathons) {
		this.moderatedMarathons = moderatedMarathons;
	}
}
