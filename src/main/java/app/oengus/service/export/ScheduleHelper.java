package app.oengus.service.export;

import app.oengus.entity.dto.schedule.ScheduleDto;
import app.oengus.entity.dto.schedule.ScheduleLineDto;
import app.oengus.entity.model.Schedule;
import app.oengus.entity.model.ScheduleLine;
import app.oengus.service.ScheduleService;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
public class ScheduleHelper {

    private final ScheduleService scheduleService;

    public ScheduleHelper(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    // TODO: delegate to models, used for export
    public ScheduleDto getSchedule(final String marathonId, final String zoneId) {
        final Schedule found = this.scheduleService.findByMarathon(marathonId);
        final ScheduleDto dto = ScheduleDto.fromModel(found, false, false);
        final List<ScheduleLineDto> lines = new ArrayList<>();

        for (int i = 0; i < found.getLines().size(); i++) {
            final ScheduleLineDto scheduleLineDto = ScheduleLineDto.fromModel(
                found.getLines().get(i),
                false
            );

            if (i == 0) {
                scheduleLineDto.setTime(
                    found.getMarathon().getStartDate().withSecond(0).withZoneSameInstant(ZoneId.of(zoneId))
                );
            } else {
                final ScheduleLine previous = found.getLines().get(i - 1);

                scheduleLineDto.setTime(
                    lines.get(i - 1)
                        .getTime()
                        .plus(previous.getEstimate())
                        .plus(previous.getSetupTime())
                );
            }

            lines.add(scheduleLineDto);
        }

        dto.setLines(lines);

        return dto;
    }
}
