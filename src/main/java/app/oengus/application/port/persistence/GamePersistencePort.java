package app.oengus.application.port.persistence;

import app.oengus.domain.Game;

import java.util.Optional;

public interface GamePersistencePort {
    Optional<Game> findById(int id);
}
