package app.oengus.adapter.jpa;

import app.oengus.adapter.jpa.entity.SavedCategoryEntity;
import app.oengus.adapter.jpa.entity.SavedGameEntity;
import app.oengus.adapter.jpa.entity.User;
import app.oengus.adapter.jpa.mapper.SavedCategoryEntityMapper;
import app.oengus.adapter.jpa.repository.SavedCategoryRepository;
import app.oengus.application.port.persistence.SavedCategoryPersistencePort;
import app.oengus.domain.user.SavedCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SavedCategoryPersistenceAdapter implements SavedCategoryPersistencePort {
    private final SavedCategoryRepository repository;
    private final SavedCategoryEntityMapper mapper;

    @Override
    public Optional<SavedCategory> findByIdAndGameId(int id, int gameId) {
        return this.repository.findByIdAndGame(id, SavedGameEntity.ofId(gameId))
            .map(this.mapper::toDomain);
    }

    @Override
    public SavedCategory save(SavedCategory category) {
        final var entity = this.mapper.fromDomain(category);

        if (entity.getId() < 1) {
            entity.setId(null);
        }

        final var savedEntity = this.repository.save(entity);

        return this.mapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(int id) {
        this.repository.deleteById(id);
    }

    @Override
    public boolean doesUserOwnCategory(int userId, int categoryId) {
        return this.repository.doesUserOwnCategory(
            SavedCategoryEntity.ofId(categoryId),
            User.ofId(userId)
        );
    }
}
