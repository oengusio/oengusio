package app.oengus.application.port.persistence;

import app.oengus.domain.submission.Game;

import java.util.List;
import java.util.Optional;

public interface GamePersistencePort {
    Optional<Game> findById(int id);

    Optional<Game> findByCategoryId(int categoryId);

    List<Game> findAllByMarathonAndSubmission(String marathonId, int submissionId);

    void deleteById(int id);

    Game save(Game game);
}
