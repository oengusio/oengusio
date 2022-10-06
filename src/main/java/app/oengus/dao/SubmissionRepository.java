package app.oengus.dao;

import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.Status;
import app.oengus.entity.model.Submission;
import app.oengus.entity.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends CrudRepository<Submission, Integer> {

    Submission findByUserAndMarathon(User user, Marathon marathon);

    @Query(value =
        "SELECT s FROM Submission s " +
            "JOIN FETCH s.games g " +
            "JOIN FETCH g.categories c " +
            "JOIN FETCH c.selection sel " +
            "where s.marathon = :marathon AND sel.status IN (2, 3)")
    List<Submission> findValidatedOrBonusSubmissionsForMarathon(@Param("marathon") Marathon marathon);

    @Query(value =
        "SELECT s FROM Submission s " +
            "INNER JOIN s.games g ON g.submission = s " +
            "INNER JOIN g.categories c ON c.game = g " +
            "WHERE s.marathon = :marathon AND (" +
            "LOWER(s.user.username) LIKE concat('%',LOWER(:searchQ),'%') OR " +
            "s.user.usernameJapanese LIKE concat('%',:searchQ,'%') OR " +
            "LOWER(g.name) LIKE concat('%',LOWER(:searchQ),'%') OR " +
            "LOWER(c.name) LIKE concat('%',LOWER(:searchQ),'%')) GROUP BY s")
    Page<Submission> searchForMarathon(@Param("marathon") Marathon marathon, @Param("searchQ") String searchQ, Pageable pageable);

    @Query(value =
        "SELECT s FROM Submission s " +
            "INNER JOIN s.games g ON g.submission = s " +
            "INNER JOIN g.categories c ON c.game = g " +
            "INNER JOIN c.selection sel ON sel.category = c " +
            "WHERE s.marathon = :marathon AND (" +
            "LOWER(s.user.username) LIKE concat('%',LOWER(:searchQ),'%') OR " +
            "s.user.usernameJapanese LIKE concat('%',:searchQ,'%') OR " +
            "LOWER(g.name) LIKE concat('%',LOWER(:searchQ),'%') OR " +
            "LOWER(c.name) LIKE concat('%',LOWER(:searchQ),'%')) AND sel.status = :status GROUP BY s")
    Page<Submission> searchForMarathonWithStatus(
        @Param("marathon") Marathon marathon, @Param("searchQ") String searchQ,
        @Param("status") Status status, Pageable pageable);

    void deleteByMarathon(Marathon marathon);

    List<Submission> findByUser(User user);

    Page<Submission> findByMarathonOrderByIdAsc(Marathon marathon, Pageable pageable);

    List<Submission> findByMarathonOrderByIdAsc(Marathon marathon);

    boolean existsByMarathonAndUser(Marathon marathon, User user);

}
