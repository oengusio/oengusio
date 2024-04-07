package app.oengus.application.port.persistence;

import app.oengus.domain.Category;
import app.oengus.domain.Game;

import java.util.List;
import java.util.Optional;

public interface CategoryPersistencePort {
    List<Category> findByMarathonSubmissionAndGameId(String marathonId, int submissionId, int gameId);

    List<Category> findByGame(Game game);

    Optional<Category> findByCode(String code);

    void delete(Category category);

    void save(Category category);
}
