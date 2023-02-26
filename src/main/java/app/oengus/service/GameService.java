package app.oengus.service;

import app.oengus.entity.dto.v2.marathon.GameDto;
import app.oengus.entity.model.Game;
import app.oengus.entity.model.Submission;
import app.oengus.entity.model.User;
import app.oengus.service.repository.GameRepositoryService;
import app.oengus.service.repository.SubmissionRepositoryService;
import javassist.NotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {

    private final SubmissionService submissionService;

    private final SubmissionRepositoryService submissionRepositoryService;

    private final GameRepositoryService gameRepositoryService;

    private final OengusWebhookService webhookService;

    public GameService(SubmissionService submissionService, SubmissionRepositoryService submissionRepositoryService, GameRepositoryService gameRepositoryService, OengusWebhookService webhookService) {
        this.submissionService = submissionService;
        this.submissionRepositoryService = submissionRepositoryService;
        this.gameRepositoryService = gameRepositoryService;
        this.webhookService = webhookService;
    }

    ///////////
    // v2 stuff

    public List<GameDto> findBySubmissionId(final String marathonId, final int submissionId) {
        return this.gameRepositoryService.findBySubmissionId(marathonId, submissionId)
            .stream()
            .map((game) -> {
                GameDto gameDto = new GameDto();

                gameDto.setId(game.getId());
                gameDto.setName(game.getName());
                gameDto.setDescription(game.getDescription());
                gameDto.setConsole(game.getConsole());
                gameDto.setRatio(game.getRatio());
                gameDto.setEmulated(game.isEmulated());

                return gameDto;
            })
            .toList();
    }

    ///////////
    // v1 stuff

    public void update(final Game game) {
        this.gameRepositoryService.update(game);
    }

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
                this.webhookService.sendGameDeleteEvent(webhook, game.fresh(true), deletedBy);
            } catch (Exception e) {
                LoggerFactory.getLogger(GameService.class).error("Error when handling webhook", e);
            }
        }

        game.setSubmission(null);
        submission.getGames().remove(game);

        this.submissionRepositoryService.save(submission);
        this.gameRepositoryService.delete(id);
    }

}
