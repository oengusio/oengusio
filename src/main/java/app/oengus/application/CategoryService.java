package app.oengus.application;

import app.oengus.adapter.jpa.entity.SubmissionEntity;
import app.oengus.adapter.jpa.entity.User;
import app.oengus.application.port.persistence.CategoryPersistencePort;
import app.oengus.application.port.persistence.SubmissionPersistencePort;
import app.oengus.application.port.security.UserSecurityPort;
import app.oengus.domain.Category;
import app.oengus.domain.RunType;
import app.oengus.domain.Submission;
import app.oengus.entity.dto.OpponentSubmissionDto;
import app.oengus.entity.model.*;
import app.oengus.exception.OengusBusinessException;
import app.oengus.service.GameService;
import app.oengus.service.OengusWebhookService;
import app.oengus.service.repository.CategoryRepositoryService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final UserSecurityPort securityPort;
    private final GameService gameService;
    private final CategoryPersistencePort categoryPersistencePort;
    private final SubmissionPersistencePort submissionPersistencePort;
    private final CategoryRepositoryService categoryRepositoryService;
    private final OengusWebhookService webhookService;

    public static final List<RunType> MULTIPLAYER_RUN_TYPES = List.of(RunType.COOP_RACE, RunType.COOP, RunType.RACE, RunType.RELAY, RunType.RELAY_RACE);

    ///////////
    // v2 stuff

    // All parameters are given to ensure no funny business happens with people getting the wrong category for the wrong marathon.
    public List<Category> findByGameId(String marathonId, int submissionId, int gameId) {
        return this.categoryPersistencePort.findByMarathonSubmissionAndGameId(marathonId, submissionId, gameId);
    }

    ///////////
    // v1 stuff

    public OpponentSubmissionDto findCategoryByCode(final String marathonId, final String code) {
        final var optionalCategory = this.categoryPersistencePort.findByCode(code);

        if (optionalCategory.isEmpty()) {
            throw new OengusBusinessException("CODE_NOT_FOUND");
        }

        final var category = optionalCategory.get();

        final var user = this.securityPort.getAuthenticatedUser();
        final Submission submission = this.submissionPersistencePort.getByGameId(category.getGameId());
        final Marathon marathon = submission.getMarathonId();

        if (!Objects.equals(marathon.getId(), marathonId)) {
            throw new OengusBusinessException("DIFFERENT_MARATHON");
        }

        if (!MULTIPLAYER_RUN_TYPES.contains(category.getType())) {
            throw new OengusBusinessException("NOT_MULTIPLAYER");
        }

        if (user != null) {
            if (Objects.equals(submission.getUser().getId(), user.getId())) {
                throw new OengusBusinessException("SAME_USER");
            }
            if (category.getOpponents()
                .stream()
                .map(opponent -> opponent.getSubmission().getUser())
                .anyMatch(user1 -> user1.getId() == user.getId())) {
                throw new OengusBusinessException("ALREADY_IN_OPPONENTS");
            }
        }
        final OpponentSubmissionDto opponentDto = new OpponentSubmissionDto();
        final List<User> users = new ArrayList<>();
        users.add(submission.getUser());
        users.addAll(category
            .getOpponents()
            .stream()
            .map(opponent -> opponent.getSubmission().getUser())
            .collect(
                Collectors.toSet()));
        if (marathon.getMaxNumberOfScreens() <= users.size()) {
            throw new OengusBusinessException("MAX_SIZE_REACHED");
        }
        opponentDto.setUsers(users);
        opponentDto.setGameName(category.getGame().getName());
        opponentDto.setCategoryName(category.getName());
        opponentDto.setCategoryId(category.getId());
        return opponentDto;
    }

    public void delete(final int id, final User deletedBy) throws NotFoundException {
        final CategoryEntity category = this.categoryRepositoryService.findById(id);
        final GameEntity game = category.getGame();

        // only have one category, delete the game
        if (game.getCategories().size() == 1) {
            this.gameService.delete(game.getId(), deletedBy);
            return;
        }

        final SubmissionEntity submission = game.getSubmission();
        final String webhook = submission.getMarathon().getWebhook();

        if (StringUtils.isNotEmpty(webhook)) {
            try {
                this.webhookService.sendCategoryDeleteEvent(webhook, category, deletedBy);
            } catch (Exception e) {
                LoggerFactory.getLogger(GameService.class).error("Error when handling webhook", e);
            }
        }

        category.setGame(null);
        game.getCategories().remove(category);
        this.categoryRepositoryService.delete(id);
        this.gameService.update(game);
    }

}
