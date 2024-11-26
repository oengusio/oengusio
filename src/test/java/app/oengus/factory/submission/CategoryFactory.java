package app.oengus.factory.submission;

import app.oengus.domain.submission.Category;
import app.oengus.domain.submission.RunType;
import app.oengus.factory.AbstractFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class CategoryFactory extends AbstractFactory<Category> {
    @NotNull
    @Override
    public Category getObject() {
        return this.getCategoryForGame(faker.number().randomDigit());
    }

    public Category getCategoryForGame(int gameId) {
        final var category = new Category(
            faker.number().randomDigit(),
            gameId
        );

        category.setName(faker.leagueOfLegends().champion() + "%");
        category.setEstimate(faker.duration().atMostHours(69));
        category.setDescription(faker.backToTheFuture().quote());
        category.setVideo(faker.internet().url());
        category.setType(faker.options().option(RunType.values()));

        return category;
    }

    @Override
    public Class<?> getObjectType() {
        return Category.class;
    }
}
