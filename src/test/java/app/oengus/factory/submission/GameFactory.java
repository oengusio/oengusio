package app.oengus.factory.submission;

import app.oengus.domain.submission.Game;
import app.oengus.factory.AbstractFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class GameFactory extends AbstractFactory<Game> {
    private final AtomicInteger idStore = new AtomicInteger();

    @NotNull
    @Override
    public Game getObject() {
        return this.withSubmissionId(faker.number().randomDigit());
    }

    public Game withSubmissionId(int submissionId) {
        final var game = new Game(this.idStore.incrementAndGet(), submissionId);

        game.setName(faker.appliance().equipment());
        game.setDescription(faker.lorem().paragraph(10));
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
