package app.oengus.adapter.jpa.mapper;

import app.oengus.domain.Category;
import app.oengus.entity.model.CategoryEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(source = "game.id", target = "gameId")
    Category toDomain(CategoryEntity entity);

    @InheritInverseConfiguration(name = "toDomain")
    CategoryEntity fromDomain(Category category);
}
