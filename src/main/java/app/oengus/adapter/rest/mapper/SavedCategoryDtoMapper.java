package app.oengus.adapter.rest.mapper;

import app.oengus.adapter.rest.dto.v2.users.savedGames.SavedCategoryCreateDto;
import app.oengus.adapter.rest.dto.v2.users.savedGames.SavedCategoryDto;
import app.oengus.domain.user.SavedCategory;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface SavedCategoryDtoMapper {
    SavedCategoryDto fromDomain(SavedCategory savedCategory);

    SavedCategory toDomain(SavedCategoryDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "gameId", ignore = true)
    SavedCategory createToDomain(SavedCategoryCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void applyPatch(@MappingTarget SavedCategory category, SavedCategoryCreateDto dto);
}
