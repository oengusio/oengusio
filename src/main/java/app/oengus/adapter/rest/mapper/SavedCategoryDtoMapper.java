package app.oengus.adapter.rest.mapper;

import app.oengus.adapter.rest.dto.v2.users.savedGames.SavedCategoryDto;
import app.oengus.domain.user.SavedCategory;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface SavedCategoryDtoMapper {
    SavedCategoryDto fromDomain(SavedCategory savedCategory);

    SavedCategory toDomain(SavedCategoryDto dto);
}
