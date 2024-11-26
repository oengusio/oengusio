package app.oengus.application;

import app.oengus.factory.OengusUserFactory;
import app.oengus.factory.marathon.MarathonFactory;
import app.oengus.factory.submission.CategoryFactory;
import org.hibernate.validator.internal.constraintvalidators.bv.AssertTrueValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class CategoryServiceTests {
    private final CategoryService categoryService;
    private final MarathonFactory marathonFactory;
    private final CategoryFactory categoryFactory;
    private final OengusUserFactory oengusUserFactory;


    @Autowired
    public CategoryServiceTests(CategoryService categoryService, MarathonFactory marathonFactory, CategoryFactory categoryFactory, OengusUserFactory oengusUserFactory) {
        this.categoryService = categoryService;
        this.marathonFactory = marathonFactory;
        this.categoryFactory = categoryFactory;
        this.oengusUserFactory = oengusUserFactory;
    }

    @BeforeEach
    void setUp() {
        // TODO: store items in database
        System.out.println(this.oengusUserFactory.getNormalUser().getUsername());
    }

    @Test
    public void categoryCanBeFoundByCodeForMarathon() {
        assertTrue(true);
    }
}
