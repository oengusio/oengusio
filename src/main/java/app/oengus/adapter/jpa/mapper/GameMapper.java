package app.oengus.adapter.jpa.mapper;

import app.oengus.domain.Game;
import app.oengus.entity.model.GameEntity;
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
public interface GameMapper {
    @Mapping(target = "submissionId", source = "submission.id")
    Game toDomain(GameEntity entity);
}
