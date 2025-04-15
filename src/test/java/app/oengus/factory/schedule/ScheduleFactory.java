package app.oengus.factory.schedule;

import app.oengus.domain.schedule.Schedule;
import app.oengus.factory.AbstractFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class ScheduleFactory extends AbstractFactory<Schedule> {

    @NotNull
    @Override
    public Schedule getObject() {
        final var schedule = new Schedule(-1);

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
