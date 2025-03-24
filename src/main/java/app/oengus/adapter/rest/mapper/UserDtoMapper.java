package app.oengus.adapter.rest.mapper;

import app.oengus.adapter.rest.dto.UserProfileDto;
import app.oengus.adapter.rest.dto.v1.V1UserDto;
import app.oengus.adapter.rest.dto.v2.users.*;
import app.oengus.adapter.rest.dto.v2.users.request.UserUpdateRequest;
import app.oengus.domain.OengusUser;
import app.oengus.domain.marathon.Marathon;
import app.oengus.domain.user.SubmissionHistoryEntry;
import app.oengus.domain.user.SupporterStatus;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    uses = {
        GameDtoMapper.class,
    }
)
public interface UserDtoMapper {
    V1UserDto fromDomainV1(OengusUser user);

    SelfUserDto selfUserFromDomain(OengusUser user);

    @Mapping(target = "history", ignore = true)
    @Mapping(target = "moderatedMarathons", ignore = true)
    @Mapping(target = "volunteeringHistory", ignore = true)
    @Mapping(target = "banned", expression = "java(user.getRoles().contains(app.oengus.domain.Role.ROLE_BANNED))")
    UserProfileDto profileFromDomain(OengusUser user);

    @Mapping(target = "banned", expression = "java(user.getRoles().contains(app.oengus.domain.Role.ROLE_BANNED))")
    ProfileDto v2ProfileFromDomain(OengusUser user);

    void applyPatch(@MappingTarget OengusUser user, UserUpdateRequest userPatch);

    @Mapping(target = "marathonId", source = "marathon.id")
    @Mapping(target = "marathonName", source = "marathon.name")
    @Mapping(target = "marathonStartDate", source = "marathon.startDate")
    ProfileHistoryDto fromDomain(SubmissionHistoryEntry entry);

    @Mapping(target = "marathonId", source = "marathon.id")
    @Mapping(target = "marathonName", source = "marathon.name")
    @Mapping(target = "marathonStartDate", source = "marathon.startDate")
    ModeratedHistoryDto fromDomainMarathon(Marathon marathon);

    SupporterStatusDto fromDomain(SupporterStatus status);
}
