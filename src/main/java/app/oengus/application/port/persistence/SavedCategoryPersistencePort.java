package app.oengus.application.port.persistence;

import app.oengus.domain.user.SavedCategory;

import java.util.Optional;

public interface SavedCategoryPersistencePort {
    Optional<SavedCategory> findByIdAndGameId(int id, int gameId);

    SavedCategory save(SavedCategory category);

    default void delete(SavedCategory category) {
        this.deleteById(category.getId());
    }

    void deleteById(int id);

    boolean doesUserOwnCategory(int userId, int categoryId);
}
