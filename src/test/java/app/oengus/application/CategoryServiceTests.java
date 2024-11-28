package app.oengus.application;

import app.oengus.application.port.persistence.CategoryPersistencePort;
import app.oengus.application.port.persistence.MarathonPersistencePort;
import app.oengus.application.port.persistence.SubmissionPersistencePort;
import app.oengus.domain.exception.OengusBusinessException;
import app.oengus.domain.marathon.Marathon;
import app.oengus.domain.submission.RunType;
import app.oengus.domain.submission.Submission;
import app.oengus.factory.OengusUserFactory;
import app.oengus.factory.marathon.MarathonFactory;
import app.oengus.factory.submission.CategoryFactory;
import app.oengus.factory.submission.GameFactory;
import app.oengus.factory.submission.SubmissionFactory;
import app.oengus.util.SubmissionHelpers;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = { @Autowired })
public class CategoryServiceTests {
    private final CategoryService categoryService;
    private final MarathonFactory marathonFactory;
    private final SubmissionFactory submissionFactory;
    private final GameFactory gameFactory;
    private final CategoryFactory categoryFactory;
    private final OengusUserFactory oengusUserFactory;
    private final MarathonPersistencePort marathonPersistencePort;
    private final SubmissionPersistencePort submissionPersistencePort;
    private final CategoryPersistencePort categoryPersistencePort;

    private final SubmissionHelpers submissionHelpers;

    @Test
    public void testCategoriesCanBeFoundByGameId() {
        final var pair = this.createMarathonAndSubmission("OENGUS");
        final var marathon = pair.getLeft();
        final var submission = pair.getRight();
        final var game = submission.getGames().stream().findFirst().orElseThrow();
        final var firstCat = game.getCategories().get(0);

        final var category = this.categoryFactory.getCategoryForGame(game.getId());

        category.setType(RunType.SINGLE);

        this.categoryPersistencePort.save(category);

        final var foundCategories = this.categoryService.findByGameId(
            marathon.getId(), submission.getId(), game.getId()
        );

        assertEquals(2, foundCategories.size());

        assertEquals(firstCat, foundCategories.get(0));
        assertEquals(category, foundCategories.get(1));
    }

    @Test
    public void testCategoryCanBeFoundByCodeForMarathon() {
        final var pair = this.createMarathonAndSubmission("OENGUS");
        final var marathon = pair.getLeft();
        final var submission = pair.getRight();
        final var game = submission.getGames().stream().findFirst().orElseThrow();
        final var category = game.getCategories().get(0);

        final var categoryRes = this.categoryService.findCategoryByCode(marathon.getId(), "OENGUS");
        final var foundCategory = categoryRes.getLeft();

        assertNotNull(foundCategory);
        assertEquals(foundCategory, category);
    }

    @Test
    public void testUserCanJoinSubmissionBeforeMaxScreenCountIsReached() {
        final var pair = this.createMarathonAndSubmission("MULTI1");
        final var marathon = pair.getLeft();

        marathon.setMaxNumberOfScreens(3);

        final var submission = pair.getRight();
        final var game = submission.getGames().stream().findFirst().orElseThrow();
        final var category = game.getCategories().get(0);

        this.submissionHelpers.addOpponents(1, category, marathon.getId());

        final var categoryRes = this.categoryService.findCategoryByCode(marathon.getId(), "MULTI1");
        final var foundCategory = categoryRes.getLeft();
        final var foundUsers = categoryRes.getRight();

        assertEquals(2, foundUsers.size());
    }

    @Test
    public void testUserCannotFetchCodeIfSubmissionIsFull() {
        final var pair = this.createMarathonAndSubmission("MULTI2");
        final var marathon = pair.getLeft();

        marathon.setMaxNumberOfScreens(3);

        final var submission = pair.getRight();
        final var game = submission.getGames().stream().findFirst().orElseThrow();
        final var category = game.getCategories().get(0);

        // We're adding 2 opponents to make 3 as the original submitter is also included.
        this.submissionHelpers.addOpponents(2, category, marathon.getId());

        assertThrows(
            OengusBusinessException.class,
            () -> this.categoryService.findCategoryByCode(marathon.getId(), "MULTI2")
        );
    }

    private Pair<Marathon, Submission> createMarathonAndSubmission(String code) {
        final var marathon = this.marathonFactory.getObject();

        this.marathonPersistencePort.save(marathon);

        final var submission = this.submissionFactory.withMarathonId(marathon.getId());

        submission.setUser(this.oengusUserFactory.getNormalUser());

        final var game = this.gameFactory.withSubmissionId(submission.getId());
        final var category = this.categoryFactory.getCategoryForGame(game.getId());

        category.setCode(code);
        category.setType(RunType.COOP);

        this.categoryPersistencePort.save(category);

        game.getCategories().add(category);

        submission.setGames(Set.of(game));

        this.submissionPersistencePort.save(submission);


        return Pair.of(marathon, submission);
    }
}
