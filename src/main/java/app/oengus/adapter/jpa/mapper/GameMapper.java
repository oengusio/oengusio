package app.oengus.adapter.jpa.mapper;

import app.oengus.domain.Game;
import app.oengus.entity.model.GameEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    uses = {
        //
    }
)
public interface GameMapper {
    @Mapping(target = "submissionId", source = "submission.id")
    Game toDomain(GameEntity entity);
}
