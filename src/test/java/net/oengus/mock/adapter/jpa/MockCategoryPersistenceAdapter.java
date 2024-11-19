package net.oengus.mock.adapter.jpa;

import app.oengus.application.port.persistence.CategoryPersistencePort;
import app.oengus.domain.submission.Category;
import app.oengus.domain.submission.Game;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Profile("test")
@Component
@RequiredArgsConstructor
public class MockCategoryPersistenceAdapter implements CategoryPersistencePort {
    @Override
    public Optional<Category> findById(int id) {
        return Optional.empty();
    }

    @Override
    public List<Category> findByGameId(int gameId) {
        return List.of();
    }

    @Override
    public List<Category> findByMarathonSubmissionAndGameId(String marathonId, int submissionId, int gameId) {
        return List.of();
    }

    @Override
    public List<Category> findByGame(Game game) {
        return List.of();
    }

    @Override
    public List<Category> findAllById(List<Integer> ids) {
        return List.of();
    }

    @Override
    public Optional<Category> findByCode(String code) {
        return Optional.empty();
    }

    @Override
    public boolean existsByCode(String code) {
        return false;
    }

    @Override
    public void delete(Category category) {

    }

    @Override
    public void deleteAllById(List<Integer> ids) {

    }

    @Override
    public void save(Category category) {

    }
}
