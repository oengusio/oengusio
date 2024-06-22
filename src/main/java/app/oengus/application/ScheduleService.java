package app.oengus.application;

import app.oengus.application.port.persistence.MarathonPersistencePort;
import app.oengus.application.port.persistence.SchedulePersistencePort;
import app.oengus.domain.exception.MarathonNotFoundException;
import app.oengus.domain.exception.schedule.EmptyScheduleException;
import app.oengus.domain.marathon.Marathon;
import app.oengus.domain.schedule.Line;
import app.oengus.domain.schedule.Schedule;
import app.oengus.domain.schedule.Ticker;
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
    private final SelectionService selectionService;

    ///////////
    // v2 stuff

    public List<Schedule> findAllInfoByMarathon(final String marathonId) {
        return this.schedulePersistencePort.findAllForMarathonWithoutLines(marathonId);
    }

    public Optional<Schedule> findInfoByScheduleId(final String marathonId, final int scheduleId) {
        return this.schedulePersistencePort.findByIdForMarathonWithoutLines(marathonId, scheduleId);
    }

    public Optional<Schedule> findBySlug(final String marathonId, final String slug, final boolean withCustomData) {
        return this.parseCustomData(
            this.schedulePersistencePort.findBySlugForMarathon(marathonId, slug),
            withCustomData
        );
    }

    // If you don't need the lines, it's better to fetch the info only
    public Optional<Schedule> findByScheduleId(final String marathonId, final int scheduleId, boolean withCustomData) {
        return this.parseCustomData(
            this.schedulePersistencePort.findByIdForMarathon(marathonId, scheduleId),
            withCustomData
        );
    }

    public boolean hasUsedSlug(final String marathonId, final String slug) {
        return this.schedulePersistencePort.existsBySlug(marathonId, slug);
    }

    public void deleteSchedule(Schedule schedule) {
        this.schedulePersistencePort.delete(schedule);
    }

    // NOTE: this always needs a fulls schedule WITH CUSTOM DATA since we are saving it to the database.
    // TODO: remove persist settings for lines in model when we ditch the v1 update api
    public void publishSchedule(Schedule schedule) {
        final var marathon = this.marathonPersistencePort.findById(schedule.getMarathonId())
            .orElseThrow(MarathonNotFoundException::new);

        if (schedule.isPublished()) {
            return;
        }

        schedule.setPublished(true);

        final var newSchedule = this.schedulePersistencePort.save(schedule);

        // We need to tell the marathon that the schedule is done.
        // We check if it is not set because updating the marathon is an expensive db operation.
        if (!marathon.isScheduleDone()) {
            // Copied this from the marathon service (update method)
            // this needs to be consolidated into a single method
            marathon.setScheduleDone(true);

            // TODO: we always need to compute the end date when a published schedule is updated.
            this.computeEndDate(marathon, newSchedule);
            this.selectionService.rejectTodos(marathon.getId());

            marathon.setSelectionDone(true);
            marathon.setCanEditSubmissions(false);

            this.marathonPersistencePort.save(marathon);
        }
    }

    private Optional<Schedule> parseCustomData(final Optional<Schedule> optionalSchedule, final boolean withCustomData) {
        if (optionalSchedule.isEmpty()) {
            return Optional.empty();
        }

        final var schedule = optionalSchedule.get();

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

        return Optional.of(schedule);
    }

    private Ticker scheduleToTicker(final Marathon marathon, final Schedule schedule) {
        final ZonedDateTime endDate = marathon.getEndDate();
        final ZonedDateTime now = ZonedDateTime.now(endDate.getZone());
        final var lines = schedule.getLines();

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

    public Optional<Ticker> findTickerByScheduleId(final String marathonId, final int scheduleId, boolean withCustomData) {
        // TODO: only parse this at the end when we finish parsing the ticker?
        return this.parseCustomData(
            this.schedulePersistencePort.findByIdForMarathon(marathonId, scheduleId), withCustomData
        ).map((schedule) -> {
            if (schedule.getLines().isEmpty()) {
                throw new EmptyScheduleException("This schedule is empty");
            }

            final var marathon = this.marathonPersistencePort.findById(marathonId)
                .orElseThrow(MarathonNotFoundException::new);

            return this.scheduleToTicker(marathon, schedule);
        });
    }

    ///////////
    // v1 stuff

    @Nullable
    @Deprecated // Multi schedule soontm so this is redundant or needs a rename
    public Schedule findByMarathon(final String marathonId) {
        return this.schedulePersistencePort.findFirstForMarathon(marathonId).orElse(null);

    }

    @Deprecated // Multi schedule soontm so this is redundant when the v1 api gets nuked
    public Optional<Schedule> findFirstByMarathon(final String marathonId) {
        return this.schedulePersistencePort.findFirstForMarathon(marathonId);

    }

    @Nullable
    public Schedule findByMarathonCustomDataControl(final String marathonId, boolean withCustomData) {
        final Schedule schedule = this.findByMarathon(marathonId);

        if (schedule == null) {
            return null;
        }

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

        if (schedule.getLines().isEmpty()) {
            throw new EmptyScheduleException("This schedule is empty");
        }

        final var marathon = this.marathonPersistencePort.findById(marathonId).orElseThrow(
            () -> new NotFoundException("Marathon not found")
        );
        final ZonedDateTime endDate = marathon.getEndDate();
        final ZonedDateTime now = ZonedDateTime.now(endDate.getZone());

        return this.scheduleToTicker(marathon, schedule);
    }

    // TODO: create method that checks if a user has the maximum number of schedules
    //  Free users: 1 schedule
    //  Supporters: 4 schedules (Hopefully 4 will be plenty for the coming years for ESA)

    public Schedule saveOrUpdate(final String marathonId, final Schedule schedule) {
        final var marathon = this.marathonPersistencePort.findById(marathonId)
            .orElseThrow(MarathonNotFoundException::new);

        schedule.setMarathonId(marathonId);

        final var saved = this.schedulePersistencePort.save(schedule);

        if (marathon.isScheduleDone()) {
            // TODO: this seems a little broken??
            this.computeEndDate(marathon, saved);
            this.marathonPersistencePort.save(marathon);
        }

        return saved;
    }

    public Schedule saveOrUpdateV1(final String marathonId, final Schedule schedule) {
        final var marathon = this.marathonPersistencePort.findById(marathonId)
            .orElseThrow(MarathonNotFoundException::new);
        final var scheduleDone = marathon.isScheduleDone();

        schedule.setMarathonId(marathonId);
        schedule.setPublished(scheduleDone);

        final var saved = this.schedulePersistencePort.save(schedule);

        if (scheduleDone) {
            // TODO: this seems a little broken??
            this.computeEndDate(marathon, saved);
            this.marathonPersistencePort.save(marathon);
        }

        return saved;
    }

    public void computeEndDate(final Marathon marathon, final Schedule schedule) {
        marathon.setEndDate(marathon.getStartDate());

        // TODO: there is probably a nicer way to do this with the stream api
        //  Maybe reduce?
        schedule.getLines().forEach(scheduleLine ->
            marathon.setEndDate(
                marathon.getEndDate()
                    .plus(scheduleLine.getEstimate())
                    .plus(scheduleLine.getSetupTime())
            )
        );
    }
}
