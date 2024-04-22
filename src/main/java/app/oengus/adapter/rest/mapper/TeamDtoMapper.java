package app.oengus.adapter.rest.mapper;

import app.oengus.domain.volunteering.Team;
import app.oengus.entity.dto.TeamDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TeamDtoMapper {
    Team toDomain(TeamDto teamDto);

    void applyDTO(@MappingTarget Team team, TeamDto teamDto);
}
