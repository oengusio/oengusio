package app.oengus.dao;

import app.oengus.entity.model.Category;
import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.Selection;
import app.oengus.entity.model.Status;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface SelectionRepository extends CrudRepository<Selection, Integer> {

	@QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
	List<Selection> findByMarathon(Marathon marathon);

	@QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
	Selection findByCategory(Category category);

	@QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
	List<Selection> findByMarathonAndStatusIn(Marathon marathon, List<Status> statuses);

	@Modifying
	@Query("UPDATE Selection s SET s.status = 1 WHERE s.marathon = :marathon AND s.status = 0")
	void rejectTodos(@Param("marathon") Marathon marathon);

	@Query("SELECT s FROM Selection s WHERE s.category.id IN :categories")
	List<Selection> findAllByCategory(@Param("categories") Iterable<Integer> categories);
}
