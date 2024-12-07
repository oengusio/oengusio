package app.oengus.adapter.rest.mapper;

import app.oengus.adapter.rest.dto.v2.schedule.*;
import app.oengus.adapter.rest.dto.v2.schedule.request.ScheduleUpdateRequestDto;
import app.oengus.domain.schedule.Line;
import app.oengus.domain.schedule.Runner;
import app.oengus.domain.schedule.Schedule;
import app.oengus.domain.schedule.Ticker;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    uses = {
        UserDtoMapper.class,
    }
)
public interface ScheduleDtoMapper {
    ScheduleTickerDto tickerToDto(Ticker ticker);

    ScheduleInfoDto infoFromSchedule(Schedule schedule);

    Schedule toDomain(ScheduleUpdateRequestDto createRequest);

    void applyPatch(@MappingTarget Schedule schedule, ScheduleUpdateRequestDto patchRequest);

    ScheduleDto fromDomain(Schedule schedule);

    @Mapping(target = "gameName", source = "game")
    @Mapping(target = "categoryName", source = "category")
    Line toDomain(LineDto dto);

    @InheritInverseConfiguration(name = "toDomain")
    LineDto fromDomain(Line line);

    @Mapping(target = "profile", source = "user")
    LineRunnerDto fromDomain(Runner runner);

    @Mapping(target = "user", source = "profile")
    Runner toDomain(LineRunnerDto dto);
}
