package app.oengus.service.mapper;

import app.oengus.entity.dto.v2.schedule.ScheduleInfoDto;
import app.oengus.entity.model.Schedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "marathonId", expression = "java(schedule.getMarathon().getId())")
    ScheduleInfoDto toScheduleInfo(Schedule schedule);

    // TODO: need to get mappings for custom data sorted
    // ScheduleDto toDto(Schedule schedule);
}
