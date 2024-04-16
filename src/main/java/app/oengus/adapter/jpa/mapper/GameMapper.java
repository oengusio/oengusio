package app.oengus.adapter.jpa.mapper;

import app.oengus.domain.Game;
import app.oengus.entity.model.GameEntity;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    uses = {
        CategoryMapper.class,
    }
)
public interface GameMapper {
    @Mapping(target = "submissionId", source = "submission.id")
    Game toDomain(GameEntity entity);

    // TODO: fix
    @Mapping(target = "submission", ignore = true)
    @Mapping(target = "fresh", ignore = true)
    @Mapping(target = "submission.id", source = "submissionId")
//    @InheritInverseConfiguration(name = "toDomain")
    GameEntity fromDomain(Game game);
}
