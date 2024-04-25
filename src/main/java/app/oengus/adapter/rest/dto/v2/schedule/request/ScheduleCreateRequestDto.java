package app.oengus.adapter.rest.dto.v2.schedule.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.MatchesPattern;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Schema(description = "Data for creating a new schedule")
public class ScheduleCreateRequestDto {
    public static final String SCHEDULE_NAME_REGEX = "^[a-zA-Z0-9_\\- ]*$";

    @NotNull
    @NotBlank
    @Size(min = 1, max = 50)
    @MatchesPattern(SCHEDULE_NAME_REGEX)
    @Schema(description = "The name of this schedule, as displayed in the UI.")
    private String name;
}
