package app.oengus.adapter.jpa.mapper;

import app.oengus.domain.submission.Game;
import app.oengus.adapter.jpa.entity.GameEntity;
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

    @Mapping(target = "submission", ignore = true)
    @Mapping(target = "submission.id", source = "submissionId")
//    @InheritInverseConfiguration(name = "toDomain")
    GameEntity fromDomain(Game game);
}
