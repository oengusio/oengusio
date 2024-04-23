package app.oengus.adapter.security.mapper;

import app.oengus.adapter.security.dto.UserDetailsDto;
import app.oengus.domain.OengusUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// TODO: I do currently not use the details configuration service, should I?
@Mapper(componentModel = "spring")
public interface UserDetailsMapper {
    UserDetailsDto fromUser(OengusUser user);
}
