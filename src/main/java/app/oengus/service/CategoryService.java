package app.oengus.service;

import app.oengus.entity.dto.OpponentSubmissionDto;
import app.oengus.entity.model.*;
import app.oengus.exception.OengusBusinessException;
import app.oengus.helper.PrincipalHelper;
import app.oengus.service.repository.CategoryRepositoryService;
import javassist.NotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private GameService gameService;

    @Autowired
    private CategoryRepositoryService categoryRepositoryService;

    @Autowired
    private OengusWebhookService webhookService;

    private static final List<RunType> MULTIPLAYER_RUN_TYPES = List.of(RunType.COOP_RACE, RunType.COOP, RunType.RACE);

    public OpponentSubmissionDto findCategoryByCode(final String marathonId, final String code) {
        final Category category = this.categoryRepositoryService.findByCode(code);
        final User user = PrincipalHelper.getCurrentUser();
        if (category != null) {
            if (!Objects.equals(category.getGame().getSubmission().getMarathon().getId(),
                    marathonId)) {
                throw new OengusBusinessException("DIFFERENT_MARATHON");
            }
            if (!MULTIPLAYER_RUN_TYPES.contains(category.getType())) {
                throw new OengusBusinessException("NOT_MULTIPLAYER");
            }
            if (user != null) {
                if (Objects.equals(category.getGame().getSubmission().getUser().getId(), user.getId())) {
                    throw new OengusBusinessException("SAME_USER");
                }
                if (category.getOpponents()
                            .stream()
                            .map(opponent -> opponent.getSubmission().getUser())
                            .anyMatch(user1 -> user1.getId().equals(user.getId()))) {
                    throw new OengusBusinessException("ALREADY_IN_OPPONENTS");
                }
            }
            final OpponentSubmissionDto opponentDto = new OpponentSubmissionDto();
            final List<User> users = new ArrayList<>();
            users.add(category.getGame().getSubmission().getUser());
            users.addAll(category
                    .getOpponents()
                    .stream()
                    .map(opponent -> opponent.getSubmission().getUser())
                    .collect(
                            Collectors.toSet()));
            if (category.getGame().getSubmission().getMarathon().getMaxNumberOfScreens() <= users.size()) {
                throw new OengusBusinessException("MAX_SIZE_REACHED");
            }
            opponentDto.setUsers(users);
            opponentDto.setGameName(category.getGame().getName());
            opponentDto.setCategoryName(category.getName());
            opponentDto.setCategoryId(category.getId());
            return opponentDto;
        }
        throw new OengusBusinessException("CODE_NOT_FOUND");
    }

    public void delete(final int id, final User deletedBy) throws NotFoundException {
        final Category category = this.categoryRepositoryService.findById(id);
        final Game game = category.getGame();

        // only have one category, delete the game
        if (game.getCategories().size() == 1) {
            this.gameService.delete(game.getId(), deletedBy);
            return;
        }

        final Submission submission = game.getSubmission();
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
