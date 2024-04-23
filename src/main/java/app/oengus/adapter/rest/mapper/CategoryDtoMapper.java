package app.oengus.adapter.rest.mapper;

import app.oengus.adapter.rest.dto.OpponentCategoryDto;
import app.oengus.adapter.rest.dto.v1.V1CategoryDto;
import app.oengus.adapter.rest.dto.v2.marathon.CategoryDto;
import app.oengus.adapter.rest.dto.v2.simple.SimpleCategoryDto;
import app.oengus.domain.submission.Category;
import app.oengus.domain.submission.Opponent;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    uses = {
        UserDtoMapper.class,
    }
)
public interface CategoryDtoMapper {
    // TODO: missing opponent.
    CategoryDto fromDomain(Category category);

    V1CategoryDto v1FromDomain(Category category);

    @Mapping(target = "user.id", source = "userId")
    OpponentCategoryDto opponentToOpponentCategoryDto(Opponent opponent);

    @Mapping(target = "status", source = "selection.status", defaultValue = "TODO")
    SimpleCategoryDto simpleFromDomain(Category category);
}
