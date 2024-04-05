package app.oengus.adapter.rest.mapper;

import app.oengus.adapter.rest.dto.v2.auth.SignUpDto;
import app.oengus.domain.OengusUser;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, // injection strategy only needed when "uses" is filled
    uses = {
        ConnectionMapper.class,
    }
)
public interface AuthMapper {
    OengusUser toDomain(SignUpDto dto);
}
