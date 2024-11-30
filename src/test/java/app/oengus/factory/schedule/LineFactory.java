package app.oengus.factory.schedule;

import app.oengus.domain.schedule.Line;
import app.oengus.domain.submission.RunType;
import app.oengus.factory.AbstractFactory;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class LineFactory extends AbstractFactory<Line> {
    private final AtomicInteger idStore = new AtomicInteger();

    private final RunnerFactory runnerFactory;

    @NotNull
    @Override
    public Line getObject() {
        return this.getWithScheduleId(
            faker.number().randomDigit()
        );
    }

    public Line getWithScheduleId(int scheduleId) {
        final var line = new Line(this.idStore.incrementAndGet(), scheduleId);

        line.setGameName(faker.videoGame().title());
        line.setConsole(faker.videoGame().platform());
        line.setEmulated(faker.bool().bool());
        line.setRatio(
            faker.options().option("16:9", "4:3", "3:2", "2.5:1", "1.85:1", "1.78:1", "1.6:1", "10:2")
        );
        line.setCategoryName(faker.lorem().word());
        line.setEstimate(faker.duration().atMostHours(5));
        line.setSetupTime(faker.duration().atMostMinutes(15));
        line.setCustomRun(faker.bool().bool());
        // Position?
        line.setType(faker.options().option(RunType.values()));

        final var runnerCount = faker.number().numberBetween(1, 5);

        for (int i = 0; i < runnerCount; i++) {
            line.getRunners().add(
                this.runnerFactory.getObject()
            );
        }

        return line;
    }

    public Line getSetupBlockWithScheduleId(int scheduleId) {
        final var line = new Line(this.idStore.incrementAndGet(), scheduleId);

        line.setSetupBlock(true);
        line.setSetupBlockText(faker.lorem().sentence(5));

        return line;
    }

    @Override
    public Class<Line> getObjectType() {
        return Line.class;
    }
}
