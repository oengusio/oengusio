package app.oengus.adapter.rest.mapper;

import app.oengus.adapter.jpa.entity.User;
import app.oengus.adapter.rest.dto.v1.V1UserDto;
import app.oengus.domain.OengusUser;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    uses = {
        //
    }
)
public interface UserDtoMapper {
    V1UserDto fromDomain(OengusUser user);

    @Deprecated(forRemoval = true)
    @Mapping(target = "pronouns", expression = "java(user.getPronouns() == null || user.getPronouns().isBlank() ? List.of() : List.of(user.getPronouns().split(\",\")))")
    @Mapping(target = "languagesSpoken", expression = "java(user.getLanguagesSpoken() == null ? List.of() : List.of(user.getLanguagesSpoken().split(\",\")))")
    V1UserDto fromDbModel(User user);
}
