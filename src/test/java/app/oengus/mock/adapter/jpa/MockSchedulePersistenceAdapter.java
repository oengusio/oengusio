package app.oengus.mock.adapter.jpa;

import app.oengus.application.port.persistence.SchedulePersistencePort;
import app.oengus.domain.schedule.Line;
import app.oengus.domain.schedule.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Profile("test")
@RequiredArgsConstructor
public class MockSchedulePersistenceAdapter implements SchedulePersistencePort {
    private final Map<Integer, Schedule> fakeDb = new HashMap<>();
    private final MockMarathonPersistenceAdapter marathonPersistenceAdapter;

    @Override
    public Optional<Schedule> findByIdForMarathon(String marathonId, int scheduleId) {
        return this.fakeDb.values()
            .stream()
            .filter((schedule) -> schedule.getMarathonId().equals(marathonId) && schedule.getId() == scheduleId)
            .findFirst()
            .map(this::setDateOnLines);
    }

    @Override
    public Optional<Schedule> findByIdForMarathonWithoutLines(String marathonId, int scheduleId) {
        return Optional.empty();
    }

    @Override
    public List<Schedule> findAllForMarathon(String marathonId) {
        return List.of();
    }

    @Override
    public List<Schedule> findAllForMarathonWithoutLines(String marathonId) {
        return this.fakeDb.values()
            .stream()
            .filter((schedule) -> schedule.getMarathonId().equals(marathonId))
            .toList();
    }

    @Override
    public Schedule save(Schedule schedule) {
        this.fakeDb.put(schedule.getId(), schedule);

        return schedule;
    }

    @Override
    public void delete(Schedule schedule) {
        this.fakeDb.remove(schedule.getId());
    }

    @Override
    public void deleteAllForMarathon(String marathonId) {

    }

    @Override
    public Optional<Schedule> findBySlugForMarathon(String marathonId, String slug) {
        return Optional.empty();
    }

    @Override
    public boolean existsBySlug(String marathonId, String slug) {
        return false;
    }

    @Override
    public int getScheduleCountForMarathon(String marathonId) {
        return 0;
    }

    private Schedule setDateOnLines(Schedule schedule) {
        final var lines = schedule.getLines();

        if (lines.isEmpty()) {
            return schedule;
        }

        final var marathon = this.marathonPersistenceAdapter.findById(schedule.getMarathonId()).get();
        final var startDate = marathon.getStartDate();

        lines.get(0).setDate(startDate);

        for (int i = 1; i < lines.size(); i++) {
            final Line previous = lines.get(i - 1);

            lines.get(i).setDate(
                previous.getDate()
                    .plus(previous.getEstimate())
                    .plus(previous.getSetupTime())
            );
        }

        return schedule;
    }
}
