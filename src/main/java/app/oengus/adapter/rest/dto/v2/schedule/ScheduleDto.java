package app.oengus.adapter.rest.dto.v2.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "A single schedule, with all lines")
public class ScheduleDto extends ScheduleInfoDto {
    @Schema(description = "The lines of this schedule, in order")
    private List<LineDto> lines;
}
