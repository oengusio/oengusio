package app.oengus.factory.submission;

import app.oengus.domain.submission.Opponent;
import app.oengus.factory.AbstractFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class OpponentFactory extends AbstractFactory<Opponent> {
    private final AtomicInteger idStore = new AtomicInteger();

    @NotNull
    @Override
    public Opponent getObject() {
        return this.getOpponent(this.idStore.incrementAndGet());
    }

    public Opponent getOpponent(int categoryId) {
        final var opponent = new Opponent(this.idStore.incrementAndGet(), categoryId);

        opponent.setVideo(faker.internet().url());

        return opponent;
    }

    @Override
    public Class<?> getObjectType() {
        return Opponent.class;
    }
}
