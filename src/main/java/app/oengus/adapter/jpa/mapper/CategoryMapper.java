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
    @Mapping(target = "gameId", source = "game.id")
    @Mapping(target = "id", source = "id")
    Category toDomain(CategoryEntity entity);

    @InheritInverseConfiguration(name = "toDomain")
    CategoryEntity fromDomain(Category category);
}
