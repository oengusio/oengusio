package app.oengus.adapter.jpa.mapper;

import app.oengus.adapter.jpa.entity.OpponentEntity;
import app.oengus.domain.Opponent;
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
        SubmissionEntityMapper.class
    }
)
public interface OpponentMapper {
    @Mapping(target = "categoryId", source = "category.id")
    Opponent toDomain(OpponentEntity entity);
}
