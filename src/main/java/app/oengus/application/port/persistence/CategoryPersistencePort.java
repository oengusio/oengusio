package app.oengus.application.port.persistence;

import app.oengus.domain.submission.Category;
import app.oengus.domain.submission.Game;

import java.util.List;
import java.util.Optional;

public interface CategoryPersistencePort {
    Optional<Category> findById(int id);

    List<Category> findByGameId(int gameId);

    List<Category> findByMarathonSubmissionAndGameId(String marathonId, int submissionId, int gameId);

    List<Category> findByGame(Game game);

    List<Category> findAllById(List<Integer> ids);

    Optional<Category> findByCode(String code);

    boolean existsByCode(String code);

    void delete(Category category);

    void deleteAllById(List<Integer> ids);

    void save(Category category);
}
