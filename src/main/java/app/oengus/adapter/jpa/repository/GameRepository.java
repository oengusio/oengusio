package app.oengus.adapter.jpa.repository;

import app.oengus.entity.model.GameEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameRepository extends CrudRepository<GameEntity, Integer> {
    @Query(value = "SELECT g FROM GameEntity g WHERE g.submission.id = :submissionId AND g.submission.marathon.id = :marathonId ORDER BY g.id ASC")
    List<GameEntity> findBySubmissionId(@Param("marathonId") String marathonId, @Param("submissionId") int submissionId);

    @Query(value = "SELECT g FROM GameEntity g WHERE g.submission.marathon.id = :marathonId ORDER BY g.id ASC")
    List<GameEntity> findByMarathon(@Param("marathonId") String marathonId);

}
