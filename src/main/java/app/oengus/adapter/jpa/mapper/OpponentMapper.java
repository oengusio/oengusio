package app.oengus.adapter.jpa.mapper;

import app.oengus.adapter.jpa.entity.OpponentEntity;
import app.oengus.domain.Opponent;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    uses = {
        SubmissionEntityMapper.class
    }
)
public interface OpponentMapper {
    @Mapping(target = "categoryId", source = "category.id")
    Opponent toDomain(OpponentEntity entity);
}
