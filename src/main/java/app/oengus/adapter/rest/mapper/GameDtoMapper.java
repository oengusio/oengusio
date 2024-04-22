package app.oengus.adapter.rest.mapper;

import app.oengus.adapter.rest.dto.v1.V1GameDto;
import app.oengus.adapter.rest.dto.v2.marathon.GameDto;
import app.oengus.domain.submission.Game;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    uses = {
        CategoryDtoMapper.class,
    }
)
public interface GameDtoMapper {
    V1GameDto v1FromDomain(Game game);

    GameDto fromDomain(Game game);
}
