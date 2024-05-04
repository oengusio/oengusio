package app.oengus.adapter.rest.dto.v2.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Basic schedule information")
public class ScheduleInfoDto {
    @Schema(description = "The unique id of the schedule")
    private int id;
    @Schema(description = "Marathon that this schedule belongs to")
    private String marathonId;
    @Schema(description = "The name of this schedule")
    private String name;
    @Schema(description = "The slug of this schedule, displayed in the url.")
    private String slug;
    @Schema(description = "True if this schedule is published and should be shown in the UI. Unpublished schedule should be filtered out when not displayed on the admin page.")
    private boolean published = true;
}
