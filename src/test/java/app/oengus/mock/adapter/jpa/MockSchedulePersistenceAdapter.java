package app.oengus.mock.adapter.jpa;

import app.oengus.application.port.persistence.SchedulePersistencePort;
import app.oengus.domain.schedule.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Profile("test")
@RequiredArgsConstructor
public class MockSchedulePersistenceAdapter implements SchedulePersistencePort {
    @Override
    public Optional<Schedule> findFirstForMarathon(String marathonId) {
        return Optional.empty();
    }

    @Override
    public Optional<Schedule> findByIdForMarathon(String marathonId, int scheduleId) {
        return Optional.empty();
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
        return List.of();
    }

    @Override
    public Schedule save(Schedule schedule) {
        return null;
    }

    @Override
    public void delete(Schedule schedule) {

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
}
