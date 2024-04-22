package app.oengus.adapter.rest.mapper;

import app.oengus.domain.PledgeInfo;
import app.oengus.adapter.rest.dto.PatreonStatusDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatreonStatusDtoMapper {
    PledgeInfo toDomain(PatreonStatusDto dto);
}
