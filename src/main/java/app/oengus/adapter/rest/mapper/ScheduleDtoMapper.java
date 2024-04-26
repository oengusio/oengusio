package app.oengus.adapter.rest.mapper;

import app.oengus.adapter.jpa.entity.SocialAccount;
import app.oengus.adapter.rest.dto.v1.request.SimpleUserDto;
import app.oengus.adapter.rest.dto.v2.schedule.*;
import app.oengus.adapter.rest.dto.v2.schedule.request.ScheduleUpdateRequestDto;
import app.oengus.domain.Connection;
import app.oengus.domain.OengusUser;
import app.oengus.domain.schedule.Line;
import app.oengus.domain.schedule.Runner;
import app.oengus.domain.schedule.Schedule;
import app.oengus.domain.schedule.Ticker;
import app.oengus.adapter.rest.dto.UserProfileDto;
import app.oengus.adapter.rest.dto.V1ScheduleDto;
import app.oengus.domain.Role;
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
            dto.setConnections(List.of());
            dto.setLanguagesSpoken(null);
            dto.setBanned(false);
            dto.setCountry(null);
        } else {
            dto.setId(user.getId());
            dto.setEmailVerified(user.isEmailVerified());
            dto.setUsername(user.getUsername());
            dto.setDisplayName(user.getDisplayName());
            dto.setEnabled(user.isEnabled());
            dto.setConnections(this.fromConnections(user.getConnections()));
            dto.setPronouns(user.getPronouns());
            dto.setLanguagesSpoken(user.getLanguagesSpoken());
            dto.setBanned(user.getRoles().contains(Role.ROLE_BANNED));
            dto.setCountry(user.getCountry());
        }

        return dto;
    }

    // TODO: AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
    //  WHY ARE YOU USING A DATABASE MODEL
    List<SocialAccount> fromConnections(List<Connection> connections);

    ScheduleTickerDto tickerToDto(Ticker ticker);

    @Mapping(target = "id", source = "id")
    Schedule fromV1UpdateRequest(app.oengus.adapter.rest.dto.v1.request.ScheduleUpdateRequestDto requestDto);

    Line fromV1UpdateRequest(app.oengus.adapter.rest.dto.v1.request.ScheduleUpdateRequestDto.Line requestLine);

    Runner fromV1UpdateRequest(app.oengus.adapter.rest.dto.v1.request.ScheduleUpdateRequestDto.LineRunner lineRunner);

    OengusUser fromV1UpdateRequest(SimpleUserDto lineUser);

    ScheduleInfoDto infoFromSchedule(Schedule schedule);

    Schedule toDomain(ScheduleUpdateRequestDto createRequest);

    ScheduleDto fromDomain(Schedule schedule);

    LineDto fromDomain(Line line);

    @Mapping(target = "profile", source = "user")
    LineRunnerDto fromDomain(Runner runner);
}
