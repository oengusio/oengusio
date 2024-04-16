package app.oengus.application.port.persistence;

import app.oengus.domain.submission.Game;

import java.util.Optional;

public interface GamePersistencePort {
    Optional<Game> findById(int id);

    void deleteById(int id);
}
