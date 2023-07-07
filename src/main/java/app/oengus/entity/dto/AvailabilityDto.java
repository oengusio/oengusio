package app.oengus.entity.dto;

import app.oengus.entity.model.Availability;
import app.oengus.entity.model.User;

import static app.oengus.helper.StringHelper.getUserDisplay;

public class AvailabilityDto extends Availability {

	private String username;

    public AvailabilityDto(User user) {
        this.username = getUserDisplay(user);
    }

	public String getUsername() {
		return this.username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}
}
