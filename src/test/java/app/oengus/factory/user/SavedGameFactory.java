package app.oengus.factory.user;

import app.oengus.domain.submission.Game;
import app.oengus.domain.user.SavedGame;
import app.oengus.factory.AbstractFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import static app.oengus.util.StringUtils.limit;

@Component
public class SavedGameFactory extends AbstractFactory<SavedGame> {
    @NotNull
    @Override
    public SavedGame getObject() {
        return this.withUserId(faker.number().randomDigit());
    }

    public SavedGame withUserId(int userId) {
        final var game = new SavedGame(-1, userId);

        game.setName(faker.appliance().equipment());
        game.setDescription(limit(faker.lorem().paragraph(10), Game.DESCRIPTION_MAX_LENGTH));
        game.setConsole(faker.videoGame().platform());
        game.setRatio(faker.options().option("4:3", "16:9", "19:6", "3:2"));
        game.setEmulated(faker.bool().bool());

        return game;
    }

    @Override
    public Class<?> getObjectType() {
        return SavedGame.class;
    }
}
