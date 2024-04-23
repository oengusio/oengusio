package app.oengus.adapter.rest.mapper;

import app.oengus.adapter.rest.dto.UserProfileDto;
import app.oengus.adapter.rest.dto.v1.UserDto;
import app.oengus.adapter.rest.dto.v1.V1UserDto;
import app.oengus.adapter.rest.dto.v2.users.ModeratedHistoryDto;
import app.oengus.adapter.rest.dto.v2.users.ProfileDto;
import app.oengus.adapter.rest.dto.v2.users.ProfileHistoryDto;
import app.oengus.domain.OengusUser;
import app.oengus.domain.marathon.Marathon;
import app.oengus.domain.user.SubmissionHistoryEntry;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    uses = {
        GameDtoMapper.class,
    }
)
public interface UserDtoMapper {
    V1UserDto fromDomain(OengusUser user);

    @Mapping(target = "history", ignore = true)
    @Mapping(target = "moderatedMarathons", ignore = true)
    @Mapping(target = "volunteeringHistory", ignore = true)
    @Mapping(target = "banned", expression = "java(user.getRoles().contains(app.oengus.domain.Role.ROLE_BANNED))")
    UserProfileDto profileFromDomain(OengusUser user);

    @Mapping(target = "banned", expression = "java(user.getRoles().contains(app.oengus.domain.Role.ROLE_BANNED))")
    ProfileDto v2ProfileFromDomain(OengusUser user);

    @Mapping(target = "pronouns", expression = "java(userPatch.getPronouns() == null || userPatch.getPronouns().isBlank() ? List.of() : List.of(userPatch.getPronouns().split(\",\")))")
    @Mapping(target = "languagesSpoken", expression = "java(userPatch.getLanguagesSpoken() == null || userPatch.getLanguagesSpoken().isBlank() ? List.of() : List.of(userPatch.getLanguagesSpoken().split(\",\")))")
    void applyV1Patch(@MappingTarget OengusUser user, UserDto userPatch);

    @Mapping(target = "marathonId", source = "marathon.id")
    @Mapping(target = "marathonName", source = "marathon.name")
    @Mapping(target = "marathonStartDate", source = "marathon.startDate")
    ProfileHistoryDto fromDomain(SubmissionHistoryEntry entry);

    @Mapping(target = "marathonId", source = "marathon.id")
    @Mapping(target = "marathonName", source = "marathon.name")
    @Mapping(target = "marathonStartDate", source = "marathon.startDate")
    ModeratedHistoryDto fromDomainMarathon(Marathon marathon);
}
