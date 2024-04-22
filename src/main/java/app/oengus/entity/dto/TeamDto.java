package app.oengus.entity.dto;

import app.oengus.domain.OengusUser;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
public class TeamDto {

    @NotNull
    @NotBlank
    @Size(max = 100)
    private String name;

    private String description;

    @Size(min = 1)
    private int teamSize;

    private boolean applicationsOpen;
    private ZonedDateTime applicationOpenDate;
    private ZonedDateTime applicationCloseDate;

    @NotNull
    private List<OengusUser> leaders; // TODO: just need user ids
}
