package app.oengus.application.port.persistence;

import app.oengus.domain.user.SavedCategory;

import java.util.Optional;

public interface SavedCategoryPersistencePort {
    Optional<SavedCategory> findByIdAndGameId(int id, int gameId);

    SavedCategory save(SavedCategory category);

    void delete(SavedCategory category);

    boolean doesUserOwnCategory(int userId, int categoryId);
}
