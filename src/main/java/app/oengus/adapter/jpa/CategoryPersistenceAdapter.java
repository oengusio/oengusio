package app.oengus.adapter.jpa;

import app.oengus.adapter.jpa.mapper.CategoryMapper;
import app.oengus.adapter.jpa.repository.CategoryRepository;
import app.oengus.application.port.persistence.CategoryPersistencePort;
import app.oengus.domain.submission.Category;
import app.oengus.domain.submission.Game;
import app.oengus.adapter.jpa.entity.CategoryEntity;
import app.oengus.adapter.jpa.entity.GameEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Component
@Profile("!test")
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
        return this.repository.findByGame(GameEntity.ofId(gameId))
            .stream()
            .map(this.mapper::toDomain)
            .toList();
    }

    @Override
    public List<Category> findByGame(Game game) {
        return this.findByGameId(game.getId());
    }

    @Override
    public List<Category> findAllById(List<Integer> ids) {
        return ((List<CategoryEntity>) this.repository.findAllById(ids))
            .stream()
            .map(this.mapper::toDomain)
            .toList();
    }

    @Override
    public Optional<Category> findByCode(String code) {
        return this.repository.findByCode(code).map(this.mapper::toDomain);
    }

    @Override
    public boolean existsByCode(String code) {
        return this.repository.existsByCode(code);
    }

    @Override
    public void delete(Category category) {
        final var entity = this.mapper.fromDomain(category);

        this.repository.delete(entity);
    }

    @Override
    @Transactional
    public void deleteAllById(List<Integer> ids) {
        this.repository.deleteAllById(ids);
    }

    @Override
    public void save(Category category) {
        final var entity = this.mapper.fromDomain(category);

        this.repository.save(entity);
    }
}
