package app.oengus.adapter.rest.dto.v2.schedule.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@Schema(description = "Data for creating or updating a schedule")
public class ScheduleUpdateRequestDto {
    public static final String SCHEDULE_NAME_REGEX = "^[a-zA-Z0-9_\\- ]+$";
    public static final String SCHEDULE_SLUG_REGEX = "^[a-z0-9_\\-]+$";

    // TODO: special update request that allows for nullable fields
    @NotNull
    @NotBlank
    @Size(min = 1, max = 50)
    @Pattern(regexp = SCHEDULE_NAME_REGEX)
    @Schema(description = "The name of this schedule, as displayed in the UI.", example = "My cool schedule")
    private String name;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 255)
    @Pattern(regexp = SCHEDULE_SLUG_REGEX)
    @Schema(description = "This is the 'slug' of the schedule, displayed in the url.", example = "stream-1")
    private String slug;
}
