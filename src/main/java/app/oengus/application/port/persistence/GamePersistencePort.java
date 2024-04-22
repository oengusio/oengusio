package app.oengus.application.port.persistence;

import app.oengus.domain.submission.Game;

import java.util.List;
import java.util.Optional;

public interface GamePersistencePort {
    Optional<Game> findById(int id);

    List<Game> findAllByMarathonAndSubmission(String marathonId, int submissionId);

    void deleteById(int id);

    Game save(Game game);
}
