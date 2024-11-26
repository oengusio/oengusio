package app.oengus.application;

import app.oengus.application.port.persistence.CategoryPersistencePort;
import app.oengus.application.port.persistence.MarathonPersistencePort;
import app.oengus.application.port.persistence.SubmissionPersistencePort;
import app.oengus.domain.marathon.Marathon;
import app.oengus.domain.submission.RunType;
import app.oengus.domain.submission.Submission;
import app.oengus.factory.OengusUserFactory;
import app.oengus.factory.marathon.MarathonFactory;
import app.oengus.factory.submission.CategoryFactory;
import app.oengus.factory.submission.GameFactory;
import app.oengus.factory.submission.SubmissionFactory;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
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


    @Autowired
    public CategoryServiceTests(
        CategoryService categoryService,
        MarathonFactory marathonFactory,
        SubmissionFactory submissionFactory,
        GameFactory gameFactory,
        CategoryFactory categoryFactory,
        OengusUserFactory oengusUserFactory,
        MarathonPersistencePort marathonPersistencePort,
        SubmissionPersistencePort submissionPersistencePort,
        CategoryPersistencePort categoryPersistencePort
    ) {
        this.categoryService = categoryService;
        this.marathonFactory = marathonFactory;
        this.submissionFactory = submissionFactory;
        this.gameFactory = gameFactory;
        this.categoryFactory = categoryFactory;
        this.oengusUserFactory = oengusUserFactory;
        this.marathonPersistencePort = marathonPersistencePort;
        this.submissionPersistencePort = submissionPersistencePort;
        this.categoryPersistencePort = categoryPersistencePort;
    }

    @BeforeEach
    void setUp() {
        //
    }

    @Test
    public void categoryCanBeFoundByCodeForMarathon() {
        final var pair = this.createMarathonAndSubmission();
        final var marathon = pair.getLeft();
        final var submission = pair.getRight();
        final var game = submission.getGames().stream().findFirst().orElse(null);
        final var category = this.categoryFactory.getCategoryForGame(game.getId());

        category.setCode("OENGUS");
        category.setType(RunType.COOP);

        this.categoryPersistencePort.save(category);

        game.getCategories().add(category);

        final var categoryRes = this.categoryService.findCategoryByCode(marathon.getId(), "OENGUS");
        final var foundCategory = categoryRes.getLeft();
        final var foundUsers = categoryRes.getRight();


        assertNotNull(foundCategory);
        assertEquals(foundCategory, category);
    }

    private Pair<Marathon, Submission> createMarathonAndSubmission() {
        final var marathon = this.marathonFactory.getObject();

        this.marathonPersistencePort.save(marathon);

        final var submission = this.submissionFactory.withMarathonId(marathon.getId());

        submission.setUser(this.oengusUserFactory.getNormalUser());

        submission.setGames(Set.of(this.gameFactory.withSubmissionId(submission.getId())));

        this.submissionPersistencePort.save(submission);


        return Pair.of(marathon, submission);
    }
}
