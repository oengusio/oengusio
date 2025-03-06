package app.oengus.mock.adapter.jpa;

import app.oengus.application.port.persistence.OpponentPersistencePort;
import app.oengus.domain.submission.Opponent;
import app.oengus.domain.submission.Submission;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("test")
@RequiredArgsConstructor
public class MockOpponentPersistenceAdapter implements OpponentPersistencePort {
    @Override
    public List<Opponent> findByUser(int userId) {
        return List.of();
    }

    @Override
    public List<Submission> findParentSubmissionsForUser(int userId) {
        return List.of();
    }
}
