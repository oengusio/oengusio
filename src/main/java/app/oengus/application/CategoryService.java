package app.oengus.application;

import app.oengus.application.port.persistence.CategoryPersistencePort;
import app.oengus.application.port.persistence.MarathonPersistencePort;
import app.oengus.application.port.persistence.SubmissionPersistencePort;
import app.oengus.application.port.security.UserSecurityPort;
import app.oengus.domain.OengusUser;
import app.oengus.domain.exception.OengusBusinessException;
import app.oengus.domain.marathon.Marathon;
import app.oengus.domain.submission.Category;
import app.oengus.domain.submission.Opponent;
import app.oengus.domain.submission.RunType;
import app.oengus.domain.submission.Submission;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final UserSecurityPort securityPort;
    private final GameService gameService;
    private final CategoryPersistencePort categoryPersistencePort;
    private final SubmissionPersistencePort submissionPersistencePort;
    private final MarathonPersistencePort marathonPersistencePort;
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

    public Pair<Category, List<OengusUser>> findCategoryByCode(final String marathonId, final String code) {
        final var optionalCategory = this.categoryPersistencePort.findByCode(code);

        if (optionalCategory.isEmpty()) {
            throw new OengusBusinessException("CODE_NOT_FOUND");
        }

        final var category = optionalCategory.get();

        System.out.println("==========================================");
        System.out.println("GOT CATEGORY");
        System.out.println(category);
        System.out.println("==========================================");

        final var selfUser = this.securityPort.getAuthenticatedUser();
        final Submission submission = this.submissionPersistencePort.getToplevelByGamId(category.getGameId());

        System.out.println("==========================================");
        System.out.println("GOT SUBMISSION");
        System.out.println(submission);
        System.out.println("==========================================");

        if (!Objects.equals(submission.getMarathonId(), marathonId)) {
            throw new OengusBusinessException("DIFFERENT_MARATHON");
        }

        if (!MULTIPLAYER_RUN_TYPES.contains(category.getType())) {
            throw new OengusBusinessException("NOT_MULTIPLAYER");
        }

        // re-use the same data to prevent loads of db lookups
        final var opponentSubmissionIds = category.getOpponents().stream().map(Opponent::getSubmissionId).toList();
        final Map<Integer, OengusUser> opponentUsers =
            opponentSubmissionIds.isEmpty()
                ? Map.of()
                : this.submissionPersistencePort.findUsersByIds(opponentSubmissionIds);

        System.out.println("==========================================");
        System.out.println("GOT USERS");
        System.out.println(opponentUsers);
        System.out.println("==========================================");

        if (selfUser != null) {
            if (Objects.equals(submission.getUser().getId(), selfUser.getId())) {
                throw new OengusBusinessException("SAME_USER");
            }

            System.out.println("==========================================");
            System.out.println("SELF CHECK PASSED");
            System.out.println("==========================================");

            if (category.getOpponents()
                .stream()
                .map(
                    opponent -> opponentUsers.get(opponent.getSubmissionId())
                )
                .filter(Objects::nonNull)
                .anyMatch(userId -> userId.getId() == selfUser.getId())) {
                throw new OengusBusinessException("ALREADY_IN_OPPONENTS");
            }

            System.out.println("==========================================");
            System.out.println("OPPONENT CHECK PASSED");
            System.out.println("==========================================");
        }

        final List<OengusUser> users = new ArrayList<>();

        users.add(submission.getUser());

        if (!opponentUsers.isEmpty()) {
            users.addAll(opponentUsers.values());
        }

        System.out.println("==========================================");
        System.out.println("PRE MARATHON FETCH");
        System.out.println("==========================================");

        final Marathon marathon = this.marathonPersistencePort.findById(submission.getMarathonId())
            .orElseThrow(
                () -> new OengusBusinessException("SUBMISSION_MARATHON_MISSING")
            );

        System.out.println("==========================================");
        System.out.println("MARATHON FETCHED");
        System.out.println("==========================================");

        // hangs on this check???
        if (marathon.getMaxNumberOfScreens() <= users.size()) {
            throw new OengusBusinessException("MAX_SIZE_REACHED");
        }

        return Pair.of(
            category, users
        );
    }

    public void delete(String marathonId, final int id) throws NotFoundException {
        final var optionalCategory = this.categoryPersistencePort.findById(id);

        if (optionalCategory.isEmpty()) {
            throw new NotFoundException("CATEGORY_NOT_FOUND");
        }

        final var category = optionalCategory.get();
        final var gameCategories = this.categoryPersistencePort.findByGameId(category.getGameId());
        final var deletedBy = this.securityPort.getAuthenticatedUser();

        // only have one category, delete the game
        if (gameCategories.size() == 1) {
            this.gameService.delete(category.getGameId(), deletedBy);
            return;
        }

        final var marathon = this.marathonPersistencePort.findById(marathonId)
            .orElseThrow(() -> new NotFoundException("MARATHON_NOT_FOUND"));
        final String webhook = marathon.getWebhook();

        if (StringUtils.isNotEmpty(webhook)) {
            try {
                this.webhookService.sendCategoryDeleteEvent(webhook, category, deletedBy);
            } catch (Exception e) {
                LoggerFactory.getLogger(CategoryService.class).error("Error when handling webhook", e);
            }
        }

        this.categoryPersistencePort.delete(category);
    }

}
