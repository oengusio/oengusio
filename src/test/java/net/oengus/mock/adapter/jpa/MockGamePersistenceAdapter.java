package net.oengus.mock.adapter.jpa;

import app.oengus.application.port.persistence.GamePersistencePort;
import app.oengus.domain.submission.Game;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Profile("test")
@Component
@RequiredArgsConstructor
public class MockGamePersistenceAdapter implements GamePersistencePort {
    @Override
    public Optional<Game> findById(int id) {
        return Optional.empty();
    }

    @Override
    public Optional<Game> findByCategoryId(int categoryId) {
        return Optional.empty();
    }

    @Override
    public List<Game> findAllByMarathonAndSubmission(String marathonId, int submissionId) {
        return List.of();
    }

    @Override
    public void deleteById(int id) {

    }

    @Override
    public Game save(Game game) {
        return null;
    }
}
