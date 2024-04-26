package app.oengus.adapter.rest.dto.v2.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema
public class ScheduleDto {
    @Schema(description = "The unique id of the schedule")
    private int id;

    @Schema(description = "Marathon that this schedule belongs to")
    private String marathonId;

    @Schema(description = "The name of this schedule, null if not set")
    private String name;

    @Schema(description = "The slug of this schedule, displayed in the url.")
    private String slug;

    @Schema(description = "The lines of this schedule, in order")
    private List<LineDto> lines;
}
