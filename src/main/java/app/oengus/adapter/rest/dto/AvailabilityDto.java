package app.oengus.adapter.rest.dto;

import app.oengus.domain.OengusUser;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

import static app.oengus.application.helper.StringHelper.getUserDisplay;

@Getter
@Setter
public class AvailabilityDto {
	private String username;
    private ZonedDateTime from;
    private ZonedDateTime to;

    public AvailabilityDto(OengusUser user) {
        this.username = getUserDisplay(user);
    }
}
