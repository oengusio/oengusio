package app.oengus.adapter.jpa.mapper;

import app.oengus.adapter.jpa.entity.OpponentEntity;
import app.oengus.domain.Opponent;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    uses = {
        CategoryMapper.class,
        SubmissionEntityMapper.class
    }
)
public interface OpponentMapper {
    @Mapping(target = "categoryId", source = "category.id")
    Opponent toDomain(OpponentEntity entity);

    @InheritInverseConfiguration(name = "toDomain")
    OpponentEntity fromDomain(Opponent opponent);
}
