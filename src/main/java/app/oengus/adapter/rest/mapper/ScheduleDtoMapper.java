package app.oengus.adapter.rest.mapper;

import app.oengus.adapter.rest.dto.v1.request.ScheduleUpdateRequestDto;
import app.oengus.adapter.rest.dto.v2.schedule.*;
import app.oengus.domain.OengusUser;
import app.oengus.domain.schedule.Line;
import app.oengus.domain.schedule.Runner;
import app.oengus.domain.schedule.Schedule;
import app.oengus.domain.schedule.Ticker;
import app.oengus.entity.dto.UserProfileDto;
import app.oengus.entity.dto.V1ScheduleDto;
import app.oengus.spring.model.Role;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    uses = {
        UserDtoMapper.class,
    }
)
public interface ScheduleDtoMapper {
    V1ScheduleDto toV1Dto(Schedule schedule);

    // TODO: This is awful, can't wait to dump the v1 api.
    default UserProfileDto runnerToUserProfileDto(Runner runner) {
        final var dto = new UserProfileDto();
        final var user = runner.getUser();

        if (user == null) {
            dto.setId(-1);
            dto.setUsername(runner.getRunnerName());
            dto.setDisplayName(runner.getRunnerName());
            dto.setEnabled(true);
            dto.setConnections(List.of());
            dto.setPronouns(List.of());
            dto.setLanguagesSpoken(null);
            dto.setBanned(false);
            dto.setCountry(null);
        } else {
            dto.setId(user.getId());
            dto.setEmailVerified(user.isEmailVerified());
            dto.setUsername(user.getUsername());
            dto.setDisplayName(user.getDisplayName());
            dto.setEnabled(user.isEnabled());
//            dto.setConnections(user.getConnections());
            dto.setPronouns(user.getPronouns());
            dto.setLanguagesSpoken(user.getLanguagesSpoken());
            dto.setBanned(user.getRoles().contains(Role.ROLE_BANNED));
            dto.setCountry(user.getCountry());
        }

        return dto;
    }

    ScheduleTickerDto tickerToDto(Ticker ticker);

    Schedule fromV1UpdateRequest(ScheduleUpdateRequestDto requestDto);

    Line fromV1UpdateRequest(ScheduleUpdateRequestDto.Line requestLine);

    Runner fromV1UpdateRequest(ScheduleUpdateRequestDto.LineRunner lineRunner);

    OengusUser fromV1UpdateRequest(ScheduleUpdateRequestDto.SimpleUser lineUser);

    ScheduleInfoDto infoFromSchedule(Schedule schedule);

    ScheduleDto fromDomain(Schedule schedule);

    LineDto fromDomain(Line line);

    @Mapping(target = "profile", source = "user")
    LineRunnerDto fromDomain(Runner runner);
}
