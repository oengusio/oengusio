package app.oengus.dao;

import app.oengus.entity.model.CategoryEntity;
import app.oengus.adapter.jpa.entity.MarathonEntity;
import app.oengus.entity.model.SelectionEntity;
import app.oengus.entity.model.Status;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SelectionRepository extends CrudRepository<SelectionEntity, Integer> {

    List<SelectionEntity> findByMarathon(MarathonEntity marathon);

    SelectionEntity findByCategory(CategoryEntity category);

    List<SelectionEntity> findByMarathonAndStatusIn(MarathonEntity marathon, List<Status> statuses);

    @Modifying
    @Query("UPDATE SelectionEntity s SET s.status = 1 WHERE s.marathon = :marathon AND s.status = 0")
    void rejectTodos(@Param("marathon") MarathonEntity marathon);

    @Query("SELECT s FROM SelectionEntity s WHERE s.category.id IN :categories")
    List<SelectionEntity> findAllByCategory(@Param("categories") Iterable<Integer> categories);
}
