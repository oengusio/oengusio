package app.oengus.factory.submission;

import app.oengus.domain.submission.Game;
import app.oengus.factory.AbstractFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import static app.oengus.util.StringUtils.limit;

@Component
public class GameFactory extends AbstractFactory<Game> {
    @NotNull
    @Override
    public Game getObject() {
        return this.withSubmissionId(faker.number().randomDigit());
    }

    public Game withSubmissionId(int submissionId) {
        final var game = new Game(-1, submissionId);

        game.setName(faker.appliance().equipment());
        game.setDescription(limit(faker.lorem().paragraph(10), 500));
        game.setConsole(faker.videoGame().platform());
        game.setRatio("4:3");
        game.setEmulated(faker.bool().bool());

        return game;
    }

    @Override
    public Class<?> getObjectType() {
        return Game.class;
    }
}
