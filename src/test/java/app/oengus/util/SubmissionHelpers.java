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

    public void addOpponents(int opponentCount, Category category, String marathonId) {
        for (int i = 0; i < opponentCount; i++) {
            final var user = this.oengusUserFactory.getNormalUser();

            this.userPersistencePort.save(user);

            final var submission = this.submissionFactory.withMarathonId(marathonId);

            submission.setUser(user);

            this.submissionPersistencePort.save(submission);

            final var opponent = this.opponentFactory.getOpponent(category.getId());

            opponent.setUserId(user.getId());
            opponent.setSubmissionId(submission.getId());

            category.getOpponents().add(opponent);
        }

        this.categoryPersistencePort.save(category);
    }
}
