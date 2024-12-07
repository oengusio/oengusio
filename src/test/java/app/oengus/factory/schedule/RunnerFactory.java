package app.oengus.factory.schedule;

import app.oengus.domain.schedule.Runner;
import app.oengus.factory.AbstractFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class RunnerFactory extends AbstractFactory<Runner> {
    @NotNull
    @Override
    public Runner getObject() {
        final var runner = new Runner();

        runner.setRunnerName(
            "%s%s%s".formatted(
                faker.hipster().word(),
                faker.internet().domainWord(),
                faker.university().name()
            )
        );

        return runner;
    }

    @Override
    public Class<Runner> getObjectType() {
        return Runner.class;
    }
}
