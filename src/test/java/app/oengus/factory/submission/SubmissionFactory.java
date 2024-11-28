package app.oengus.factory.submission;

import app.oengus.domain.submission.Submission;
import app.oengus.factory.AbstractFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class SubmissionFactory extends AbstractFactory<Submission> {
    private final AtomicInteger idStore = new AtomicInteger();

    @NotNull
    @Override
    public Submission getObject() {
        return this.withMarathonId(
            faker.random().hex()
        );
    }

    public Submission withMarathonId(String marathonId) {
        return new Submission(this.idStore.incrementAndGet(), marathonId);
    }

    @Override
    public Class<?> getObjectType() {
        return Submission.class;
    }
}
