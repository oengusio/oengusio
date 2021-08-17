package app.oengus.service.export;

import app.oengus.entity.dto.ScheduleDto;
import app.oengus.entity.dto.schedule.ScheduleLineDto;
import app.oengus.entity.model.Schedule;
import app.oengus.helper.BeanHelper;
import app.oengus.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
public class ScheduleHelper {

	@Autowired
	private ScheduleService scheduleService;

	// TODO: delegate to models, used for export
	public ScheduleDto getSchedule(final String marathonId, final String zoneId) {
	    // make sure lines is null when copying over to the DTO
		ScheduleDto schedule = new ScheduleDto();
		final Schedule found = this.scheduleService.findByMarathon(marathonId);
		BeanHelper.copyProperties(found, schedule, "lines");
		final List<ScheduleLineDto> scheduleLineDtos = new ArrayList<>();
		for (int i = 0; i < found.getLines().size(); i++) {
			final ScheduleLineDto scheduleLineDto = new ScheduleLineDto();
			BeanHelper.copyProperties(found.getLines().get(i), scheduleLineDto);
			if (i == 0) {
				scheduleLineDto.setTime(
                    found.getMarathon().getStartDate().withSecond(0).withZoneSameInstant(ZoneId.of(zoneId))
                );
			} else {
				scheduleLineDto.setTime(scheduleLineDtos.get(i - 1)
				                                        .getTime()
				                                        .plus(found.getLines().get(i - 1)
				                                                           .getEstimate())
				                                        .plus(found.getLines().get(i - 1)
				                                                           .getSetupTime()));
			}
			scheduleLineDtos.add(scheduleLineDto);
		}
		schedule.setLinesWithTime(scheduleLineDtos);
		return schedule;
	}
}
