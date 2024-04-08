package app.oengus.adapter.jpa;

import app.oengus.adapter.jpa.mapper.CategoryMapper;
import app.oengus.adapter.jpa.repository.CategoryRepository;
import app.oengus.application.port.persistence.CategoryPersistencePort;
import app.oengus.domain.Category;
import app.oengus.domain.Game;
import app.oengus.entity.model.GameEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryPersistenceAdapter implements CategoryPersistencePort {
    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    @Override
    public Optional<Category> findById(int id) {
        return this.repository.findById(id).map(this.mapper::toDomain);
    }

    @Override
    public List<Category> findByMarathonSubmissionAndGameId(String marathonId, int submissionId, int gameId) {
        return this.repository.findByGameId(
            marathonId,
            submissionId,
            gameId
        )
            .stream()
            .map(this.mapper::toDomain)
            .toList();
    }

    @Override
    public List<Category> findByGameId(int gameId) {
        return this.repository.findByGameContaining(GameEntity.ofId(gameId))
            .stream()
            .map(this.mapper::toDomain)
            .toList();
    }

    @Override
    public List<Category> findByGame(Game game) {
        return this.findByGameId(game.getId());
    }

    @Override
    public Optional<Category> findByCode(String code) {
        return this.repository.findByCode(code).map(this.mapper::toDomain);
    }

    @Override
    public void delete(Category category) {
        final var entity = this.mapper.fromDomain(category);

        this.repository.delete(entity);
    }

    @Override
    public void save(Category category) {
        final var entity = this.mapper.fromDomain(category);

        this.repository.save(entity);
    }
}
