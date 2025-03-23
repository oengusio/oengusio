package app.oengus.adapter.rest.helper;

import app.oengus.adapter.rest.dto.v1.SubmissionDto;
import app.oengus.adapter.rest.dto.v1.V1OpponentDto;
import app.oengus.adapter.rest.mapper.UserDtoMapper;
import app.oengus.application.port.persistence.GamePersistencePort;
import app.oengus.application.port.persistence.SubmissionPersistencePort;
import app.oengus.application.port.persistence.UserPersistencePort;
import app.oengus.domain.OengusUser;
import app.oengus.domain.submission.Category;
import app.oengus.domain.submission.Game;
import app.oengus.domain.submission.Submission;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpponentRestService {
    private final GamePersistencePort gamePersistencePort;
    private final UserPersistencePort userPersistencePort;
    private final SubmissionPersistencePort submissionPersistencePort;
    private final UserDtoMapper userDtoMapper;

    public void setCategoryAndGameNameOnOpponents(SubmissionDto submission, String marathonId) {
        final Map<Integer, Game> gameCache = new HashMap<>();

        final var submissionIds = collectSubmissionIds(submission, gameCache);
        final var userCache = lookupSubmissionUsers(submissionIds);

        submission.getOpponents().forEach((opponent) -> {
            this.mapOpponent(opponent, gameCache, userCache);
        });

        this.setOpponentUserFromCache(submission, userCache);
    }

    // Be careful with alterations to this method.
    // Incorrect changes may cause this mapping to take 2 fucking minutes
    public void setCategoryAndGameNameOnOpponents(Page<SubmissionDto> submissions, String marathonId) {
        final Map<Integer, Game> gameCache = new HashMap<>();

        submissions.forEach((submission) -> {
            final var submissionIds = collectSubmissionIds(submission, gameCache);
            final var userCache = lookupSubmissionUsers(submissionIds);

            this.setOpponentUserFromCache(submission, userCache);
        });
    }

    // TODO: Find a way to do even less queries
    private void mapOpponent(V1OpponentDto opponent, Map<Integer, Game> gameCache, Map<Integer, OengusUser> userCache) {
        final var catId = opponent.getCategoryId();
        final var game = lookupGame(gameCache, catId);
        final var cat = findCategory(game, catId);
        final var oppUsers = opponent.getUsers();
        final var user1 = userCache.get(game.getSubmissionId());

        oppUsers.add(this.userDtoMapper.fromDomainV1(user1));

        cat.getOpponents().forEach((oppCat) -> {
            // Ignore our own submission
            if (oppCat.getSubmissionId() == opponent.getSubmissionId()) {
                return;
            }

            final var user2 = userCache.get(oppCat.getSubmissionId());

            oppUsers.add(this.userDtoMapper.fromDomainV1(user2));
        });

        opponent.setGameName(game.getName());
        opponent.setCategoryName(cat.getName());
        opponent.setUsers(oppUsers);
    }

    // Submission id -> User
    private Map<Integer, OengusUser> lookupSubmissionUsers(List<Integer> submissionIds) {
        return this.submissionPersistencePort.findUsersByIds(submissionIds);
    }

    private Submission lookupSubmission(Map<Integer, Submission> submissionCache, int submissionId) {
        if (!submissionCache.containsKey(submissionId)) {
            submissionCache.put(
                submissionId,
                this.submissionPersistencePort.findById(submissionId).get()
            );
        }

        return submissionCache.get(submissionId);
    }

    private Game lookupGame(Map<Integer, Game> gameCache, int catId) {
        if (!gameCache.containsKey(catId)) {
            gameCache.put(
                catId,
                this.gamePersistencePort.findByCategoryId(catId).get()
            );
        }

        return gameCache.get(catId);
    }

    private static Category findCategory(Game game, int catId) {
        for (final var category : game.getCategories()) {
            if (category.getId() == catId) {
                return category;
            }
        }

        return null;
    }

    private List<Integer> collectSubmissionIds(Page<SubmissionDto> submissions, Map<Integer, Game> gameCache) {
        final List<Integer> submissionIds = new ArrayList<>();

        submissions.forEach(
            (submission) -> submissionIds.addAll(
                collectSubmissionIds(submission, gameCache)
            )
        );

        return submissionIds;
    }

    private List<Integer> collectSubmissionIds(SubmissionDto submission, Map<Integer, Game> gameCache) {
        final List<Integer> submissionIds = new ArrayList<>();

        submissionIds.add(submission.getId());

        submission.getOpponents().forEach((opponent) -> {
            submissionIds.add(opponent.getSubmissionId());

            final var game = lookupGame(gameCache, opponent.getCategoryId());

            submissionIds.add(game.getSubmissionId());

            final var category = findCategory(game, opponent.getCategoryId());

            category.getOpponents().forEach((oppCat) -> {
                submissionIds.add(oppCat.getSubmissionId());
            });
        });

        submission.getGames().forEach((game) -> {
            game.getCategories().forEach((category) -> {
                category.getOpponents().forEach((opponent) -> {
                    submissionIds.add(opponent.getSubmissionId());
                });
            });
        });

        return submissionIds.stream().distinct().toList();
    }

    private void setOpponentUserFromCache(SubmissionDto submission, Map<Integer, OengusUser> userCache) {
        submission.getGames().forEach((game) -> {
            game.getCategories().forEach((category) -> {
                category.getOpponents().forEach((opponent) -> {
                    final var subId = opponent.getSubmissionId();
                    var cachedUser = userCache.get(subId);

                    if (cachedUser == null) {
                        cachedUser = this.submissionPersistencePort.findUsersByIds(List.of(subId)).get(subId);
                    }

                    opponent.setUser(
                        this.userDtoMapper.fromDomainV1(cachedUser)
                    );
                });
            });
        });
    }
}
