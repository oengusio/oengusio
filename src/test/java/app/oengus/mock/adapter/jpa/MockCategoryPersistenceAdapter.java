package app.oengus.mock.adapter.jpa;

import app.oengus.application.port.persistence.CategoryPersistencePort;
import app.oengus.domain.submission.Category;
import app.oengus.domain.submission.Game;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Profile("test")
@Component
@RequiredArgsConstructor
public class MockCategoryPersistenceAdapter implements CategoryPersistencePort {
    private final Map<Integer, Category> fakeDb = new HashMap<>();

    @Override
    public Optional<Category> findById(int id) {
        return Optional.ofNullable(this.fakeDb.get(id));
    }

    @Override
    public List<Category> findByGameId(int gameId) {
        return this.fakeDb.values()
            .stream()
            .filter(category -> category.getGameId() == gameId)
            .toList();
    }

    @Override
    public List<Category> findByMarathonSubmissionAndGameId(String marathonId, int submissionId, int gameId) {
        return List.of();
    }

    @Override
    public List<Category> findByGame(Game game) {
        return this.findByGameId(game.getId());
    }

    @Override
    public List<Category> findAllById(List<Integer> ids) {
        return List.of();
    }

    @Override
    public Optional<Category> findByCode(String code) {
        return this.fakeDb.values()
            .stream()
            .filter((c) -> c.getCode().equals(code))
            .findFirst();
    }

    @Override
    public boolean existsByCode(String code) {
        return this.fakeDb.values()
            .stream()
            .anyMatch((category) -> category.getCode().equals(code));
    }

    @Override
    public void delete(Category category) {
        this.fakeDb.remove(category.getId());
    }

    @Override
    public void deleteAllById(List<Integer> ids) {
        // TODO
    }

    @Override
    public void save(Category category) {
        this.fakeDb.put(category.getId(), category);
    }
}
