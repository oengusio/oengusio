package app.oengus.adapter.jpa.mapper;

import app.oengus.adapter.jpa.entity.ScheduleLine;
import app.oengus.adapter.jpa.entity.ScheduleLineRunner;
import app.oengus.domain.schedule.Line;
import app.oengus.domain.schedule.Runner;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    uses = {
        UserMapper.class,
    }
)
public interface ScheduleLineMapper {
    @BeanMapping(ignoreUnmappedSourceProperties = { "categoryId", "customDataDTO" })
    @Mapping(target = "scheduleId", source = "schedule.id")
    Line toDomain(ScheduleLine entity);

    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "customDataDTO", ignore = true)
    @Mapping(target = "schedule.id", source = "scheduleId")
    ScheduleLine fromDomain(Line line);

    Runner runnerEntityToDomain(ScheduleLineRunner runnerEntity);

    @BeanMapping(ignoreUnmappedSourceProperties = { "effectiveDisplay" })
    ScheduleLineRunner runnerEntityFromDomain(Runner runner);
}
