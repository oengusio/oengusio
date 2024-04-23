package app.oengus.adapter.jpa.repository;

import app.oengus.adapter.jpa.entity.CategoryEntity;
import app.oengus.adapter.jpa.entity.GameEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends CrudRepository<CategoryEntity, Integer> {

    // TODO: aren't these ids unique?
    // Actually, this is probably done for safety in the routes.
    @Query(value = "SELECT c FROM CategoryEntity c WHERE c.game.id = :gameId AND c.game.submission.id = :submissionId AND c.game.submission.marathon.id = :marathonId ORDER BY c.id ASC")
    List<CategoryEntity> findByGameId(
        @Param("marathonId") String marathonId,
        @Param("submissionId") int submissionId,
        @Param("gameId") int gameId
    );

    List<CategoryEntity> findByGame(GameEntity game);

    boolean existsByCode(String code);

    Optional<CategoryEntity> findByCode(String code);

}
