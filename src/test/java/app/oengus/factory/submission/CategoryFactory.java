package app.oengus.factory.submission;

import app.oengus.domain.submission.Category;
import app.oengus.domain.submission.RunType;
import app.oengus.factory.AbstractFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import static app.oengus.util.StringUtils.limit;

@Component
public class CategoryFactory extends AbstractFactory<Category> {
    @NotNull
    @Override
    public Category getObject() {
        return this.getCategoryForGame(faker.number().randomDigit());
    }

    public Category getCategoryForGame(int gameId) {
        final var category = new Category(
            -1,
            gameId
        );

        category.setName(faker.leagueOfLegends().champion() + "%");
        category.setEstimate(faker.duration().atMostHours(69).plusSeconds(10));
        category.setDescription(limit(faker.backToTheFuture().quote(), Category.DESCRIPTION_MAX_LENGTH));
        category.setVideo(limit(faker.internet().url(), Category.VIDEO_MAX_LENGTH));
        category.setType(faker.options().option(RunType.values()));
        category.setCode(faker.letterify("??????"));

        return category;
    }

    @Override
    public Class<?> getObjectType() {
        return Category.class;
    }
}
