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
    }
)
public interface CategoryMapper {
    @Mapping(target = "gameId", source = "game.id")
    Category toDomain(CategoryEntity entity);

    @InheritInverseConfiguration(name = "toDomain")
    CategoryEntity fromDomain(Category category);
}
