package app.oengus.dao;

import app.oengus.entity.model.Marathon;
import app.oengus.adapter.jpa.entity.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface MarathonRepository extends JpaRepository<Marathon, String> {

    @Query(value = "SELECT m from Marathon m WHERE m.startDate > current_timestamp AND m.isPrivate = FALSE " +
        "ORDER BY m.startDate ASC")
    List<Marathon> findNext(Pageable pageable);

    @Query(value = "SELECT m.name FROM Marathon m WHERE m.id = :id")
    String getNameById(@Param("id") String id);

    default List<Marathon> findNext() {
        return this.findNext(PageRequest.of(0, 5));
    }

    @Query(value =
        "SELECT m from Marathon m WHERE m.startDate > current_timestamp AND m.submitsOpen = TRUE " +
            "AND m.isPrivate = FALSE ORDER BY COALESCE(m.submissionsEndDate, m.startDate) ASC")
    List<Marathon> findBySubmitsOpenTrue();

    @Query(value =
        "SELECT m from Marathon m WHERE m.startDate < current_timestamp AND m.endDate > current_timestamp " +
            "AND m.scheduleDone = TRUE AND m.isPrivate = FALSE ORDER BY m.startDate ASC")
    List<Marathon> findLive();

    @Query(value =
        "SELECT m from Marathon m WHERE (m.startDate > :start AND m.endDate < :end " +
            "OR m.startDate < :start AND m.endDate > :end " +
            "OR m.startDate < :start AND m.endDate > :start AND m.endDate < :end " +
            "OR m.startDate > :start AND m.startDate < :end AND m.endDate > :end)" +
            "AND m.isPrivate = FALSE ORDER BY m.startDate ASC")
    List<Marathon> findBetween(@Param("start") ZonedDateTime start, @Param("end") ZonedDateTime end);

    List<Marathon> findByClearedFalseAndEndDateBefore(ZonedDateTime endDate);

    @Query(value =
        "SELECT DISTINCT m from Marathon m " +
            "LEFT JOIN m.moderators u " +
            "WHERE m.endDate > current_timestamp " +
            "AND (m.creator = :user OR u = :user)" +
            "ORDER BY m.startDate ASC")
    List<Marathon> findActiveMarathonsByCreatorOrModerator(@Param("user") User user);

    @Query(value =
        "SELECT DISTINCT m from Marathon m " +
            "LEFT JOIN m.moderators u " +
            "WHERE (m.creator = :user OR u = :user)" +
            "ORDER BY m.startDate ASC")
    List<Marathon> findAllMarathonsByCreatorOrModerator(@Param("user") User user);

    @Modifying
    @Query("UPDATE Marathon m SET m.cleared = true WHERE m = :marathon")
    void clearMarathon(@Param("marathon") Marathon marathon);

    @Query(value = "SELECT m from Marathon m WHERE m.submissionsEndDate > current_timestamp " +
        "ORDER BY m.submissionsStartDate ASC")
    List<Marathon> findFutureMarathonsWithScheduledSubmissions();

    @Query(value = "SELECT m from Marathon m WHERE m.startDate > current_timestamp AND m.scheduleDone = TRUE " +
        "ORDER BY m.startDate ASC")
    List<Marathon> findFutureMarathonsWithScheduleDone();

    @Modifying
    @Query("UPDATE Marathon m SET m.submitsOpen = true, m.canEditSubmissions = true WHERE m = :marathon")
    void openSubmissions(@Param("marathon") Marathon marathon);

    @Modifying
    @Query("UPDATE Marathon m SET m.submitsOpen = false WHERE m = :marathon")
    void closeSubmissions(@Param("marathon") Marathon marathon);

    @Query("SELECT COUNT(c.id) AS submissionCount," +
        "COUNT(DISTINCT c.game.submission.user) AS runnerCount," +
        "SUM(c.estimate) AS totalLength," +
        "AVG(c.estimate) AS averageEstimate " +
        "FROM Category c WHERE c.game.submission.marathon = :marathon")
    Optional<Map<String, Object>> findStats(@Param("marathon") Marathon marathon);

}
