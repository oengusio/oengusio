package net.oengus.application;

import app.oengus.application.CategoryService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@RequiredArgsConstructor
public class CategoryServiceTests {
    private final CategoryService categoryService;

    @BeforeEach
    void setUp() {
        // TODO: store items in database
    }

    @Test
    public void categoryCanBeFoundByCodeForMarathon() {
        //
    }
}
