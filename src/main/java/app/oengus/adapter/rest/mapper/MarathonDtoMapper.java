package app.oengus.adapter.rest.mapper;

import app.oengus.adapter.rest.dto.MarathonDto;
import app.oengus.adapter.rest.dto.MarathonStatsDto;
import app.oengus.adapter.rest.dto.v1.MarathonBasicInfoDto;
import app.oengus.adapter.rest.dto.v1.V1QuestionDto;
import app.oengus.adapter.rest.dto.v1.request.MarathonCreateRequestDto;
import app.oengus.adapter.rest.dto.v2.marathon.MarathonSettingsDto;
import app.oengus.domain.marathon.Marathon;
import app.oengus.domain.marathon.MarathonStats;
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
    @Mapping(target = "discordPrivacy", source = "discordPrivate")
    @Mapping(target = "submitsOpen", source = "submissionsOpen")
    MarathonDto toDto(Marathon marathon);

    MarathonStatsDto statsFromDomain(MarathonStats stats);

    void applyCreateRequest(@MappingTarget Marathon marathon, MarathonCreateRequestDto createRequest);

    @Mapping(target = "questionType", source = "type")
    V1QuestionDto questionToV1QuestionDto(Question question);

    @InheritInverseConfiguration(name = "questionToV1QuestionDto")
    Question questionFromV1Dto(V1QuestionDto dto);

    @Mapping(target = "isPrivate", source = "private")
    @Mapping(target = "discordPrivate", source = "discordPrivate")
    @Mapping(target = "allowEmulators", source = "emulatorAuthorized")
    @Mapping(target = "allowMultiplayer", source = "hasMultiplayer")
    MarathonSettingsDto toSettingsDto(Marathon marathon);

    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "moderators", ignore = true)
    @Mapping(target = "questions", ignore = true)
    @InheritInverseConfiguration(name = "toSettingsDto")
    Marathon fromSettingsDto(MarathonSettingsDto dto);


    @Mapping(target = "private", source = "isPrivate")
    @Mapping(target = "emulatorAuthorized", source = "allowEmulators")
    @Mapping(target = "hasMultiplayer", source = "allowMultiplayer")
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "moderators", ignore = true)
    @Mapping(target = "questions", ignore = true)
    @Mapping(target = "canEditSubmissions", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy =  NullValuePropertyMappingStrategy.IGNORE)
    void applyUpdateRequest(@MappingTarget Marathon marathon, MarathonSettingsDto createRequest);
}
