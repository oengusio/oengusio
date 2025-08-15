package app.oengus.factory.user;

import app.oengus.domain.submission.Category;
import app.oengus.domain.user.SavedCategory;
import app.oengus.factory.AbstractFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import static app.oengus.util.StringUtils.limit;

@Component
public class SavedCategoryFactory extends AbstractFactory<SavedCategory> {
    @NotNull
    @Override
    public SavedCategory getObject() {
        return this.withGameId(faker.number().randomDigit());
    }

    public SavedCategory withGameId(int gameId) {
        final var category = new SavedCategory(-1, gameId);

        category.setName(faker.leagueOfLegends().champion() + "%");
        category.setEstimate(faker.duration().atMostHours(69).plusSeconds(10));
        category.setDescription(limit(faker.backToTheFuture().quote(), Category.DESCRIPTION_MAX_LENGTH));
        category.setVideo(limit(faker.internet().url(), Category.VIDEO_MAX_LENGTH));

        return category;
    }

    @Override
    public Class<?> getObjectType() {
        return SavedCategory.class;
    }
}
