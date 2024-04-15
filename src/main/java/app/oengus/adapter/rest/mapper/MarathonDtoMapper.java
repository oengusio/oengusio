package app.oengus.adapter.rest.mapper;

import app.oengus.adapter.rest.dto.v1.MarathonBasicInfoDto;
import app.oengus.adapter.rest.dto.v1.request.MarathonCreateRequestDto;
import app.oengus.adapter.rest.dto.v1.request.MarathonUpdateRequestDto;
import app.oengus.domain.marathon.Marathon;
import app.oengus.domain.marathon.MarathonStats;
import app.oengus.entity.dto.MarathonDto;
import app.oengus.entity.dto.marathon.MarathonStatsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MarathonDtoMapper {
    MarathonBasicInfoDto toBasicInfo(Marathon marathon);

    MarathonDto toDto(Marathon marathon);

    MarathonStatsDto statsFromDomain(MarathonStats stats);

    void applyCreateRequest(@MappingTarget Marathon marathon, MarathonCreateRequestDto createRequest);

    @Mapping(target = "discordPrivate", source = "discordPrivacy")
    void applyUpdateRequest(@MappingTarget Marathon marathon, MarathonUpdateRequestDto createRequest);
}
