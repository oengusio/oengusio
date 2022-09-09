package app.oengus.dao;

import app.oengus.entity.model.Category;
import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.Selection;
import app.oengus.entity.model.Status;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SelectionRepository extends CrudRepository<Selection, Integer> {

    List<Selection> findByMarathon(Marathon marathon);

    Selection findByCategory(Category category);

    List<Selection> findByMarathonAndStatusIn(Marathon marathon, List<Status> statuses);

    @Modifying
    @Query("UPDATE Selection s SET s.status = 1 WHERE s.marathon = :marathon AND s.status = 0")
    void rejectTodos(@Param("marathon") Marathon marathon);

    @Query("SELECT s FROM Selection s WHERE s.category.id IN :categories")
    List<Selection> findAllByCategory(@Param("categories") Iterable<Integer> categories);
}
