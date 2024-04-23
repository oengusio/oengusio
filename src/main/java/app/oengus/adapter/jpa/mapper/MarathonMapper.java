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
        QuestionMapper.class,
    }
)
public interface MarathonMapper {
    // TODO: implement these when needed
    @BeanMapping(ignoreUnmappedSourceProperties = { "teams", "userInfoHidden", "cleared" })
    @Mapping(target = "submissionsOpen", source = "submitsOpen")
    @Mapping(target = "discordPrivate", source = "discordPrivacy")
    Marathon toDomain(MarathonEntity entity);

    // TODO: implement these when needed
    @Mapping(target = "cleared", ignore = true)
    @Mapping(target = "userInfoHidden", ignore = true)
    @Mapping(target = "teams", ignore = true)
    @InheritInverseConfiguration(name = "toDomain")
    MarathonEntity fromDomain(Marathon marathon);
}
