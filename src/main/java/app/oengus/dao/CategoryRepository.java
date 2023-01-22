package app.oengus.dao;

import app.oengus.entity.model.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Integer> {

    @Query(value = "SELECT c FROM Category c WHERE c.game.id = :gameId AND c.game.submission.id = :submissionId AND c.game.submission.marathon.id = :marathonId ORDER BY c.id ASC")
    List<Category> findByGameId(
        @Param("marathonId") String marathonId,
        @Param("submissionId") int submissionId,
        @Param("gameId") int gameId
    );

    boolean existsByCode(String code);

    Category findByCode(String code);

}
