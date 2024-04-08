package app.oengus.adapter.jpa.repository;

import app.oengus.entity.model.GameEntity;
import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.Status;
import app.oengus.adapter.jpa.entity.SubmissionEntity;
import app.oengus.adapter.jpa.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
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
    List<Map<String, ?>> findByMarathonToplevel(@Param("marathon") Marathon marathon);

    SubmissionEntity findByGamesContaining(GameEntity game);

    ////////////
    // Old stuff

    Optional<SubmissionEntity> findByUserAndMarathon(User user, Marathon marathon);

    @Query(value =
        "SELECT s FROM SubmissionEntity s " +
            "JOIN FETCH s.games g " +
            "JOIN FETCH g.categories c " +
            "JOIN FETCH c.selection sel " +
            "where s.marathon = :marathon AND sel.status IN (2, 3)")
    List<SubmissionEntity> findValidatedOrBonusSubmissionsForMarathon(@Param("marathon") Marathon marathon);

    @Query(value =
        "SELECT s FROM SubmissionEntity s " +
            "INNER JOIN s.games g ON g.submission = s " +
            "INNER JOIN g.categories c ON c.game = g " +
            "WHERE s.marathon = :marathon AND (" +
            "LOWER(s.user.username) LIKE concat('%',LOWER(:searchQ),'%') OR " +
            "LOWER(s.user.displayName) LIKE concat('%',LOWER(:searchQ),'%') OR " +
            "(" +
                "SELECT COUNT(opp.id) FROM Opponent opp WHERE " +
                    "LOWER(opp.submission.user.username) LIKE concat('%',LOWER(:searchQ),'%') OR " +
                    "LOWER(opp.submission.user.displayName) LIKE concat('%',LOWER(:searchQ),'%')" +
            ") > 0 OR " +
            "LOWER(g.name) LIKE concat('%',LOWER(:searchQ),'%') OR " +
            "LOWER(c.name) LIKE concat('%',LOWER(:searchQ),'%')) GROUP BY s")
    Page<SubmissionEntity> searchForMarathon(@Param("marathon") Marathon marathon, @Param("searchQ") String searchQ, Pageable pageable);

    @Query(value =
        "SELECT s FROM SubmissionEntity s " +
            "INNER JOIN s.games g ON g.submission = s " +
            "INNER JOIN g.categories c ON c.game = g " +
            "INNER JOIN c.selection sel ON sel.category = c " +
            "WHERE s.marathon = :marathon AND (" +
            "LOWER(s.user.username) LIKE concat('%',LOWER(:searchQ),'%') OR " +
            "LOWER(s.user.displayName) LIKE concat('%',LOWER(:searchQ),'%') OR " +
            "(" +
                "SELECT COUNT(opp.id) FROM Opponent opp WHERE " +
                    "LOWER(opp.submission.user.username) LIKE concat('%',LOWER(:searchQ),'%') OR " +
            "LOWER(opp.submission.user.displayName) LIKE concat('%',LOWER(:searchQ),'%')" +
            ") > 0 OR " +
            "LOWER(g.name) LIKE concat('%',LOWER(:searchQ),'%') OR " +
            "LOWER(c.name) LIKE concat('%',LOWER(:searchQ),'%')) AND sel.status = :status GROUP BY s")
    Page<SubmissionEntity> searchForMarathonWithStatus(
        @Param("marathon") Marathon marathon, @Param("searchQ") String searchQ,
        @Param("status") Status status, Pageable pageable);

    void deleteByMarathon(Marathon marathon);

    List<SubmissionEntity> findByUser(User user);

    Page<SubmissionEntity> findByMarathonOrderByIdAsc(Marathon marathon, Pageable pageable);

    List<SubmissionEntity> findByMarathonOrderByIdAsc(Marathon marathon);

    boolean existsByMarathonAndUser(Marathon marathon, User user);

}
