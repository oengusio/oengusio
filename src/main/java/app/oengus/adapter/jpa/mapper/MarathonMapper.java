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
        UserMapper.class,
    }
)
public interface MarathonMapper {
    // TODO: implement these when needed
    @BeanMapping(ignoreUnmappedSourceProperties = { "mastodonValid", "teams", "userInfoHidden", "cleared" })
//    @Mapping(target = "creatorId", source = "creator.id")
    @Mapping(target = "submissionsOpen", source = "submitsOpen")
    @Mapping(target = "discordPrivate", source = "discordPrivacy")
    Marathon toDomain(MarathonEntity entity);

    @Mapping(target = "mastodonValid", ignore = true)
    // TODO: implement these when needed
    @Mapping(target = "cleared", ignore = true)
    @Mapping(target = "userInfoHidden", ignore = true)
    @Mapping(target = "teams", ignore = true)
    @InheritInverseConfiguration(name = "toDomain")
    MarathonEntity fromDomain(Marathon marathon);
}
