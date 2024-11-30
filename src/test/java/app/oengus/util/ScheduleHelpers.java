package app.oengus.util;

import app.oengus.application.port.persistence.SchedulePersistencePort;
import app.oengus.domain.schedule.Line;
import app.oengus.domain.schedule.Schedule;
import app.oengus.factory.schedule.LineFactory;
import app.oengus.factory.schedule.ScheduleFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleHelpers {
    private final ScheduleFactory scheduleFactory;
    private final LineFactory lineFactory;

    public Schedule createEmptySchedule() {
        return this.scheduleFactory.getObject();
    }

    public Schedule createSchedule(String marathonId) {
        final var schedule = this.scheduleFactory.getObject();

        schedule.setMarathonId(marathonId);

        schedule.setLines(
            this.createLines(schedule.getId(), 10)
        );

        return schedule;
    }

    public List<Line> createLines(int scheduleId, int lineCount) {
        final List<Line> lines = new ArrayList<>();

        for (int i = 0; i < lineCount; i++) {
            lines.add(this.createLine(scheduleId));
        }

        return lines;
    }

    public Line createLine(int scheduleId) {
        return this.lineFactory.getWithScheduleId(scheduleId);
    }

}
