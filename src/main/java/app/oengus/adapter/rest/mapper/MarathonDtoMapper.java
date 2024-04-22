package app.oengus.adapter.rest.mapper;

import app.oengus.adapter.rest.dto.v1.MarathonBasicInfoDto;
import app.oengus.adapter.rest.dto.v1.request.MarathonCreateRequestDto;
import app.oengus.adapter.rest.dto.v1.request.MarathonUpdateRequestDto;
import app.oengus.domain.marathon.Marathon;
import app.oengus.domain.marathon.MarathonStats;
import app.oengus.adapter.rest.dto.MarathonDto;
import app.oengus.adapter.rest.dto.MarathonStatsDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    uses = {
        UserDtoMapper.class,
    }
)
public interface MarathonDtoMapper {
    MarathonBasicInfoDto toBasicInfo(Marathon marathon);

    // TODO: fix dto
    @Mapping(target = "submitsOpen", source = "submissionsOpen")
    MarathonDto toDto(Marathon marathon);

    MarathonStatsDto statsFromDomain(MarathonStats stats);

    void applyCreateRequest(@MappingTarget Marathon marathon, MarathonCreateRequestDto createRequest);

    @Mapping(target = "discordPrivate", source = "discordPrivacy")
    @Mapping(target = "submissionsOpen", source = "submitsOpen")
    void applyUpdateRequest(@MappingTarget Marathon marathon, MarathonUpdateRequestDto createRequest);
}
