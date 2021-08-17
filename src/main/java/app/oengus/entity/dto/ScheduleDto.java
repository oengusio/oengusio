package app.oengus.entity.dto;

import app.oengus.entity.dto.schedule.ScheduleLineDto;
import app.oengus.entity.model.Schedule;

import java.util.List;

public class ScheduleDto extends Schedule {

	private List<ScheduleLineDto> linesWithTime;

	public List<ScheduleLineDto> getLinesWithTime() {
		return linesWithTime;
	}

	public void setLinesWithTime(List<ScheduleLineDto> linesWithTime) {
		this.linesWithTime = linesWithTime;
	}
}
