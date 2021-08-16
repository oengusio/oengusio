package app.oengus.entity.dto.schedule;

import app.oengus.entity.dto.ScheduleLineDto;
import app.oengus.entity.model.Schedule;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

public class ScheduleDto {

    @NotNull
    private int id;

    @NotNull
    private String marathon;

    @NotNull
    private List<ScheduleLineDto> lines;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMarathon() {
        return marathon;
    }

    public void setMarathon(String marathon) {
        this.marathon = marathon;
    }

    public List<ScheduleLineDto> getLines() {
        return lines;
    }

    public void setLines(List<ScheduleLineDto> lines) {
        this.lines = lines;
    }

    public static ScheduleDto fromModel(final Schedule model) {
        final ScheduleDto dto = new ScheduleDto();

        dto.setId(model.getId());
        dto.setMarathon(model.getMarathon().getId());
        // TODO
        /*dto.setLines(
            model.getLines().stream().map(ScheduleLineDto::fromModel).collect(Collectors.toList())
        );*/

        return dto;
    }
}
