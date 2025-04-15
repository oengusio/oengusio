package app.oengus.util;

import app.oengus.application.port.persistence.CategoryPersistencePort;
import app.oengus.application.port.persistence.SubmissionPersistencePort;
import app.oengus.application.port.persistence.UserPersistencePort;
import app.oengus.domain.submission.Category;
import app.oengus.factory.OengusUserFactory;
import app.oengus.factory.submission.OpponentFactory;
import app.oengus.factory.submission.SubmissionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubmissionHelpers {
    private final OengusUserFactory oengusUserFactory;
    private final OpponentFactory opponentFactory;
    private final SubmissionFactory submissionFactory;

    private final UserPersistencePort userPersistencePort;
    private final SubmissionPersistencePort submissionPersistencePort;
    private final CategoryPersistencePort categoryPersistencePort;

    public Category addOpponents(int opponentCount, Category category, String marathonId) {
        for (int i = 0; i < opponentCount; i++) {
            final var user = this.userPersistencePort.save(this.oengusUserFactory.getNormalUser());

            final var submission = this.submissionFactory.withMarathonId(marathonId);

            submission.setUser(user);

            final var savedSubmission = this.submissionPersistencePort.save(submission);

            final var opponent = this.opponentFactory.getOpponent(category.getId());

            opponent.setUserId(user.getId());
            opponent.setSubmissionId(savedSubmission.getId());

            category.getOpponents().add(opponent);
        }

        return this.categoryPersistencePort.save(category);
    }
}
