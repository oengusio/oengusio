package app.oengus.factory.schedule;

import app.oengus.domain.schedule.Schedule;
import app.oengus.factory.AbstractFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ScheduleFactory extends AbstractFactory<Schedule> {
    private final AtomicInteger idStore = new AtomicInteger();

    @NotNull
    @Override
    public Schedule getObject() {
        final var schedule = new Schedule(this.idStore.incrementAndGet());

        schedule.setName(faker.name().name());
        schedule.setSlug(faker.buffy().bigBads());
        schedule.setPublished(faker.bool().bool());

        return schedule;
    }

    @Override
    public Class<Schedule> getObjectType() {
        return Schedule.class;
    }
}
