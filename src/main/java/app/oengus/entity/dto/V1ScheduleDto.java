package app.oengus.entity.dto;

import app.oengus.entity.model.Schedule;

import java.util.List;

public class V1ScheduleDto {

    private int id;
    private List<ScheduleLineDto> lines;
	private List<ScheduleLineDto> linesWithTime;

    private V1ScheduleDto() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<ScheduleLineDto> getLines() {
        return lines;
    }

    public void setLines(List<ScheduleLineDto> lines) {
        this.lines = lines;
    }

    public List<ScheduleLineDto> getLinesWithTime() {
		return linesWithTime;
	}

	public void setLinesWithTime(List<ScheduleLineDto> linesWithTime) {
		this.linesWithTime = linesWithTime;
	}

    public static V1ScheduleDto fromSchedule(Schedule schedule) {
        final V1ScheduleDto dto = new V1ScheduleDto();

        dto.setId(schedule.getId());

        return dto;
    }
}
