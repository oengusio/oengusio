package app.oengus.adapter.rest.mapper;

import app.oengus.domain.submission.Selection;
import app.oengus.entity.dto.SelectionDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SelectionDtoMapper {
    Selection toDomain(SelectionDto dto);
}
