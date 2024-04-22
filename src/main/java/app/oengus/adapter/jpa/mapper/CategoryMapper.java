package app.oengus.adapter.jpa.mapper;

import app.oengus.domain.submission.Category;
import app.oengus.adapter.jpa.entity.CategoryEntity;
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
        SelectionMapper.class,
    }
)
public interface CategoryMapper {
    // TODO: implement these when needed
    @BeanMapping(ignoreUnmappedSourceProperties = { "status", "opponentDtos" })
    @Mapping(target = "gameId", source = "game.id")
    @Mapping(target = "id", source = "id")
    Category toDomain(CategoryEntity entity);

    @Mapping(target = "opponentDtos", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "fresh", ignore = true)
    @InheritInverseConfiguration(name = "toDomain")
    CategoryEntity fromDomain(Category category);
}
