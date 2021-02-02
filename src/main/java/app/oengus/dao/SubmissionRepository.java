package app.oengus.dao;

import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.Submission;
import app.oengus.entity.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;

@Repository
public interface SubmissionRepository extends CrudRepository<Submission, Integer> {

	@QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
	Submission findByUserAndMarathon(User user, Marathon marathon);

	@Query(value =
			"SELECT s FROM Submission s " +
					"JOIN FETCH s.games g " +
					"JOIN FETCH g.categories c " +
					"JOIN FETCH c.selection sel " +
					"where s.marathon = :marathon AND sel.status IN (2, 3)")
	@QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
	List<Submission> findValidatedOrBonusSubmissionsForMarathon(@Param("marathon") Marathon marathon);

	void deleteByMarathon(Marathon marathon);

	@QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
	List<Submission> findByUser(User user);

	@QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
	List<Submission> findByMarathonOrderByIdAsc(Marathon marathon);

	@QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
	Boolean existsByMarathonAndUser(Marathon marathon, User user);

}
