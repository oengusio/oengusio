package app.oengus.application;

import app.oengus.application.port.persistence.SavedCategoryPersistencePort;
import app.oengus.application.port.persistence.SavedGamePersistencePort;
import app.oengus.domain.OengusUser;
import app.oengus.domain.exception.OengusBusinessException;
import app.oengus.domain.user.SavedCategory;
import app.oengus.domain.user.SavedGame;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SavedGameService {
    private final SavedGamePersistencePort savedGamePort;
    private final SavedCategoryPersistencePort savedCategoryPort;

    public Optional<SavedGame> findByIdAndUser(int gameId, OengusUser user) {
        return this.findByIdAndUser(gameId, user.getId());
    }

    public Optional<SavedGame> findByIdAndUser(int gameId, int userId) {
        return this.savedGamePort.findByIdAndUser(gameId, userId);
    }

    public List<SavedGame> getByUser(OengusUser user) {
        return getByUserId(user.getId());
    }

    public List<SavedGame> getByUserId(int userId) {
        return this.savedGamePort.findAllByUser(userId, Pageable.unpaged()).getContent();
    }

    public SavedGame save(SavedGame game) {
        return this.savedGamePort.save(game);
    }

    public void deleteForUser(OengusUser user) {
        throw new OengusBusinessException("SavedGameService#deleteForUser has not yet been implemented");
    }

    public Optional<SavedCategory> findCategoryByIdAndGame(int gameId, int categoryId) {
        return this.savedCategoryPort.findByIdAndGameId(categoryId, gameId);
    }

    public SavedCategory saveCategory(SavedCategory category) {
        return this.savedCategoryPort.save(category);
    }
}
