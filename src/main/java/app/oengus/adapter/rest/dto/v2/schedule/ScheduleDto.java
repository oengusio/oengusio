package app.oengus.adapter.rest.dto.v2.schedule;

import app.oengus.adapter.jpa.entity.ScheduleEntity;
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
    @Schema(description = "The lines of this schedule, in order")
    private List<LineDto> lines;

    @Deprecated(forRemoval = true)
    public static ScheduleDto fromSchedule(ScheduleEntity schedule, boolean withCustomData) {
        final ScheduleDto dto = new ScheduleDto();

        dto.setId(schedule.getId());
        dto.setMarathonId(schedule.getMarathon().getId());
        dto.setLines(
            schedule.getLines()
                .stream()
                .map((line) -> LineDto.fromLine(line, withCustomData))
                .toList()
        );

        final List<LineDto> lines = dto.getLines();

        if (!lines.isEmpty()) {
            lines.get(0).setDate(schedule.getMarathon().getStartDate());

            for (int i = 1; i < lines.size(); i++) {
                final LineDto previous = lines.get(i - 1);
                lines.get(i).setDate(
                    previous.getDate()
                        .plus(previous.getEstimate())
                        .plus(previous.getSetupTime())
                );
            }
        }

        return dto;
    }
}
