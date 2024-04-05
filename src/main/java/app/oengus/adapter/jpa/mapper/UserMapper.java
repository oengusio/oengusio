package app.oengus.adapter.jpa.mapper;

import app.oengus.domain.OengusUser;
import app.oengus.entity.model.User;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import javax.annotation.Nonnull;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, // injection strategy only needed when "uses" is filled
    uses = {
        // TODO: connection mapper
    }
)
public interface UserMapper {
    @Nonnull
    @Mapping(target = "pronouns", expression = "java(String.join(\",\", user.getPronouns()))")
    @Mapping(target = "languagesSpoken", expression = "java(String.join(\",\", user.getLanguagesSpoken()))")
    User fromDomain(@Nonnull OengusUser user);

    @Nonnull
    @Mapping(target = "pronouns", expression = "java(user.getPronouns() == null || user.getPronouns().isBlank() ? List.of() : List.of(user.getPronouns().split(\",\")))")
    @Mapping(target = "languagesSpoken", expression = "java(user.getLanguagesSpoken() == null ? List.of() : List.of(user.getLanguagesSpoken().split(\",\")))")
    OengusUser toDomain(@Nonnull User user);
}
