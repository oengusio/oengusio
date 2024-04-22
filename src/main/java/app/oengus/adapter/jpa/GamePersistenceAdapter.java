package app.oengus.adapter.jpa;

import app.oengus.adapter.jpa.entity.CategoryEntity;
import app.oengus.adapter.jpa.mapper.GameMapper;
import app.oengus.adapter.jpa.repository.GameRepository;
import app.oengus.application.port.persistence.GamePersistencePort;
import app.oengus.domain.submission.Game;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GamePersistenceAdapter implements GamePersistencePort {
    private final GameRepository repository;
    private final GameMapper mapper;

    @Override
    public Optional<Game> findById(int id) {
        return this.repository.findById(id).map(this.mapper::toDomain);
    }

    @Override
    public Optional<Game> findByCategoryId(int categoryId) {
        return this.repository.findByCategoriesContaining(
            CategoryEntity.ofId(categoryId)
        ).map(this.mapper::toDomain);
    }

    @Override
    public List<Game> findAllByMarathonAndSubmission(String marathonId, int submissionId) {
        return List.of();
    }

    @Override
    public void deleteById(int id) {
        this.repository.deleteById(id);
    }

    @Override
    public Game save(Game game) {
        final var entity = this.mapper.fromDomain(game);
        final var savedEntity = this.repository.save(entity);

        return this.mapper.toDomain(savedEntity);
    }
}
