package app.oengus.dao;

import app.oengus.entity.model.Game;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;

@Repository
public interface GameRepository extends CrudRepository<Game, Integer> {

	@Query(value = "SELECT g FROM Game g WHERE g.submission.marathon.id = :marathonId ORDER BY g.id ASC")
	@QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
	List<Game> findByMarathon(@Param("marathonId") String marathonId);

}
