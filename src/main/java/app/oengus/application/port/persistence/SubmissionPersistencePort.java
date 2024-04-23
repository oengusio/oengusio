package app.oengus.application.port.persistence;

import app.oengus.domain.submission.Submission;
import app.oengus.domain.submission.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SubmissionPersistencePort {
    Optional<Submission> findById(int id);

    Submission getByGameId(int gameId);

    Optional<Submission> findForUserInMarathon(int userId, String marathonId);

    // Extra boolean method is more efficient than mapping the models
    boolean existsForUserInMarathon(int userId, String marathonId);

    List<Submission> findAcceptedInMarathon(String marathonId);

    Page<Submission> searchInMarathon(String marathonId, String query, Pageable pageable);

    Page<Submission> searchInMarathon(String marathonId, String query, Status status, Pageable pageable);

    Submission save(Submission submission);

    void deleteByMarathon(String marathonId);

    void delete(Submission submission);

    List<Submission> findByUser(int userId);

    Page<Submission> findByMarathon(String marathonId, Pageable pageable);

    List<Submission> findAllByMarathon(String marathonId);

    Optional<Integer> getUserIdFromOpponentId(int opponentId);
}
