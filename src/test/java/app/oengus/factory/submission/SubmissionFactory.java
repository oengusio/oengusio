package app.oengus.factory.submission;

import app.oengus.domain.submission.Submission;
import app.oengus.factory.AbstractFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class SubmissionFactory extends AbstractFactory<Submission> {
    @NotNull
    @Override
    public Submission getObject() {
        return this.withMarathonId(
            faker.random().hex()
        );
    }

    public Submission withMarathonId(String marathonId) {
        return new Submission(-1, marathonId);
    }

    @Override
    public Class<?> getObjectType() {
        return Submission.class;
    }
}
