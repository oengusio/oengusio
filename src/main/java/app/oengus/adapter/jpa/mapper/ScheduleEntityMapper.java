package app.oengus.adapter.jpa.mapper;

import app.oengus.adapter.jpa.entity.ScheduleEntity;
import app.oengus.domain.schedule.Schedule;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    uses = {
        ScheduleLineMapper.class,
    }
)
// TODO: un-ignore name when we have one.
public interface ScheduleEntityMapper {
    @Mapping(target = "marathonId", source = "marathon.id")
    Schedule toDomain(ScheduleEntity entity);

    @BeanMapping(ignoreUnmappedSourceProperties = { "lines" })
    @Mapping(target = "marathonId", source = "marathon.id")
    @Mapping(target = "lines", ignore = true)
    Schedule toDomainNoLines(ScheduleEntity entity);

    @InheritInverseConfiguration(name = "toDomain")
    ScheduleEntity fromDomain(Schedule schedule);
}
