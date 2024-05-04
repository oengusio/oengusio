package app.oengus.application.port.persistence;

import app.oengus.domain.schedule.Schedule;

import java.util.List;
import java.util.Optional;

public interface SchedulePersistencePort {
    Optional<Schedule> findFirstForMarathon(String marathonId);

    Optional<Schedule> findByIdForMarathon(String marathonId, int scheduleId);

    Optional<Schedule> findByIdForMarathonWithoutLines(String marathonId, int scheduleId);

    List<Schedule> findAllForMarathon(String marathonId);

    List<Schedule> findAllForMarathonWithoutLines(String marathonId);

    Schedule save(Schedule schedule);

    void delete(Schedule schedule);

    void deleteAllForMarathon(String marathonId);

    Optional<Schedule> findBySlugForMarathon(String marathonId, String slug);

    boolean existsBySlug(String marathonId, String slug);

    int getScheduleCountForMarathon(String marathonId);
}
