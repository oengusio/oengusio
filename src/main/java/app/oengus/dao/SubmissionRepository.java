package app.oengus.dao;

import app.oengus.entity.model.Marathon;
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

    void deleteByMarathon(Marathon marathon);

    List<Submission> findByUser(User user);

    Page<Submission> findByMarathonOrderByIdAsc(Marathon marathon, Pageable pageable);

    List<Submission> findByMarathonOrderByIdAsc(Marathon marathon);

    boolean existsByMarathonAndUser(Marathon marathon, User user);

}
