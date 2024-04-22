package app.oengus.service;

import app.oengus.application.OengusWebhookService;
import app.oengus.application.SubmissionService;
import app.oengus.application.port.persistence.GamePersistencePort;
import app.oengus.application.port.persistence.MarathonPersistencePort;
import app.oengus.application.port.persistence.SubmissionPersistencePort;
import app.oengus.domain.OengusUser;
import app.oengus.domain.submission.Game;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final SubmissionService submissionService;
    private final GamePersistencePort gamePersistencePort;
    private final SubmissionPersistencePort submissionPersistencePort;
    private final MarathonPersistencePort marathonPersistencePort;
    private final OengusWebhookService webhookService;

    ///////////
    // v2 stuff

    // TODO: use mapper in controller
    public List<Game> findBySubmissionId(final String marathonId, final int submissionId) {
        return this.gamePersistencePort.findAllByMarathonAndSubmission(marathonId, submissionId);
    }

    ///////////
    // v1 stuff

    public void update(final Game game) {
        this.gamePersistencePort.save(game);
    }

    // IMPORTANT: the hook is sent here so that it only triggers once for submission delete
    public void delete(final int id, final OengusUser deletedBy) throws NotFoundException {
        final var game = this.gamePersistencePort.findById(id).orElseThrow(
            () -> new NotFoundException("Game not found")
        );
        final var submission = this.submissionPersistencePort.getByGameId(game.getId());

        // only one game, delete the submission
        if (submission.getGames().size() == 1) {
            this.submissionService.delete(submission.getId(), deletedBy);
            return;
        }

        final var marathon = this.marathonPersistencePort.findById(submission.getMarathonId()).orElseThrow(
            () -> new NotFoundException("Marathon not found")
        );

        final String webhook = marathon.getWebhook();

        if (StringUtils.isNotEmpty(webhook)) {
            try {
                this.webhookService.sendGameDeleteEvent(webhook, game, deletedBy);
            } catch (Exception e) {
                LoggerFactory.getLogger(GameService.class).error("Error when handling webhook", e);
            }
        }

//        submission.getGames().remove(game);

        this.gamePersistencePort.deleteById(id);
        // TODO: do I still need to do this?
//        this.submissionPersistencePort.save(submission);
    }

}
