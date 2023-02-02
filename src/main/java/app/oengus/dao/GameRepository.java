package app.oengus.dao;

import app.oengus.entity.model.Game;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends CrudRepository<Game, Integer> {

    @Query(value = "SELECT g FROM Game g WHERE g.submission.id = :submissionId AND g.submission.marathon.id = :marathonId ORDER BY g.id ASC")
    List<Game> findBySubmissionId(@Param("marathonId") String marathonId, @Param("submissionId") int submissionId);

    @Query(value = "SELECT g FROM Game g WHERE g.submission.marathon.id = :marathonId ORDER BY g.id ASC")
    List<Game> findByMarathon(@Param("marathonId") String marathonId);

}
