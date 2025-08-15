package app.oengus.application.port.persistence;

import app.oengus.domain.OengusUser;
import app.oengus.domain.user.SavedGame;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SavedGamePersistencePort {
    Optional<SavedGame> findById(int id);

    Optional<SavedGame> findByIdAndUser(int id, int userId);

    default Page<SavedGame> findAllByUser(OengusUser user, Pageable pageable) {
        return this.findAllByUser(user.getId(), pageable);
    }

    Page<SavedGame> findAllByUser(int userId, Pageable pageable);

    SavedGame save(SavedGame savedGame);

    void delete(SavedGame savedGame);
}
