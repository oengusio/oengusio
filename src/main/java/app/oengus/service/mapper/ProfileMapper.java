package app.oengus.service.mapper;

import app.oengus.entity.dto.UserProfileDto;
import app.oengus.adapter.rest.dto.v2.users.ProfileDto;
import app.oengus.adapter.jpa.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    @Mapping(target = "history", ignore = true)
    @Mapping(target = "moderatedMarathons", ignore = true)
    @Mapping(target = "volunteeringHistory", ignore = true)
    @Mapping(target = "banned", expression = "java(user.getRoles().contains(app.oengus.spring.model.Role.ROLE_BANNED))")
    @Mapping(target = "pronouns", expression = "java(user.getPronouns() == null || user.getPronouns().isBlank() ? List.of() : List.of(user.getPronouns().split(\",\")))")
    @Mapping(target = "languagesSpoken", expression = "java(user.getLanguagesSpoken() == null ? List.of() : List.of(user.getLanguagesSpoken().split(\",\")))")
    UserProfileDto toV1Profile(User user);

    @Mapping(target = "banned", expression = "java(user.getRoles().contains(app.oengus.spring.model.Role.ROLE_BANNED))")
    @Mapping(target = "pronouns", expression = "java(user.getPronouns() == null || user.getPronouns().isBlank() ? List.of() : List.of(user.getPronouns().split(\",\")))")
    @Mapping(target = "languagesSpoken", expression = "java(user.getLanguagesSpoken() == null ? List.of() : List.of(user.getLanguagesSpoken().split(\",\")))")
    ProfileDto toProfile(User user);
}