package app.oengus.entity.dto.schedule;

import app.oengus.entity.dto.ScheduleLineDto;
import app.oengus.entity.model.Schedule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@ApiModel(description = "The model returned for the schedule")
public class ScheduleDto {

    @NotNull
    @ApiModelProperty(value = "The unique id of the schedule (automatically generated), set to -1 when creating a schedule")
    private int id;

    @NotNull
    @ApiModelProperty(required = true, value = "The id of the marathon that this schedule belongs to")
    private String marathon;

    @NotNull
    @ApiModelProperty(required = true, value = "The lines of the schedule, these are in order of appearance")
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
