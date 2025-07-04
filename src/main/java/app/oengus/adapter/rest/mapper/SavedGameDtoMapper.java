package app.oengus.adapter.rest.mapper;

import app.oengus.adapter.rest.dto.v2.users.savedGames.SavedGameDto;
import app.oengus.domain.user.SavedGame;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    uses = {
//        CategoryDtoMapper.class,
    }
)
public interface SavedGameDtoMapper {
    @BeanMapping(ignoreUnmappedSourceProperties = { "userId" })
    SavedGameDto fromDomain(SavedGame savedGame);
}
