package app.oengus.adapter.jpa.mapper;

import app.oengus.domain.Category;
import app.oengus.entity.model.CategoryEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
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
