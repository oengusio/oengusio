package app.oengus.adapter.jpa.mapper;

import app.oengus.adapter.jpa.entity.MarathonEntity;
import app.oengus.domain.marathon.Marathon;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    uses = {
        //
    }
)
public interface MarathonMapper {
    @Mapping(target = "creatorId", source = "creator.id")
    @Mapping(target = "submissionsOpen", source = "submitsOpen")
    @Mapping(target = "discordPrivate", source = "discordPrivacy")
    @Mapping(target = "mastodonValid", ignore = true)
    Marathon toDomain(MarathonEntity entity);

    @InheritInverseConfiguration(name = "toDomain")
    MarathonEntity fromDomain(Marathon marathon);
}
