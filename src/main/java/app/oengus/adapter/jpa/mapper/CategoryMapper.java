package app.oengus.adapter.jpa.mapper;

import app.oengus.domain.Category;
import app.oengus.entity.model.CategoryEntity;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    uses = {
        UserMapper.class,
        OpponentMapper.class,
        GameMapper.class,
        SubmissionEntityMapper.class,
    }
)
public interface CategoryMapper {
    // TODO: implement these when needed
    @BeanMapping(ignoreUnmappedSourceProperties = { "selection", "status", "opponentDtos" })
    @Mapping(target = "gameId", source = "game.id")
    Category toDomain(CategoryEntity entity);

    @Mapping(target = "selection", ignore = true)
    @Mapping(target = "opponentDtos", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "fresh", ignore = true)
    @InheritInverseConfiguration(name = "toDomain")
    CategoryEntity fromDomain(Category category);
}
