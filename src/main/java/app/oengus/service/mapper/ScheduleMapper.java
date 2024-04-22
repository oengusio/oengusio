package app.oengus.service.mapper;

import app.oengus.adapter.rest.dto.v2.schedule.ScheduleInfoDto;
import app.oengus.adapter.jpa.entity.ScheduleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "marathonId", expression = "java(schedule.getMarathon().getId())")
    ScheduleInfoDto toScheduleInfo(ScheduleEntity schedule);

    // TODO: need to get mappings for custom data sorted
    // ScheduleDto toDto(Schedule schedule);
}
