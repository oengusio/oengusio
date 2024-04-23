package app.oengus.adapter.rest.mapper;

import app.oengus.adapter.rest.dto.v1.MarathonBasicInfoDto;
import app.oengus.adapter.rest.dto.v1.V1QuestionDto;
import app.oengus.adapter.rest.dto.v1.request.MarathonCreateRequestDto;
import app.oengus.adapter.rest.dto.v1.request.MarathonUpdateRequestDto;
import app.oengus.domain.marathon.Marathon;
import app.oengus.domain.marathon.MarathonStats;
import app.oengus.adapter.rest.dto.MarathonDto;
import app.oengus.adapter.rest.dto.MarathonStatsDto;
import app.oengus.domain.marathon.Question;
import org.mapstruct.*;

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

    @Mapping(target = "questionType", source = "type")
    V1QuestionDto questionToV1QuestionDto(Question question);

    @InheritInverseConfiguration(name = "questionToV1QuestionDto")
    Question questionFromV1Dto(V1QuestionDto dto);
}
