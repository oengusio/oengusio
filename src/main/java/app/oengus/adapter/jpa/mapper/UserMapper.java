package app.oengus.adapter.jpa.mapper;

import app.oengus.domain.OengusUser;
import app.oengus.adapter.jpa.entity.User;
import org.mapstruct.*;

import javax.annotation.Nonnull;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, // injection strategy only needed when "uses" is filled
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    uses = {
        SocialAccountMapper.class,
    }
)
public interface UserMapper {
    @Nonnull
    @BeanMapping(ignoreUnmappedSourceProperties = { "pronouns", "languagesSpoken" })
    @Mapping(target = "hashedPassword", source = "password")
    @Mapping(target = "mail", source = "email")
    @Mapping(target = "pronouns", expression = "java(String.join(\",\", user.getPronouns()))")
    @Mapping(target = "languagesSpoken", expression = "java(String.join(\",\", user.getLanguagesSpoken()))")
    User fromDomain(@Nonnull OengusUser user);

    @Nonnull
    @BeanMapping(ignoreUnmappedSourceProperties = { "atLeastOneAccountSynchronized", "emailPresentForExistingUser", "pronouns", "languagesSpoken" })
    @Mapping(target = "password", source = "hashedPassword")
    @Mapping(target = "email", source = "mail")
    @Mapping(target = "pronouns", expression = "java(user.getPronouns() == null || user.getPronouns().isBlank() ? List.of() : List.of(user.getPronouns().split(\",\")))")
    @Mapping(target = "languagesSpoken", expression = "java(user.getLanguagesSpoken() == null ? List.of() : List.of(user.getLanguagesSpoken().split(\",\")))")
    OengusUser toDomain(@Nonnull User user);
}
