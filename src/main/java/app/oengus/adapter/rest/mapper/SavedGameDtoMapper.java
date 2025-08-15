package app.oengus.adapter.rest.mapper;

import app.oengus.adapter.rest.dto.v2.users.savedGames.SavedGameCreateDto;
import app.oengus.adapter.rest.dto.v2.users.savedGames.SavedGameDto;
import app.oengus.adapter.rest.dto.v2.users.savedGames.SavedGameUpdateDto;
import app.oengus.domain.user.SavedGame;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    uses = {
        SavedCategoryDtoMapper.class,
    }
)
public interface SavedGameDtoMapper {
    @BeanMapping(ignoreUnmappedSourceProperties = { "userId" })
    SavedGameDto fromDomain(SavedGame savedGame);

    SavedGame toDomain(SavedGameDto dto, int userId);

    @Mapping(target = "id", ignore = true)
    SavedGame createToDomain(SavedGameCreateDto dto, int userId);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void applyPatch(@MappingTarget SavedGame game, SavedGameUpdateDto patch);
}
