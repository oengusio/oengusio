package app.oengus.adapter.rest.mapper;

import app.oengus.adapter.rest.dto.v1.V1ApplicationDto;
import app.oengus.adapter.rest.dto.v1.request.ApplicationCreateRequestDto;
import app.oengus.domain.volunteering.Application;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    uses = {
        UserDtoMapper.class,
    }
)
public interface ApplicationDtoMapper {
    // TODO embed team and user in domain object.
    V1ApplicationDto fromDomainV1(Application application);

    @Mapping(target = "status", ignore = true)
    void applyPatch(@MappingTarget Application target, V1ApplicationDto source);

    @Mapping(target = "status", ignore = true)
    void applyPatch(@MappingTarget Application target, ApplicationCreateRequestDto source);
}
