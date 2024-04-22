package app.oengus.application;

import app.oengus.application.port.persistence.MarathonPersistencePort;
import app.oengus.application.port.persistence.SchedulePersistencePort;
import app.oengus.domain.marathon.Marathon;
import app.oengus.domain.schedule.Line;
import app.oengus.domain.schedule.Schedule;
import app.oengus.domain.schedule.Ticker;
import app.oengus.exception.schedule.EmptyScheduleException;
import app.oengus.service.mapper.ScheduleMapper;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final SchedulePersistencePort schedulePersistencePort;
    private final MarathonPersistencePort marathonPersistencePort;
    private final ScheduleMapper scheduleMapper;

    ///////////
    // v2 stuff

    public List<Schedule> findAllInfoByMarathon(final String marathonId) {
        return this.schedulePersistencePort.findAllForMarathonWithoutLines(marathonId);
    }

    public List<Schedule> findAllByMarathon(final String marathonId, boolean withCustomData) {
        return this.schedulePersistencePort.findAllForMarathon(marathonId);
    }

    public Optional<Schedule> findByScheduleId(final String marathonId, final int scheduleId, boolean withCustomData) {
        return this.schedulePersistencePort.findByIdForMarathon(marathonId, scheduleId);
    }

    ///////////
    // v1 stuff

    @Nullable
    public Schedule findByMarathon(final String marathonId) {
        /*final var optionalMarathon = this.marathonPersistencePort.findById(marathonId);

        if (optionalMarathon.isEmpty()) {
            return null;
        }*/

        final var optionalSchedule = this.schedulePersistencePort.findFirstForMarathon(marathonId);

        if (optionalSchedule.isEmpty()) {
            return null;
        }

//        final var marathon = optionalMarathon.get();
        final var schedule = optionalSchedule.get();

        /*// TODO: put this in the repository adapter
        if (schedule.getLines() != null && !schedule.getLines().isEmpty()) {
            final List<Line> lines = schedule.getLines();


            lines.get(0).setDate(marathon.getStartDate());

            for (int i = 1; i < lines.size(); i++) {
                final Line previous = lines.get(i - 1);
                lines.get(i).setDate(
                    previous.getDate()
                        .plus(previous.getEstimate())
                        .plus(previous.getSetupTime())
                );
            }
        }*/

        return schedule;
    }

    @Nullable
    public Schedule findByMarathonCustomDataControl(final String marathonId, boolean withCustomData) {
        final Schedule schedule = this.findByMarathon(marathonId);

        if (schedule == null) {
            return null;
        }

//        final var schedule = V1ScheduleDto.fromSchedule(byMarathon);

        // Strip custom data instead of adding them :)
        if (!withCustomData) {
            final List<Line> lines = schedule.getLines();

            // lines can be null, so we check if they are not
            if (lines != null && !lines.isEmpty()) {
                // Remove custom data if not requested
                lines.forEach(
                    (line) -> line.setCustomData(null)
                );
            }
        }

        return schedule;
    }

    public void deleteByMarathon(final String marathonId) {
        this.schedulePersistencePort.deleteAllForMarathon(marathonId);
    }

    public Ticker getForTicker(final String marathonId, boolean withCustomData) throws NotFoundException {
        final Schedule schedule = this.findByMarathonCustomDataControl(marathonId, withCustomData);

        if (schedule == null) {
            throw new NotFoundException("Schedule not found");
        }

        final var marathon = this.marathonPersistencePort.findById(marathonId).orElseThrow(
            () -> new NotFoundException("Marathon not found")
        );
        final ZonedDateTime endDate = marathon.getEndDate();
        final ZonedDateTime now = ZonedDateTime.now(endDate.getZone());
        final List<Line> lines = schedule.getLines();

        if (lines.isEmpty()) {
            throw new EmptyScheduleException("This schedule is empty");
        }

        // fast return if the marathon has ended
        if (now.isAfter(endDate) || now.isEqual(endDate)) {
            return new Ticker(
                lines.get(lines.size() - 1),
                null,
                null
            );
        }

        Line previous = null;
        Line current = null;
        Line next = null;

        for (final Line line : lines) {
            if (now.isEqual(line.getDate()) || now.isAfter(line.getDate())) {
                previous = current;
                current = line;
            } else {
                next = line;
                // we always will find the next one last
                break;
            }
        }

        return new Ticker(
            previous,
            current,
            next
        );
    }

    public void saveOrUpdate(final String marathonId, final Schedule schedule) throws NotFoundException {
        final var marathon = this.marathonPersistencePort.findById(marathonId).orElseThrow(
            () -> new NotFoundException("Marathon not found")
        );

        schedule.setMarathonId(marathonId);
        // TODO: this should be done automatically from now on.
        /*schedule.getLines().forEach(scheduleLine -> {
            scheduleLine.setSchedule(schedule);

            scheduleLine.getRunners().forEach((runner) -> {
                if (runner.getUser() != null) {
                    runner.setRunnerName(null);
                }
            });
        });*/

        this.schedulePersistencePort.save(schedule);

        if (marathon.isScheduleDone()) {
            this.computeEndDate(marathon, schedule);
            this.marathonPersistencePort.save(marathon);
        }
    }

    public void computeEndDate(final Marathon marathon, final Schedule schedule) {
        marathon.setEndDate(marathon.getStartDate());
        schedule.getLines().forEach(scheduleLine -> {
            marathon.setEndDate(
                marathon.getEndDate().plus(scheduleLine.getEstimate()).plus(scheduleLine.getSetupTime()));
        });
    }
}
