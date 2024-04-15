package app.oengus.application.port.persistence;

import app.oengus.domain.marathon.Marathon;
import app.oengus.domain.OengusUser;
import app.oengus.domain.marathon.MarathonStats;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface MarathonPersistencePort {
    Optional<Marathon> findById(String marathonId);

    Marathon save(Marathon marathon);

    void delete(Marathon marathon);

    boolean existsById(String marathonId);

    List<Marathon> findLive();

    List<Marathon> findNextUp();

    List<Marathon> findSubmissionsOpen();

    List<Marathon> findActiveModeratedBy(int userId);

    List<Marathon> findAllModeratedBy(int userId);

    List<Marathon> findBetween(ZonedDateTime start, ZonedDateTime end);

    default List<Marathon> findAllModeratedBy(OengusUser user) {
        return this.findAllModeratedBy(user.getId());
    }

    void markSubmissionsOpen(Marathon marathon);

    void markSubmissionsClosed(Marathon marathon);

    Optional<MarathonStats> findStatsById(String marathonId);

    List<Marathon> findNotClearedBefore(ZonedDateTime date);

    void clear(Marathon marathon);

    List<Marathon> findFutureWithScheduledSubmissions();
}
