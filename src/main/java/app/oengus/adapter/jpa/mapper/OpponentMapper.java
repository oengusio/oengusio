package app.oengus.adapter.jpa.mapper;

import app.oengus.adapter.jpa.entity.OpponentEntity;
import app.oengus.domain.submission.Opponent;
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
public interface OpponentMapper {
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "submissionId", source = "submission.id")
    @Mapping(target = "userId", source = "submission.user.id")
    Opponent toDomain(OpponentEntity entity);

    @InheritInverseConfiguration(name = "toDomain")
    OpponentEntity fromDomain(Opponent opponent);
}
