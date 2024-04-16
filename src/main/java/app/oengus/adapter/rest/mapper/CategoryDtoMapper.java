package app.oengus.adapter.rest.mapper;

import app.oengus.adapter.rest.dto.v1.V1CategoryDto;
import app.oengus.adapter.rest.dto.v2.marathon.CategoryDto;
import app.oengus.domain.submission.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryDtoMapper {
    // TODO: missing opponent.
    CategoryDto fromDomain(Category category);

    V1CategoryDto v1FromDomain(Category category);
}
