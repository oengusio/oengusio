package app.oengus.adapter.rest;

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
        final Map<Integer, Submission> submissionCache = new HashMap<>();

        submission.getOpponents().forEach((opponent) -> {
            this.mapOpponent(opponent, gameCache, submissionCache);
        });

        submission.getGames().forEach((game) -> {
            game.getCategories().forEach((category) -> {
                category.getOpponents().forEach((opponent) -> {
                    // this.mapOpponent(opponent, gameCache, submissionCache);`
                    final var user = this.userPersistencePort.findById(opponent.getUser().id()).get();

                    opponent.setUser(
                        this.userDtoMapper.fromDomain(user)
                    );
                });
            });
        });
    }

    // Be careful with alterations to this method.
    // Incorrect changes may cause this mapping to take 2 fucking minutes
    public void setCategoryAndGameNameOnOpponents(Page<SubmissionDto> submissions, String marathonId) {
        final List<Integer> submissionIds = new ArrayList<>();

        submissions.forEach((submission) -> {
            submissionIds.add(submission.getId());

            submission.getGames().forEach((game) -> {
                game.getCategories().forEach((category) -> {
                    category.getOpponents().forEach((opponent) -> {
                        submissionIds.add(opponent.getSubmissionId());
                    });
                });
            });

            final var userCache = findSubmissionUsers(submissionIds);

            submission.getGames().forEach((game) -> {
                game.getCategories().forEach((category) -> {
                    category.getOpponents().forEach((opponent) -> {
                        final var subId = opponent.getSubmissionId();
                        var cachedUser = userCache.get(subId);

                        if (cachedUser == null) {
                            cachedUser = this.submissionPersistencePort.findUsersByIds(List.of(subId)).get(0);
                        }

                        opponent.setUser(
                            this.userDtoMapper.fromDomain(cachedUser)
                        );
                    });
                });
            });
        });
    }

    private void mapOpponent(V1OpponentDto opponent, Map<Integer, Game> gameCache, Map<Integer, Submission> submissionCache) {
        final var catId = opponent.getCategoryId();
        final var game = findGame(gameCache, catId);
        final var cat = findCategory(game, catId);
        final var oppUsers = opponent.getUsers();
        final var user1 = this.findSubmission(
            submissionCache,
            game.getSubmissionId()
        ).getUser();

        oppUsers.add(this.userDtoMapper.fromDomain(user1));

        cat.getOpponents().forEach((oppCat) -> {
            // Ignore our own submission
            if (oppCat.getSubmissionId() == opponent.getSubmissionId()) {
                return;
            }

            final var user2 = this.findSubmission(
                submissionCache,
                oppCat.getSubmissionId()
            ).getUser();

            oppUsers.add(this.userDtoMapper.fromDomain(user2));
        });

        opponent.setGameName(game.getName());
        opponent.setCategoryName(cat.getName());
        opponent.setUsers(oppUsers);
    }

//    private void mapOpponent(OpponentCategoryDto opponent, Map<Integer, Game> gameCache, Map<Integer, Submission> submissionCache) {
//        final var catId = opponent.getCategoryId();
//        final var game = findGame(gameCache, catId);
//        final var cat = findCategory(game, catId);
//        final var oppUsers = opponent.getUsers();
//        final var user1 = this.findSubmission(
//            submissionCache,
//            game.getSubmissionId()
//        ).getUser();
//
//        oppUsers.add(this.userDtoMapper.fromDomain(user1));
//
//        cat.getOpponents().forEach((oppCat) -> {
//            // Ignore our own submission
//            if (oppCat.getSubmissionId() == opponent.getSubmissionId()) {
//                return;
//            }
//
//            final var user2 = this.findSubmission(
//                submissionCache,
//                oppCat.getSubmissionId()
//            ).getUser();
//
//            oppUsers.add(this.userDtoMapper.fromDomain(user2));
//        });
//
//        opponent.setGameName(game.getName());
//        opponent.setCategoryName(cat.getName());
//        opponent.setUsers(oppUsers);
//    }

    private Map<Integer, OengusUser> findSubmissionUsers(List<Integer> submissionIds) {
        final var sortedIds = submissionIds.stream()
            .sorted(Integer::compare)
            .toList();
        final Map<Integer, OengusUser> userCache = new HashMap<>();
        final var opponentUsers = this.submissionPersistencePort.findUsersByIds(submissionIds);

        for (int i = 0; i < opponentUsers.size(); i++) {
            userCache.put(sortedIds.get(i), opponentUsers.get(i));
        }

        return userCache;
    }

    private Submission findSubmission(Map<Integer, Submission> submissionCache, int submissionId) {
        if (!submissionCache.containsKey(submissionId)) {
            submissionCache.put(
                submissionId,
                this.submissionPersistencePort.findById(submissionId).get()
            );
        }

        return submissionCache.get(submissionId);
    }

    private Game findGame(Map<Integer, Game> gameCache, int catId) {
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
}
