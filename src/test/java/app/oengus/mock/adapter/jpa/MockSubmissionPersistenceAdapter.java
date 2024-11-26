package app.oengus.mock.adapter.jpa;

import app.oengus.application.port.persistence.SubmissionPersistencePort;
import app.oengus.domain.OengusUser;
import app.oengus.domain.submission.Status;
import app.oengus.domain.submission.Submission;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Profile("test")
@Component
@RequiredArgsConstructor
public class MockSubmissionPersistenceAdapter implements SubmissionPersistencePort {
    private final Map<Integer, Submission> fakeDb = new HashMap<>();

    @Override
    public Optional<Submission> findById(int id) {
        return Optional.ofNullable(this.fakeDb.get(id));
    }

    @Override
    public Submission getByGameId(int gameId) {
        return this.fakeDb.values()
            .stream()
            .filter(
                (s) -> s.getGames().stream().anyMatch((g) -> g.getId() == gameId)
            )
            .findFirst()
            .orElse(null);
    }

    @Override
    public Submission getToplevelByGamId(int gameId) {
        return this.getByGameId(gameId);
    }

    @Override
    public Optional<Submission> findForUserInMarathon(int userId, String marathonId) {
        return Optional.empty();
    }

    @Override
    public boolean existsForUserInMarathon(int userId, String marathonId) {
        return false;
    }

    @Override
    public List<Submission> findAcceptedInMarathon(String marathonId) {
        return List.of();
    }

    @Override
    public Page<Submission> searchInMarathon(String marathonId, String query, Pageable pageable) {
        return null;
    }

    @Override
    public Page<Submission> searchInMarathon(String marathonId, String query, Status status, Pageable pageable) {
        return null;
    }

    @Override
    public Submission save(Submission submission) {
        this.fakeDb.put(submission.getId(), submission);

        return submission;
    }

    @Override
    public void deleteByMarathon(String marathonId) {

    }

    @Override
    public void delete(Submission submission) {

    }

    @Override
    public List<Submission> findByUser(int userId) {
        return List.of();
    }

    @Override
    public Page<Submission> findByMarathon(String marathonId, Pageable pageable) {
        return null;
    }

    @Override
    public List<Submission> findAllByMarathon(String marathonId) {
        return List.of();
    }

    @Override
    public Optional<Integer> getUserIdFromOpponentId(int opponentId) {
        return Optional.empty();
    }

    @Override
    public Map<Integer, OengusUser> findUsersByIds(List<Integer> submissionIds) {
        return Map.of();
    }
}
