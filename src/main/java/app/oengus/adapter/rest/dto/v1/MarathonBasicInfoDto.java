package app.oengus.adapter.rest.dto.v1;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class MarathonBasicInfoDto {
    private String id;
    private String name;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private ZonedDateTime submissionsEndDate;
    private boolean onsite;
    private boolean isPrivate;
    private String location;
    private String country;
    private String language;
}
