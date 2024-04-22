package app.oengus.entity.dto;

import app.oengus.adapter.jpa.entity.ScheduleEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class V1ScheduleDto {

    private int id;
    private List<ScheduleLineDto> lines;
    private List<ScheduleLineDto> linesWithTime;

    @Deprecated(forRemoval = true)
    public static V1ScheduleDto fromSchedule(ScheduleEntity schedule) {
        final V1ScheduleDto dto = new V1ScheduleDto();

        dto.setId(schedule.getId());
        dto.setLines(
            schedule.getLines().stream()
                .map(ScheduleLineDto::fromLine)
                .toList()
        );

        return dto;
    }
}
