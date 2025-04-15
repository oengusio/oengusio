package app.oengus.factory.submission;

import app.oengus.domain.submission.Opponent;
import app.oengus.factory.AbstractFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import static app.oengus.util.StringUtils.limit;

@Component
public class OpponentFactory extends AbstractFactory<Opponent> {
    @NotNull
    @Override
    public Opponent getObject() {
        return this.getOpponent(-1);
    }

    public Opponent getOpponent(int categoryId) {
        final var opponent = new Opponent(-1, categoryId);

        opponent.setVideo(limit(faker.internet().url(), 100));

        return opponent;
    }

    @Override
    public Class<?> getObjectType() {
        return Opponent.class;
    }
}
