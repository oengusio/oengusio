package app.oengus.service;

import app.oengus.entity.model.Game;
import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.Submission;
import app.oengus.service.repository.GameRepositoryService;
import javassist.NotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;

@Service
public class GameService {

	@Autowired
	private GameRepositoryService gameRepositoryService;

    @Autowired
    private OengusWebhookService webhookService;

    @Autowired
    private EntityManager entityManager;

    // IMPORTANT: the hook is sent here so that it only triggers once for submission delete
	public void delete(final int id) throws NotFoundException {
	    final Game game = this.gameRepositoryService.findById(id);
        final Submission submission = game.getSubmission();
        final String webhook = submission.getMarathon().getWebhook();

        if (StringUtils.isNotEmpty(webhook)) {
            Game.initialize(game);
            Submission.initialize(submission, false);

            entityManager.detach(submission);
            entityManager.detach(game);

            try {
                //
            } catch (Exception e) {
                LoggerFactory.getLogger(GameService.class).error("Error when handling webhook", e);
            }
        }

		this.gameRepositoryService.delete(id);
	}

}
