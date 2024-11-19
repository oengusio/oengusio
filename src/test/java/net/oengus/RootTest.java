package net.oengus;


import app.oengus.OengusApplication;
import app.oengus.adapter.security.UserSecurityAdapter;
import app.oengus.application.CategoryService;
import app.oengus.application.GameService;
import app.oengus.application.SubmissionService;
import app.oengus.application.UserLookupService;
import net.oengus.factory.OengusUserFactory;
import net.oengus.factory.marathon.MarathonFactory;
import net.oengus.factory.submission.CategoryFactory;
import net.oengus.mock.adapter.jpa.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(classes = { OengusApplication.class })
@SpringBootConfiguration
@Import(value = {
    // TODO: find a better way of doing this
    // Oengus main imports
    CategoryService.class,
    UserSecurityAdapter.class,
    UserLookupService.class,
    GameService.class,
    SubmissionService.class,

    // Factories
    MarathonFactory.class, CategoryFactory.class, OengusUserFactory.class,

    // Database overrides
    MockApplicationPersistenceAdapter.class,
    MockUserPersistenceAdapter.class,
    MockSubmissionPersistenceAdapter.class,
    MockMarathonPersistenceAdapter.class,
    MockCategoryPersistenceAdapter.class,
    MockGamePersistenceAdapter.class,
})
public class RootTest {
    @Test
    void contextLoads() {
    }
}
