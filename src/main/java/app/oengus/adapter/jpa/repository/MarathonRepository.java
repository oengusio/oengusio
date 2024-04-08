package app.oengus.adapter.jpa.repository;

import app.oengus.adapter.jpa.entity.MarathonEntity;
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
public interface MarathonRepository extends JpaRepository<MarathonEntity, String> {

    @Query(value = "SELECT m from MarathonEntity m WHERE m.startDate > current_timestamp AND m.isPrivate = FALSE " +
        "ORDER BY m.startDate ASC")
    List<MarathonEntity> findNext(Pageable pageable);

    @Query(value = "SELECT m.name FROM MarathonEntity m WHERE m.id = :id")
    String getNameById(@Param("id") String id);

    default List<MarathonEntity> findNext() {
        return this.findNext(PageRequest.of(0, 5));
    }

    @Query(value =
        "SELECT m from MarathonEntity m WHERE m.startDate > current_timestamp AND m.submitsOpen = TRUE " +
            "AND m.isPrivate = FALSE ORDER BY COALESCE(m.submissionsEndDate, m.startDate) ASC")
    List<MarathonEntity> findBySubmitsOpenTrue();

    @Query(value =
        "SELECT m from MarathonEntity m WHERE m.startDate < current_timestamp AND m.endDate > current_timestamp " +
            "AND m.scheduleDone = TRUE AND m.isPrivate = FALSE ORDER BY m.startDate ASC")
    List<MarathonEntity> findLive();

    @Query(value =
        "SELECT m from MarathonEntity m WHERE (m.startDate > :start AND m.endDate < :end " +
            "OR m.startDate < :start AND m.endDate > :end " +
            "OR m.startDate < :start AND m.endDate > :start AND m.endDate < :end " +
            "OR m.startDate > :start AND m.startDate < :end AND m.endDate > :end)" +
            "AND m.isPrivate = FALSE ORDER BY m.startDate ASC")
    List<MarathonEntity> findBetween(@Param("start") ZonedDateTime start, @Param("end") ZonedDateTime end);

    List<MarathonEntity> findByClearedFalseAndEndDateBefore(ZonedDateTime endDate);

    @Query(value =
        "SELECT DISTINCT m from MarathonEntity m " +
            "LEFT JOIN m.moderators u " +
            "WHERE m.endDate > current_timestamp " +
            "AND (m.creator = :user OR u = :user)" +
            "ORDER BY m.startDate ASC")
    List<MarathonEntity> findActiveMarathonsByCreatorOrModerator(@Param("user") User user);

    @Query(value =
        "SELECT DISTINCT m from MarathonEntity m " +
            "LEFT JOIN m.moderators u " +
            "WHERE (m.creator = :user OR u = :user)" +
            "ORDER BY m.startDate ASC")
    List<MarathonEntity> findAllMarathonsByCreatorOrModerator(@Param("user") User user);

    @Modifying
    @Query("UPDATE MarathonEntity m SET m.cleared = true WHERE m = :marathon")
    void clearMarathon(@Param("marathon") MarathonEntity marathon);

    @Query(value = "SELECT m from MarathonEntity m WHERE m.submissionsEndDate > current_timestamp " +
        "ORDER BY m.submissionsStartDate ASC")
    List<MarathonEntity> findFutureMarathonsWithScheduledSubmissions();

    @Query(value = "SELECT m from MarathonEntity m WHERE m.startDate > current_timestamp AND m.scheduleDone = TRUE " +
        "ORDER BY m.startDate ASC")
    List<MarathonEntity> findFutureMarathonsWithScheduleDone();

    @Modifying
    @Query("UPDATE MarathonEntity m SET m.submitsOpen = true, m.canEditSubmissions = true WHERE m = :marathon")
    void openSubmissions(@Param("marathon") MarathonEntity marathon);

    @Modifying
    @Query("UPDATE MarathonEntity m SET m.submitsOpen = false WHERE m = :marathon")
    void closeSubmissions(@Param("marathon") MarathonEntity marathon);

    @Query("SELECT COUNT(c.id) AS submissionCount," +
        "COUNT(DISTINCT c.game.submission.user) AS runnerCount," +
        "SUM(c.estimate) AS totalLength," +
        "AVG(c.estimate) AS averageEstimate " +
        "FROM CategoryEntity c WHERE c.game.submission.marathon = :marathon")
    Optional<Map<String, Object>> findStats(@Param("marathon") MarathonEntity marathon);

}
