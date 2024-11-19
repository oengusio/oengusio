package net.oengus.factory.submission;

import app.oengus.domain.submission.Category;
import app.oengus.domain.submission.RunType;
import net.oengus.factory.AbstractFactory;

public class CategoryFactory extends AbstractFactory<Category> {
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
