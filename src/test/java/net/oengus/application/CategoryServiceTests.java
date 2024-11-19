package net.oengus.application;

import app.oengus.OengusApplication;
import app.oengus.application.CategoryService;
import net.oengus.factory.OengusUserFactory;
import net.oengus.factory.marathon.MarathonFactory;
import net.oengus.factory.submission.CategoryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
        //
    }
}
