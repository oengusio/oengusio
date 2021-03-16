package app.oengus.service;

import app.oengus.entity.model.Game;
import app.oengus.entity.model.Submission;
import app.oengus.entity.model.User;
import app.oengus.service.repository.GameRepositoryService;
import javassist.NotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private GameRepositoryService gameRepositoryService;

    @Autowired
    private OengusWebhookService webhookService;

    // IMPORTANT: the hook is sent here so that it only triggers once for submission delete
    public void delete(final int id, final User deletedBy) throws NotFoundException {
        final Game game = this.gameRepositoryService.findById(id);
        final Submission submission = game.getSubmission();

        // only one game, delete the submission
        if (submission.getGames().size() == 1) {
            this.submissionService.delete(submission.getId(), deletedBy);
            return;
        }

        final String webhook = submission.getMarathon().getWebhook();

        if (StringUtils.isNotEmpty(webhook)) {
            try {
                this.webhookService.sendGameDeleteEvent(webhook, game, deletedBy);
            } catch (Exception e) {
                LoggerFactory.getLogger(GameService.class).error("Error when handling webhook", e);
            }
        }

        game.setSubmission(null);
        submission.getGames().remove(game);
        this.gameRepositoryService.delete(id);
    }

}
