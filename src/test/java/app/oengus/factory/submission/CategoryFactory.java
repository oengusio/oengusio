package app.oengus.factory.submission;

import app.oengus.domain.submission.Category;
import app.oengus.domain.submission.RunType;
import app.oengus.factory.AbstractFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class CategoryFactory extends AbstractFactory<Category> {
    private final AtomicInteger idStore = new AtomicInteger();

    @NotNull
    @Override
    public Category getObject() {
        return this.getCategoryForGame(faker.number().randomDigit());
    }

    public Category getCategoryForGame(int gameId) {
        final var category = new Category(
            this.idStore.incrementAndGet(),
            gameId
        );

        category.setName(faker.leagueOfLegends().champion() + "%");
        category.setEstimate(faker.duration().atMostHours(69));
        category.setDescription(faker.backToTheFuture().quote());
        category.setVideo(faker.internet().url());
        category.setType(faker.options().option(RunType.values()));
        category.setCode(faker.random().hex(5));

        return category;
    }

    @Override
    public Class<?> getObjectType() {
        return Category.class;
    }
}
