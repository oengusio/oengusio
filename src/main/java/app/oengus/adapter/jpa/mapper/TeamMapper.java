package app.oengus.adapter.jpa.mapper;

import app.oengus.domain.volunteering.Team;
import app.oengus.adapter.jpa.entity.TeamEntity;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    uses = {
        UserMapper.class,
    }
)
public interface TeamMapper {
    @Mapping(target = "marathonId", source = "marathon.id")
    @Mapping(target = "size", source = "teamSize")
    Team toDomain(TeamEntity entity);

    @InheritInverseConfiguration(name = "toDomain")
    TeamEntity fromDomain(Team team);
}
