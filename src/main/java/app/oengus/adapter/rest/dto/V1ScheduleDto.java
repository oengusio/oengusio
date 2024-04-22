package app.oengus.adapter.rest.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class V1ScheduleDto {

    private int id;
    private List<ScheduleLineDto> lines;
    private List<ScheduleLineDto> linesWithTime;
}
