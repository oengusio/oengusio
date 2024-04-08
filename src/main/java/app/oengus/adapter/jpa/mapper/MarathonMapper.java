package app.oengus.adapter.jpa.mapper;

import app.oengus.adapter.jpa.entity.MarathonEntity;
import app.oengus.domain.Marathon;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    uses = {
        //
    }
)
public interface MarathonMapper {
    @Mapping(target = "creatorId", source = "creator.id")
    Marathon toDomain(MarathonEntity entity);
}
