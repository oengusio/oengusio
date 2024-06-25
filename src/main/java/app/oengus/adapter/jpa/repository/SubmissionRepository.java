package app.oengus.adapter.jpa.repository;

import app.oengus.adapter.jpa.entity.*;
import app.oengus.domain.submission.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface SubmissionRepository extends CrudRepository<SubmissionEntity, Integer> {
    ///////////
    // V2 stuff

    @Query(value = "SELECT " +
        "s.id as id, " +
        "s.user.id as userId, " +
        "s.user.username as username, " +
        "s.user.displayName as displayName, " +
        "(SELECT COUNT(c.id) FROM CategoryEntity c WHERE c.game = (SELECT g FROM GameEntity g WHERE g.submission = s)) as total " +
        "FROM SubmissionEntity s WHERE s.marathon = :marathon")
    List<Map<String, ?>> findByMarathonToplevel(@Param("marathon") MarathonEntity marathon);

    @Query("SELECT s FROM SubmissionEntity s JOIN FETCH s.games g WHERE g = :game")
    SubmissionEntity findByGamesContaining(@Param("game") GameEntity game);

    ////////////
    // Old stuff

    Optional<SubmissionEntity> findByUserAndMarathon(User user, MarathonEntity marathon);

    @Query(value =
        "SELECT s FROM SubmissionEntity s " +
            "JOIN FETCH s.games g " +
            "JOIN FETCH g.categories c " +
            "JOIN FETCH c.selection sel " +
            "where s.marathon = :marathon AND sel.status IN (2, 3)")
    List<SubmissionEntity> findValidatedOrBonusSubmissionsForMarathon(@Param("marathon") MarathonEntity marathon);

    @Query(value =
        "SELECT s FROM SubmissionEntity s " +
            "INNER JOIN s.games g ON g.submission = s " +
            "INNER JOIN g.categories c ON c.game = g " +
            "WHERE s.marathon = :marathon AND (" +
            "LOWER(s.user.username) LIKE concat('%',LOWER(:searchQ),'%') OR " +
            "LOWER(s.user.displayName) LIKE concat('%',LOWER(:searchQ),'%') OR " +
            "(" +
                "SELECT COUNT(opp.id) FROM OpponentEntity opp WHERE opp.category = c AND (" +
                    "LOWER(opp.submission.user.username) LIKE concat('%',LOWER(:searchQ),'%') OR " +
                    "LOWER(opp.submission.user.displayName) LIKE concat('%',LOWER(:searchQ),'%'))" +
            ") > 0 OR " +
            "LOWER(g.name) LIKE concat('%',LOWER(:searchQ),'%') OR " +
            "LOWER(c.name) LIKE concat('%',LOWER(:searchQ),'%')) GROUP BY s")
    Page<SubmissionEntity> searchForMarathon(@Param("marathon") MarathonEntity marathon, @Param("searchQ") String searchQ, Pageable pageable);

    @Query(value =
        "SELECT s FROM SubmissionEntity s " +
            "INNER JOIN s.games g ON g.submission = s " +
            "INNER JOIN g.categories c ON c.game = g " +
            "INNER JOIN c.selection sel ON sel.category = c " +
            "WHERE s.marathon = :marathon AND (" +
            "LOWER(s.user.username) LIKE concat('%',LOWER(:searchQ),'%') OR " +
            "LOWER(s.user.displayName) LIKE concat('%',LOWER(:searchQ),'%') OR " +
            "(" +
                "SELECT COUNT(opp.id) FROM OpponentEntity opp WHERE opp.category = c AND (" +
                    "LOWER(opp.submission.user.username) LIKE concat('%',LOWER(:searchQ),'%') OR " +
                    "LOWER(opp.submission.user.displayName) LIKE concat('%',LOWER(:searchQ),'%'))" +
            ") > 0 OR " +
            "LOWER(g.name) LIKE concat('%',LOWER(:searchQ),'%') OR " +
            "LOWER(c.name) LIKE concat('%',LOWER(:searchQ),'%')) AND sel.status = :status GROUP BY s")
    Page<SubmissionEntity> searchForMarathonWithStatus(
        @Param("marathon") MarathonEntity marathon, @Param("searchQ") String searchQ,
        @Param("status") Status status, Pageable pageable);

    void deleteByMarathon(MarathonEntity marathon);

    List<SubmissionEntity> findByUser(User user);

    Page<SubmissionEntity> findByMarathonOrderByIdAsc(MarathonEntity marathon, Pageable pageable);

    List<SubmissionEntity> findByMarathonOrderByIdAsc(MarathonEntity marathon);

    boolean existsByMarathonAndUser(MarathonEntity marathon, User user);

    Optional<SubmissionEntity> findFirstByOpponentsContaining(OpponentEntity opponent);
}
