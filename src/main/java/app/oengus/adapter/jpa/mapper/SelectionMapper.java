package app.oengus.adapter.jpa.mapper;

import app.oengus.domain.submission.Selection;
import app.oengus.adapter.jpa.entity.SelectionEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SelectionMapper {
    @Mapping(target = "marathonId", source = "marathon.id")
    @Mapping(target = "categoryId", source = "category.id")
    Selection toDomain(SelectionEntity entity);

    @InheritInverseConfiguration(name = "toDomain")
    SelectionEntity fromDomain(Selection selection);
}
