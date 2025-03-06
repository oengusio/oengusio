package app.oengus.application.port.persistence;

import app.oengus.domain.submission.Opponent;
import app.oengus.domain.submission.Submission;

import java.util.List;

public interface OpponentPersistencePort {
    List<Opponent> findByUser(int userId);

    List<Submission> findParentSubmissionsForUser(int userId);
}
