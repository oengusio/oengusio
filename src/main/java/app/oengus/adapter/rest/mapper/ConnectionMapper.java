package app.oengus.adapter.rest.mapper;

import app.oengus.adapter.rest.dto.v2.users.ConnectionDto;
import app.oengus.domain.Connection;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConnectionMapper {
    Connection toDomain(ConnectionDto dto);
}
