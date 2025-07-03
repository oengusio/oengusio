package app.oengus.adapter.jpa.mapper;

import app.oengus.adapter.jpa.entity.SavedCategoryEntity;
import app.oengus.domain.user.SavedCategory;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface SavedCategoryEntityMapper {
    @Mapping(target = "gameId", source = "game.id")
    SavedCategory toDomain(SavedCategoryEntity entity);

    @InheritInverseConfiguration(name = "toDomain")
    SavedCategoryEntity fromDomain(SavedCategory savedCategory);
}
